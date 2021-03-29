package turntabl.io.trade_engine.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import turntabl.io.trade_engine.TradeEngineLogic;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.model.order.OrderService;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;

import java.util.List;

@Component
public class CheckOrderStatus {
    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeEngineRabbitMqSender tradeEngineRabbitMqSender;

    private static final Logger log = LoggerFactory.getLogger(CheckOrderStatus.class);

    @Scheduled(fixedRate = 10000)
    public void handleOrders () {
        System.out.println("Scheduled tasks have begun");
        List<Order> orderList = orderService.findIncompleteOrders();
        System.out.println(orderList.size());
        orderList.forEach( order -> {
            TradeEngineLogic tradeEngineLogic = new TradeEngineLogic();
            tradeEngineLogic.tradeEngineLogic(order);
        });
    }
}
