package xin.ryven.project.server.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xin.ryven.project.common.beans.RedisClient;
import xin.ryven.project.common.constant.RedisConstant;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.server.config.ApplicationProperties;
import xin.ryven.project.server.service.RouteService;

/**
 * @author gray
 */
@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final ServerAddress serverAddress;

    private final RedisClient redisClient;

    @Autowired
    public RouteServiceImpl(RedisClient redisClient, ApplicationProperties applicationProperties) {
        this.redisClient = redisClient;
        serverAddress = ServerAddress.builder()
                .host(applicationProperties.getServerHost())
                .port(applicationProperties.getServerImPort())
                .httpPort(applicationProperties.getServerPort()).build();
    }

    @Override
    public void offline(Integer userId) {
        redisClient.del(RedisConstant.USER_ADDRESS + userId);
    }

    @Override
    public void saveUserChannel(Integer userId) {
        redisClient.set(RedisConstant.USER_ADDRESS + userId, JSON.toJSONString(serverAddress));
    }
}
