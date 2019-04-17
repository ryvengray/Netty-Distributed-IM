package xin.ryven.project.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xin.ryven.project.common.spring.SpringBeanUtils;
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


    @Override
    public void afterPropertiesSet() throws IOException {

        //创建queue
        ConnectionFactory factory = connectionFactory();
        factory.createConnection().createChannel(false).queueDeclare(queueName, false, true, true, null);
        factory.createConnection().createChannel(false).queueBind(queueName, properties.getMqExchange(), queueName);

        //设置监听

    }

    @Bean
    public SimpleMessageListenerContainer container(LoginOutListener loginOutListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueueNames(queueName);
        container.setExposeListenerChannel(true);
        container.setMessageListener(loginOutListener);
        return container;
    }
}
