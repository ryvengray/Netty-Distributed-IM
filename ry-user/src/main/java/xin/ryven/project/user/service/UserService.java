package xin.ryven.project.user.service;

import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.exception.UserException;

/**
 * @author gray
 */
public interface UserService {

    /**
     * 登录
     *
     * @param user username username/password
     * @return user
     * @throws UserException something wrong
     */
    User login(User user) throws UserException;

    /**
     * 注册
     *
     * @param user 用户名、密码
     * @throws UserException something wrong
     */
    void register(User user) throws UserException;
}
