package xin.ryven.project.server.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gray
 */
@Configuration
public class LocalBeanConfig {

    private final ApplicationProperties applicationProperties;

    @Autowired
    public LocalBeanConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(applicationProperties.getZkAddress(),
                applicationProperties.getZkConnectTimeout());
    }

}