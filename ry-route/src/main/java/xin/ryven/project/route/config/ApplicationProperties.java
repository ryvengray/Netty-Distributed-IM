package xin.ryven.project.route.config;

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

    @Value("${url.msg.send}")
    private String sendMsgUrl;
}
