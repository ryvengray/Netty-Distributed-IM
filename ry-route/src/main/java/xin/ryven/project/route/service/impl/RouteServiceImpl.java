package xin.ryven.project.route.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xin.ryven.project.common.algorithm.LruCache;
import xin.ryven.project.common.algorithm.RouteHandler;
import xin.ryven.project.common.beans.RedisClient;
import xin.ryven.project.common.constant.RedisConstant;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.enums.Status;
import xin.ryven.project.common.exception.MsgServerException;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.route.service.RouteService;
import xin.ryven.project.route.tools.ServerMsgUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gray
 */
@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteHandler routeHandler;
    private final RedisClient redisClient;
    private final ServerMsgUtils serverMsg;

    private ExecutorService executorService = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20), new ThreadFactoryBuilder().setNameFormat("Route-Service-%d").build());

    private LruCache<Integer, User> localCache = new LruCache<>(1 << 6);

    @Autowired
    public RouteServiceImpl(RouteHandler routeHandler, RedisClient redisClient, ServerMsgUtils serverMsg) {
        this.routeHandler = routeHandler;
        this.redisClient = redisClient;
        this.serverMsg = serverMsg;
    }

    @Override
    public String routeServer(String value) {
        return routeHandler.route(value);
    }

    @Override
    public void refreshServers(List<String> servers) {
        //处理地址的前部分
        String prefixSeparator = "-";
        servers = servers.stream().map(s -> {
            if (s.contains(prefixSeparator)) {
                return s.split(prefixSeparator)[1];
            }
            return s;
        }).collect(Collectors.toList());
        routeHandler.refreshList(servers);
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

    @Override
    public void offline(Integer userId) {
        redisClient.del(RedisConstant.USER_ADDRESS + userId);
        redisClient.srem(RedisConstant.ACTIVE_USERS, userId.toString());
    }

    @Override
    public void saveUserChannel(Integer userId, ServerAddress serverAddress) {
        redisClient.set(RedisConstant.USER_ADDRESS + userId, JSON.toJSONString(serverAddress));
        redisClient.sadd(RedisConstant.ACTIVE_USERS, userId.toString());
        //加入缓存
        executorService.submit(() -> {
            //触发缓存
            userById(userId, true);
        });
    }

    @Override
    public List<User> onlineUsers() {
        Set<String> members = redisClient.smember(RedisConstant.ACTIVE_USERS);
        return members.stream().map(m -> userById(Integer.valueOf(m))).collect(Collectors.toList());
    }

    private User userById(Integer id) {
        return userById(id, false);
    }

    private User userById(Integer id, boolean refresh) {
        User user = localCache.get(id);
        if (user == null || refresh) {
            String name = redisClient.get(RedisConstant.USER_ID_NAME + id);
            String userStr = redisClient.hget(RedisConstant.USER_NAME_POOL, name);
            user = JSON.parseObject(userStr, User.class);
            user.setPassword(null);
            localCache.put(id, user);
        }
        return user;
    }
}
