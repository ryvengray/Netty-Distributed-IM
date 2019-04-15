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
}
