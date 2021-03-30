package turntabl.io.trade_engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import turntabl.io.trade_engine.ApplicationContextProvider;
import turntabl.io.trade_engine.TradeEngineLogic;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.model.order.OrderService;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;


import java.io.IOException;

@Component
public class OrderListener implements MessageListener {
//    @Autowired
//    OrderService orderService;

//
//    public OrderListener(OrderService orderService) {
//        this.orderService = orderService;
//    }

    @Autowired
    private TradeEngineLogic tradeEngineLogic;

    public OrderListener(TradeEngineLogic tradeEngineLogic) {
        this.tradeEngineLogic = tradeEngineLogic;
    }

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msg = mapper.readValue(message.getBody(),String.class);
            System.out.println("order validated. orderId:  "+msg);
            int orderId = Integer.parseInt(msg);
            tradeEngineLogic.setOrder(orderId);
            tradeEngineLogic.tradeEngineLogic();
            //orderService.testTradeEngineController(orderId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
