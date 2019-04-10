package xin.ryven.project.user.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xin.ryven.project.common.beans.RedisClient;
import xin.ryven.project.common.constant.RedisConstant;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.exception.UserException;
import xin.ryven.project.user.service.UserService;

/**
 * @author gray
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final RedisClient redisClient;

    @Autowired
    public UserServiceImpl(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public User login(String userName) {
        if (StringUtils.isEmpty(userName)) {
            throw new UserException("User name can't be null");
        }
        //检查是否重名
        String userId = redisClient.hget(RedisConstant.USER_NAME_POOL, userName);
        if (userId != null) {
            log.info("已被使用昵称 {}", userName);
            throw new UserException("昵称已被使用");
        }
        Integer id = Math.toIntExact(redisClient.incr(RedisConstant.INCR_USER_ID));
        User user = new User(id, userName);
        redisClient.set(RedisConstant.USER_PRE + id, JSON.toJSONString(user));
        redisClient.hset(RedisConstant.USER_NAME_POOL, userName, id.toString());
        return user;
    }
}
