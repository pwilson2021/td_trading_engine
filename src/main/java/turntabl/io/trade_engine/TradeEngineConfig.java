package turntabl.io.trade_engine;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import turntabl.io.trade_engine.listener.OrderListener;
import turntabl.io.trade_engine.publish.TradePublisher;

@Configuration
public class TradeEngineConfig {
    @Bean
    public JedisConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("localhost");
        configuration.setPort(6379);

        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object > template(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));

        return template;
    }

    @Bean
    public ChannelTopic topic() {return new ChannelTopic("pubsub:message-channel");}

    @Bean
    public MessageListenerAdapter messageListenerAdapter(){
//        pass the receiver object to the MessageListener Adapter
        return new MessageListenerAdapter(new OrderListener());

    }

    @Bean
    TradePublisher redisPublisher(){
        return new TradePublisher(template(),topic());
    }
    @Bean
    public RedisMessageListenerContainer redisContainer(){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.addMessageListener(messageListenerAdapter(), topic());

        return container;
    }

//    rabbit mq config
    @Value("${menu.rabbitmq.queue}")
    String queueName;
    @Value("${menu.rabbitmq.exchange}")
    String exchange;
    @Value("${menu.rabbitmq.key}")
    private String key;

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.username}")
    private String userName;

    @Bean
    Queue queue(){
        return new Queue(queueName, true);
    }
    @Bean
    DirectExchange exchange(){
        return new DirectExchange(exchange);
    }
    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public CachingConnectionFactory rabbitConnectionFactory(){
        CachingConnectionFactory connection = new CachingConnectionFactory();
        connection.setHost(host);
        connection.setPort(5672);
        connection.setUsername(userName);
        connection.setPassword(password);
        return connection;
    }
    @Bean
    Binding binding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(key);
    }
    @Bean
    public AmqpTemplate rabTemplate(){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(rabbitConnectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
