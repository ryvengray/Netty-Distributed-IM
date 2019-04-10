package xin.ryven.project.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.enums.Status;
import xin.ryven.project.common.exception.UserException;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.user.service.UserService;

/**
 * @author gray
 */
@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public Resp<User> login(@RequestBody User user) {
        try {
            User loginUser = userService.login(user.getUsername());
            return Resp.status(Status.OK, loginUser);
        } catch (UserException e) {
            log.error("Login failed", e);
            Resp<User> resp = Resp.status(Status.FAILED, null);
            resp.setMsg(e.getMessage());
            return resp;
        }

    }

}
