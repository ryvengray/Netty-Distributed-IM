package xin.ryven.project.common.beans;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import xin.ryven.project.common.spring.RedisProps;

/**
 * @author gray
 */
@Slf4j
public class RedisClient {

    private JedisPool jedisPool;

    public RedisClient(RedisProps redisProps) {
        this.jedisPool = new JedisPool(
                new GenericObjectPoolConfig(),
                redisProps.getHost(),
                redisProps.getPort(),
                Protocol.DEFAULT_TIMEOUT,
                redisProps.getPassword(),
                redisProps.getDatabase()
        );
    }

    public boolean set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()){
            return "OK".equals(jedis.set(key, value));
        }
    }

    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()){
             jedis.del(key);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()){
            return jedis.get(key);
        }
    }

    public String hget(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()){
            return jedis.hget(key, field);
        }
    }

    public void hset(String key, String field, String value) {
        try (Jedis jedis = jedisPool.getResource()){
            jedis.hset(key, field, value);
        }
    }

    public Long incr(String key) {
        try (Jedis jedis = jedisPool.getResource()){
            return jedis.incr(key);
        }
    }
}
