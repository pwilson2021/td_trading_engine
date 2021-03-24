package turntabl.io.trade_engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import turntabl.io.trade_engine.model.ExchangeMarketDataModel;
import turntabl.io.trade_engine.model.ExchangeOrder;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.model.order.OrderService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradeEngineLogic {
    private Order order;
    private OrderService orderService;
    private Flux<ExchangeOrder> exchange1Orders;
    private Flux<ExchangeOrder> exchange2Orders;
    private String api_key = "240ccb53-33cf-4453-b4b0-e9ada4a7409d";
    private String exchange1 = "https://exchange.matraining.com/";
    private String exchange2 = "https://exchange2.matraining.com/";
    WebClient client = WebClient.create();


    @Autowired
    public TradeEngineLogic(int order_id, OrderService orderService) {
        this.orderService = orderService;
        this.order = orderService.findOrder(order_id);
        this.exchange1Orders = dynamicFetch(order.getProduct().getTicker(), 1, order.getOrder_type());
        this.exchange2Orders = dynamicFetch(order.getProduct().getTicker(), 2, order.getOrder_type());
    }

    public void tradeEngineLogic () {
        if(order.getOrder_type() == "buy") {
          List<ExchangeOrder> firstSet =  exchange1Orders.toStream().filter(ord ->
                    ord.getPrice() <= order.getPrice()
                  ).collect(Collectors.toList()); //sell prices less than order price
          List<ExchangeOrder> secondSet = exchange1Orders.toStream().filter(ord ->
                  (ord.getPrice() > ord.getPrice()) && ((((ord.getPrice() - ord.getPrice())/ ord.getPrice()) * 100) < 25)
          ).collect(Collectors.toList()); //sell prices a percentage more than the user's buy price
          int firstSetQuantity = firstSet.stream().map(ord -> ord.getQuantity()).reduce(0,Integer::sum);


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
        do {
            int qtyToBuyCheck = orderQuantity - orderSet.get(i).getQuantity();
            int qtyToBuy;
            if (qtyToBuyCheck < 0) {
                qtyToBuy = orderQuantity;
                orderQuantity = 0;
                i++;

                // Make order over here
            } else {
                qtyToBuy = orderSet.get(i).getQuantity();
                orderQuantity -= orderSet.get(i).getQuantity();
                i++;
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
}
