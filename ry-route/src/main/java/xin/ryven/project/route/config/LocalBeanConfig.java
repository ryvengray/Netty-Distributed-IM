package xin.ryven.project.route.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import xin.ryven.project.common.algorithm.RouteHandler;
import xin.ryven.project.common.algorithm.impl.consistenthash.ConsistentHashHandler;
import xin.ryven.project.common.algorithm.impl.consistenthash.TreeMapConsistentHash;
import xin.ryven.project.common.beans.RedisClient;
import xin.ryven.project.common.spring.RedisProps;

/**
 * @author gray
 */
@Configuration
@Slf4j
public class LocalBeanConfig {

    private final ApplicationProperties applicationProperties;

    @Autowired
    public LocalBeanConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public LoadingCache<String, String> loadingCache() {
        return CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(String s) {
                return null;
            }
        });
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

    @Bean
    public RouteHandler routeHandler() {
        ConsistentHashHandler routeHandler = new ConsistentHashHandler();
        routeHandler.setConsistentHash(new TreeMapConsistentHash());
        return routeHandler;
    }

    @Bean
    public RedisClient redisClient(RedisProps redisProps) {
        return new RedisClient(redisProps);
    }

}