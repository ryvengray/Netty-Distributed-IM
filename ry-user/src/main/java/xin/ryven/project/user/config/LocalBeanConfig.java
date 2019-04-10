package xin.ryven.project.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xin.ryven.project.common.beans.RedisClient;
import xin.ryven.project.common.spring.RedisProps;

/**
 * @author gray
 */
@Configuration
public class LocalBeanConfig {

    @Bean
    public RedisClient redisClient(RedisProps redisProps) {
        return new RedisClient(redisProps);
    }

}
