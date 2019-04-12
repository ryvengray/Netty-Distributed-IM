package xin.ryven.project.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("user")
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
            User loginUser = userService.login(user);
            return Resp.status(Status.OK, loginUser);
        } catch (UserException e) {
            log.error("Login failed, {}", e.getMessage());
            Resp<User> resp = Resp.status(Status.FAILED, null);
            resp.setMsg(e.getMessage());
            return resp;
        }

    }

    @PostMapping("register")
    public Resp register(@RequestBody User user) {
        try {
            userService.register(user);
            return Resp.status(Status.OK);
        } catch (UserException e) {
            log.error("Register failed: {}", e.getMessage());
            return Resp.status(Status.FAILED).setMsg(e.getMessage());
        }

    }

}
