package xin.ryven.project.route.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.http.Resp;

/**
 * @author gray
 */
@FeignClient(value = "im-user")
@Component
public interface UserService {
    /**
     * Register
     *
     * @param user username/password
     * @return User
     */
    @PostMapping("user/register")
    Resp register(@RequestBody User user);

    /**
     * 登录
     *
     * @param user 用户，包含username
     * @return 用户
     */
    @PostMapping("user/login")
    Resp<User> login(@RequestBody User user);
}
