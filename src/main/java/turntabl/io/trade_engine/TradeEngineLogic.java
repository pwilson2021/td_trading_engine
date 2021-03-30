package turntabl.io.trade_engine;


import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import turntabl.io.trade_engine.model.ExchangeMarketDataModel;
import turntabl.io.trade_engine.model.ExchangeOrder;
import turntabl.io.trade_engine.model.QueueTradeModel;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.model.order.OrderService;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TradeEngineLogic {
    private Order order;
    public Flux<ExchangeOrder> exchange1Orders;
    public Flux<ExchangeOrder> exchange2Orders;
    private final String api_key = "240ccb53-33cf-4453-b4b0-e9ada4a7409d";
    private String exchange1 = "https://exchange.matraining.com/";
    private String exchange2 = "https://exchange2.matraining.com/";
    private String ord_type1 ="buy";
    private String ord_type2 ="sell";

    @Autowired
    private TradeEngineRabbitMqSender tradeEngineRabbitMqSender;
    @Autowired
    private OrderService orderService;

    WebClient client = WebClient.create();

    public TradeEngineLogic() {

    }

//    public void setOrderService(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    public void setTradeEngineRabbitMqSender(TradeEngineRabbitMqSender tradeEngineRabbitMqSender) {
//        this.tradeEngineRabbitMqSender = tradeEngineRabbitMqSender;
//    }


    public void setOrder(int orderId) {
        System.out.println(tradeEngineRabbitMqSender);
        this.order = orderService.findOrder(orderId);
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void tradeEngineLogic () {
//        this.order = order;
        String ord_type = order.getOrder_type().equals(ord_type1) ? ord_type2 : ord_type1;
        this.exchange1Orders = dynamicFetch(order.getProduct().getTicker(), 1, ord_type);
        this.exchange2Orders = dynamicFetch(order.getProduct().getTicker(), 2, ord_type);
        System.out.println("Starting");
        if(order.getOrder_type().equals("buy")) {
            List<ExchangeOrder> firstSet =  exchange1Orders.toStream().filter(ord -> ord.getPrice() <= order.getPrice())
                      .sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList()); //sell prices less than order price;

            List<ExchangeOrder> secondSet = exchange1Orders.toStream().filter(ord -> (ord.getPrice() > order.getPrice()) && ((((ord.getPrice() - order.getPrice())/ ord.getPrice()) * 100) < 20))
                      .sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList()); //sell prices a percentage more than the user's buy price

            List<ExchangeOrder> thirdSet =  exchange2Orders.toStream().filter(ord -> ord.getPrice() <= order.getPrice())
                    .sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList()); //sell prices less than order price;

            List<ExchangeOrder> fourthSet = exchange2Orders.toStream().filter(ord -> (ord.getPrice() > order.getPrice()) && ((((ord.getPrice() - order.getPrice())/ ord.getPrice()) * 100) < 20))
                    .sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList()); //sell prices a percentage more than the user's buy price

            firstSet.forEach( ord -> ord.setExchangeId(1));
            secondSet.forEach(ord -> ord.setExchangeId(1));
            thirdSet.forEach(ord -> ord.setExchangeId(2));
            fourthSet.forEach(ord -> ord.setExchangeId(2));

            int firstAndThirdSetQuantity =
                    firstSet.stream().map(ord -> ord.getQuantity()).reduce(0, Integer::sum)
                    + thirdSet.stream().map(ord -> ord.getQuantity()).reduce(0,Integer::sum);
            //secondSet.forEach(s -> System.out.println(s.toString()));
            System.out.println(firstAndThirdSetQuantity);
            if (firstAndThirdSetQuantity >= order.getQuantity()) {
                List<ExchangeOrder> mergedSet = new ArrayList<>();
                mergedSet.addAll(firstSet);
                mergedSet.addAll(thirdSet);
                mergedSet.stream().sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList());
                splitOrder(order, mergedSet);
            } else {
                  List<ExchangeOrder> mergedSet = new ArrayList<>();
                  mergedSet.addAll(firstSet);
                  mergedSet.addAll(secondSet);
                  mergedSet.addAll(thirdSet);
                  mergedSet.addAll(fourthSet);
                  mergedSet.stream().sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList());
                  splitOrder(order, mergedSet);
            }
        } else {
            ExchangeMarketDataModel fetchExchange1MD = fetchMarketData(1, order.getProduct().getTicker()).block();
            ExchangeMarketDataModel fetchExchange2MD =  fetchMarketData(2, order.getProduct().getTicker()).block();

            double firstExchangeCheck = order.getPrice() - fetchExchange1MD.getBid_price() ;
            double secondExchangeCheck = order.getPrice() - fetchExchange2MD.getBid_price();

            int exchangeId = (Math.abs(firstExchangeCheck) < Math.abs(secondExchangeCheck)) ? 1 : 2;
            QueueTradeModel queueTradeModel = new QueueTradeModel(order.getProduct().getTicker(),
                    order.getQuantity(), order.getPrice(), order.getOrder_type(), exchangeId, order.getId() );
            System.out.println(queueTradeModel.toString());
            tradeEngineRabbitMqSender.send(queueTradeModel);
        }
    }

    public void splitOrder (Order order, List <ExchangeOrder> orderSet) {
        int quantityFulfilled = (order.getQtyFulfilled() != null) ? order.getQtyFulfilled() : 0;
        int orderQuantity = order.getQuantity() - quantityFulfilled;
        int i = 0;
        if (orderSet.size() == 0 || orderQuantity == 0) {
            System.out.println("OrderSet is Empty or Order has been fullfilled already");
            orderService.updateOrder(order.getId(), null, 0, "hold",0);
            return;
        }

        do {
            int qtyToBuyCheck = orderQuantity - orderSet.get(i).getQuantity();
            int qtyToBuy;
            if (qtyToBuyCheck < 0) {
                qtyToBuy = orderQuantity;
                orderQuantity = 0;
                // Make order over here
                QueueTradeModel queueTradeModel = new QueueTradeModel(order.getProduct().getTicker(), qtyToBuy, orderSet.get(i).getPrice(), order.getOrder_type(), orderSet.get(i).getExchangeId(), order.getId());
                System.out.println(queueTradeModel.toString());
                orderService.updateOrder(order.getId(),null ,0 , "completed", order.getQuantity());
                tradeEngineRabbitMqSender.send(queueTradeModel);
            } else {
                qtyToBuy = orderSet.get(i).getQuantity();
                orderQuantity -= orderSet.get(i).getQuantity();
                QueueTradeModel queueTradeModel = new QueueTradeModel(order.getProduct().getTicker(), qtyToBuy, orderSet.get(i).getPrice(), order.getOrder_type(), orderSet.get(i).getExchangeId(), order.getId());
                System.out.println(queueTradeModel.toString());
                tradeEngineRabbitMqSender.send(queueTradeModel);
                quantityFulfilled += qtyToBuy;
            }
            i++;
        } while (orderQuantity != 0 && i < orderSet.size());

        if (orderQuantity > 0) {
            orderService.updateOrder(order.getId(), null, 0, "partially_fulfilled", quantityFulfilled);
        }

    }

    private Flux<ExchangeOrder> dynamicFetch(
            String ticker,
            int exchangeId,
            String side
    ) {
        if (exchangeId == 1) {
            return client.get().uri(exchange1+"orderbook/"+ticker+"/"+side).retrieve().bodyToFlux(ExchangeOrder.class);
        } else {
            return client.get().uri(exchange2+"orderbook/"+ticker+"/"+side).retrieve().bodyToFlux(ExchangeOrder.class);
        }
    }

    private Mono<ExchangeMarketDataModel> fetchMarketData(int exchangeId, String ticker) {
        String exchange =  (exchangeId == 1 ) ? exchange1 : exchange2;
        return client.get().uri(exchange+"/md/"+ticker).retrieve().bodyToMono(ExchangeMarketDataModel.class);
    }
}
