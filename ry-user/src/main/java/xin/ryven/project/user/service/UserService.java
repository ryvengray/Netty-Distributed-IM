package xin.ryven.project.user.service;

import xin.ryven.project.common.entity.User;

/**
 * @author gray
 */
public interface UserService {

    /**
     * 登录
     *
     * @param user username username/password
     * @return user
     */
    User login(User user);

    /**
     * 注册
     *
     * @param user 用户名、密码
     */
    void register(User user);
}
