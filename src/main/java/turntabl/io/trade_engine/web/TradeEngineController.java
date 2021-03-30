package turntabl.io.trade_engine.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import turntabl.io.trade_engine.model.QueueTradeModel;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;

@RestController
public class TradeEngineController {
    private RedisTemplate template;
    private ChannelTopic topic;

    @Autowired
    TradeEngineRabbitMqSender tradeEngineRabbitMqSender;

    @PostMapping
    public String trade(@RequestBody QueueTradeModel trade){
        tradeEngineRabbitMqSender.send(trade);
        template.convertAndSend(topic.getTopic(), trade);
        return "Trade sent to Queue successfully";
    }
}
