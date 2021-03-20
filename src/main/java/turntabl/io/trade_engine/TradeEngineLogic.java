package turntabl.io.trade_engine;

import org.springframework.beans.factory.annotation.Autowired;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.model.order.OrderService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeEngineLogic {
    private Order order;
    private OrderService orderService;
    private List<Map> exchangeOrders = new ArrayList<>();

    @Autowired
    public TradeEngineLogic(int order_id, OrderService orderService) {
        this.orderService = orderService;
        this.order = orderService.findOrder(order_id);
    }

    public void tradeEngineLogic () {
        if(order.getOrder_type() == "buy") {
          List<Map> firstSet =  exchangeOrders.stream().filter(exc_ord -> ); //sell prices less than order price
          List<Map> secondSet = exchangeOrders.stream().filter(exc_ord -> ); //sell prices a percentage more than the user's buy price
          int firstSetQuantity = firstSet.stream().reduce( );


          if (firstSetQuantity >= order.getQuantity()) {
//              int orderQuantity = order.getQuantity();
//              int i = 0;

//              do {
//                  int qtyToBuyCheck = orderQuantity - firstSet[i].quantity;
//                  int qtyToBuy;
//                  if (qtyToBuyCheck < 0) {
//                      qtyToBuy = orderQuantity;
//                      orderQuantity = 0;
//                      // Make order over here
//                  } else {
//                      qtyToBuy = firstSet[i].quantity;
//                      orderQuantity -= firstSet[i].quantity;
//                      i++;
//                  }
//              } while (orderQuantity != 0);
              splitOrder(order, firstSet);
          } else {
              List<Map> mergedSet = new ArrayList<>();
              mergedSet.addAll(firstSet);
              mergedSet.addAll(secondSet);

              splitOrder(order, mergedSet);
          }

        }
    }

    public void splitOrder (Order order, List <Map> orderSet) {
        int orderQuantity = order.getQuantity();
        int i = 0;
        do {
            int qtyToBuyCheck = orderQuantity - orderSet[i].quantity;
            int qtyToBuy;
            if (qtyToBuyCheck < 0) {
                qtyToBuy = orderQuantity;
                orderQuantity = 0;
                // Make order over here
            } else {
                qtyToBuy = orderSet[i].quantity;
                orderQuantity -= orderSet[i].quantity;
                i++;
            }
        } while (orderQuantity != 0);
    }

}
