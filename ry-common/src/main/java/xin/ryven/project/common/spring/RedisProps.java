package xin.ryven.project.common.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author gray
 */
@Getter
@Setter
@Component
public class RedisProps {

    @Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.database:0}")
    private int database;

}
