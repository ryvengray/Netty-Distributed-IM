package xin.ryven.project.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author gray
 */
@Component
@Getter
public class ApplicationProperties {

    @Value("${zk.address}")
    private String zkAddress;

    @Value("${zk.connect-timeout}")
    private Integer zkConnectTimeout;

    @Value("${zk.client.root}")
    private String zkClientRoot;

    @Value("${zk.client.registry}")
    private Boolean registry;

    @Value("${server.host:}")
    private String serverHost;

    @Value("${server.im-port}")
    private Integer serverImPort;

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${server.heart-beat-time}")
    private Integer heartBeatTime;

    @Value("${rabbitmq.username}")
    private String mqUsername;

    @Value("${rabbitmq.password}")
    private String mqPassword;

    @Value("${rabbitmq.addresses}")
    private String mqAddresses;

    @Value("${rabbitmq.port:5672}")
    private Integer port;

    @Value("${rabbitmq.exchange}")
    private String mqExchange;
}
