package turntabl.io.trade_engine.publish;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

public class TradePublisher {
    RedisTemplate<?, ?> template;
    ChannelTopic topic;

    public TradePublisher(RedisTemplate<?, ?> template, ChannelTopic topic) {
        this.template = template;
        this.topic = topic;
    }
}
