package xin.ryven.project.route.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xin.ryven.project.common.algorithm.RouteHandler;
import xin.ryven.project.common.beans.RedisClient;
import xin.ryven.project.common.constant.RedisConstant;
import xin.ryven.project.common.enums.Status;
import xin.ryven.project.common.exception.MsgServerException;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.common.tools.MsgTools;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.route.cache.ServerCache;
import xin.ryven.project.route.config.ApplicationProperties;
import xin.ryven.project.route.service.RouteService;
import xin.ryven.project.route.tools.ServerMsgUtils;

import java.util.List;

/**
 * @author gray
 */
@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteHandler routeHandler;
    private final ServerCache serverCache;
    private final RedisClient redisClient;
    private final ServerMsgUtils serverMsg;

    @Autowired
    public RouteServiceImpl(RouteHandler routeHandler, ServerCache serverCache, RedisClient redisClient, ServerMsgUtils serverMsg) {
        this.routeHandler = routeHandler;
        this.serverCache = serverCache;
        this.redisClient = redisClient;
        this.serverMsg = serverMsg;
    }

    @Override
    public String routeServer(String value) {
        List<String> servers = serverCache.allServer();
        return routeHandler.route(servers, value);
    }

    @Override
    public void sendMessage(MsgVo msgVo) {
        Integer toUserId = msgVo.getToUserId();
        //得到服务器地址
        String addressStr = redisClient.get(RedisConstant.USER_ADDRESS + toUserId);
        if (StringUtils.isEmpty(addressStr)) {
            log.info("user {} offline", toUserId);
            throw new MsgServerException("对方不在线");
        }
        ServerAddress addressVo = JSON.parseObject(addressStr, ServerAddress.class);
        Resp resp = serverMsg.sendMsg(addressVo, msgVo);
        if (resp.getCode() != Status.OK.getCode()) {
            throw new MsgServerException(resp.getMsg());
        }
    }
}
