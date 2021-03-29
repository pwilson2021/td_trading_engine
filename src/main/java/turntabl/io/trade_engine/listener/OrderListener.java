package turntabl.io.trade_engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import turntabl.io.trade_engine.model.order.Order;
import turntabl.io.trade_engine.model.order.OrderService;


import java.io.IOException;

public class OrderListener implements MessageListener {

    @Autowired
    OrderService orderService;

    public OrderListener(OrderService orderService) {
        this.orderService = orderService;
    }

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            String msg = mapper.readValue(message.getBody(),String.class);
            System.out.println("order validated. orderId:  "+msg);
            int orderId = Integer.parseInt(msg);
            orderService.testTradeEngineController(orderId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
