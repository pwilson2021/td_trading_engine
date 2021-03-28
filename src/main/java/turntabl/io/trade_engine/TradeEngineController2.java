package turntabl.io.trade_engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.model.order.OrderService;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;

@RestController
public class TradeEngineController2 {
    private OrderService orderService;
    private TradeEngineRabbitMqSender tradeEngineRabbitMqSender;

    @Autowired
    public TradeEngineController2(OrderService orderService, TradeEngineRabbitMqSender tradeEngineRabbitMqSender) {
        this.orderService = orderService;
        this.tradeEngineRabbitMqSender = tradeEngineRabbitMqSender;
    }

    @GetMapping("ultimate/test/{orderId}")
    public void testTradeEngine(@PathVariable("orderId") int order_id) {
       Order order = orderService.findOrder(order_id);
       TradeEngineLogic tradeEngineLogic = new TradeEngineLogic(order, orderService, tradeEngineRabbitMqSender);
       tradeEngineLogic.tradeEngineLogic();
    }
}
