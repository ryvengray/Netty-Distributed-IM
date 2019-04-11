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
    public User login(User user) {
        if (StringUtils.isEmpty(user.getUsername())) {
            throw new UserException("User name can't be null");
        }
        //检查是否重名
        String userStr = redisClient.hget(RedisConstant.USER_NAME_POOL, user.getUsername());
        if (StringUtils.isEmpty(userStr)) {
            log.info("用户不存在 {}", user.getUsername());
            throw new UserException("用户未注册");
        }
        User saveUser = JSON.parseObject(userStr, User.class);
        if (user.getPassword().equals(saveUser.getPassword())) {
            return saveUser;
        } else {
            throw new UserException("密码错误");
        }
    }

    @Override
    public void register(User user) {
        String userStr = redisClient.hget(RedisConstant.USER_NAME_POOL, user.getUsername());
        if (!StringUtils.isEmpty(userStr)) {
            log.info("用户已经注册 {}", userStr);
            throw new UserException("用于已经注册");
        }
        Integer id = Math.toIntExact(redisClient.incr(RedisConstant.INCR_USER_ID));
        user.setUserId(id);
        redisClient.hset(RedisConstant.USER_NAME_POOL, user.getUsername(), JSON.toJSONString(user));
        //备份一份id->name的映射
        redisClient.set(RedisConstant.USER_ID_NAME + id, user.getUsername());
    }
}
