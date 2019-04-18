package xin.ryven.project.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.server.listener.LoginOutListener;

import java.io.IOException;

/**
 * @author gray
 */
@Configuration
@Slf4j
public class RabbitConfig implements InitializingBean {

    private final ApplicationProperties properties;
    private String queueName;

    @Autowired
    public RabbitConfig(ApplicationProperties properties, ServerAddress serverAddress) {
        this.properties = properties;
        //使用server的地址与端口生成队列名
        this.queueName = "q." + serverAddress.getHost() + "." + serverAddress.getPort();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUsername(properties.getMqUsername());
        factory.setPassword(properties.getMqPassword());
        factory.setPort(properties.getPort());
        factory.setAddresses(properties.getMqAddresses());
        factory.setPublisherConfirms(true);
        return factory;
    }


    /**
     * 多个Server实例想要监听到一个Exchange的消息，同时要满足：
     * -1 每个Server都能够接收到消息
     * -2 Server停止之后对应的队列删除，避免资源的浪费
     * 所以，不能使用固定的队列，否则多个Server实例只会有一个实例收到消息
     * 绑定动态的队列，设置队列的exclusive为true，当前的连接断开就删除对应
     */
    @Override
    public void afterPropertiesSet() throws IOException {

        //创建queue
        ConnectionFactory factory = connectionFactory();

        factory.createConnection().createChannel(false).exchangeDeclare(properties.getMqExchange(), ExchangeTypes.FANOUT);

        factory.createConnection().createChannel(false).queueDeclare(queueName, false, true, true, null);
        factory.createConnection().createChannel(false).queueBind(queueName, properties.getMqExchange(), queueName);
    }

    /**
     * 手动添加Listener，由于queueName是动态的
     */
    @Bean
    public SimpleMessageListenerContainer container(LoginOutListener loginOutListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueueNames(queueName);
        container.setExposeListenerChannel(true);
        container.setMessageListener(loginOutListener);
        return container;
    }
}
