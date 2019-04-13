package xin.ryven.project.server.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import xin.ryven.project.common.beans.RedisClient;
import xin.ryven.project.common.spring.RedisProps;

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

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}