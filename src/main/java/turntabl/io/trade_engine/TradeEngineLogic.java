package turntabl.io.trade_engine;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import turntabl.io.trade_engine.model.ExchangeOrder;
import turntabl.io.trade_engine.model.QueueTradeModel;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TradeEngineLogic {
    private Order order;
    private Flux<ExchangeOrder> exchange1Orders;
    private Flux<ExchangeOrder> exchange2Orders;
    private String api_key = "240ccb53-33cf-4453-b4b0-e9ada4a7409d";
    private String exchange1 = "https://exchange.matraining.com/";
    private String exchange2 = "https://exchange2.matraining.com/";
    private String ord_type1 ="buy";
    private String ord_type2 ="sell";


    private TradeEngineRabbitMqSender tradeEngineRabbitMqSender;


    WebClient client = WebClient.create();

    public TradeEngineLogic(Order order, TradeEngineRabbitMqSender tradeEngineRabbitMqSender) {
        this.order = order;
        String ord_type = order.getOrder_type().equals(ord_type1) ? ord_type2 : ord_type1;
        this.exchange1Orders = dynamicFetch(order.getProduct().getTicker(), 1, ord_type);
        this.exchange2Orders = dynamicFetch(order.getProduct().getTicker(), 2, ord_type);
        this.tradeEngineRabbitMqSender = tradeEngineRabbitMqSender;
    }

    public TradeEngineLogic() {
    }


    public void tradeEngineLogic () {
        System.out.println("Starting");

        // System.out.println(order.getOrder_type().equals("buy"));
        if(order.getOrder_type().equals("buy")) {
            System.out.println("testing 1");
              List<ExchangeOrder> firstSet =  exchange1Orders.toStream().filter(ord ->
                        ord.getPrice() <= order.getPrice()
                      ).sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList()); //sell prices less than order price;
//            exchange1Orders.toStream().forEach(ord ->
//                    //System.out.println("Percentage");
//                    System.out.println(((((ord.getPrice() - order.getPrice())/ ord.getPrice()) * 100))));
              List<ExchangeOrder> secondSet = exchange1Orders.toStream().filter(ord ->
                      (ord.getPrice() > order.getPrice()) && ((((ord.getPrice() - order.getPrice())/ ord.getPrice()) * 100) < 2000)
              ).sorted((ord1, ord2) -> order.getPrice().compareTo(ord2.getPrice())).collect(Collectors.toList()); //sell prices a percentage more than the user's buy price

            int firstSetQuantity = firstSet.stream().map(ord -> ord.getQuantity()).reduce(0,Integer::sum);

              //System.out.println(firstSetQuantity);
              secondSet.forEach(s -> System.out.println(s.toString()));
              if (firstSetQuantity >= order.getQuantity()) {
                  splitOrder(order, firstSet);
              } else {
                  List<ExchangeOrder> mergedSet = new ArrayList<>();
                  mergedSet.addAll(firstSet);
                  mergedSet.addAll(secondSet);
                  splitOrder(order, mergedSet);
              }

        }
    }

    public void splitOrder (Order order, List <ExchangeOrder> orderSet) {
        int orderQuantity = order.getQuantity();
        int i = 0;
        if (orderSet.size() == 0) {
            System.out.println("OrderSet is Empty");
            return;
        }

        do {
            int qtyToBuyCheck = orderQuantity - orderSet.get(i).getQuantity();
            int qtyToBuy;
            if (qtyToBuyCheck < 0) {
                qtyToBuy = orderQuantity;
                orderQuantity = 0;
                // Make order over here
                QueueTradeModel queueTradeModel = new QueueTradeModel(order.getProduct().getTicker(), qtyToBuy, orderSet.get(i).getPrice(), order.getOrder_type(), 1);
                System.out.println(queueTradeModel.toString());
                tradeEngineRabbitMqSender.send(queueTradeModel);
            } else {
                qtyToBuy = orderSet.get(i).getQuantity();
                orderQuantity -= orderSet.get(i).getQuantity();
                i++;
                QueueTradeModel queueTradeModel = new QueueTradeModel(order.getProduct().getTicker(), qtyToBuy, orderSet.get(i).getPrice(), order.getOrder_type(), 1);
                System.out.println(queueTradeModel.toString());
                tradeEngineRabbitMqSender.send(queueTradeModel);
            }
        } while (orderQuantity != 0);
    }

    public Flux<ExchangeOrder> dynamicFetch(
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

    @Override
    public String toString() {
        return "TradeEngineLogic{" +
                "order=" +
                ", exchange1Orders=" + exchange1Orders +
                ", exchange2Orders=" + exchange2Orders +
                '}';
    }
}
