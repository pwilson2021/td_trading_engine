package turntabl.io.trade_engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import turntabl.io.trade_engine.model.order.Order;

import java.io.IOException;

public class OrderListener implements MessageListener {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            Order msg = mapper.readValue(message.getBody(),Order.class);
//            you can do whatever now with the validated order received;
//            change the Order type to SoapOrder as seen in OVS
//            then u can now create a new order from it before saving it into the db
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
