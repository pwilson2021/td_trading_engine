package turntabl.io.trade_engine.publish;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import turntabl.io.trade_engine.model.QueueTradeModel;

@Service
public class TradeEngineRabbitMqSender {

    @Autowired
    private AmqpTemplate rabTemplate;

    @Value("${trade.engine.rabbitmq.exchange}")
    private String exchange;
    @Value("${trade.engine.rabbitmq.key}")
    private String routingkey;

    public void send(QueueTradeModel trade){
        rabTemplate.convertAndSend(exchange, routingkey,trade);
        System.out.println("Send message =  " + trade);
    }
}
