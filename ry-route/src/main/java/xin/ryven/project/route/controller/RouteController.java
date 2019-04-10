package xin.ryven.project.route.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.enums.Status;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.route.feigns.ServerService;
import xin.ryven.project.route.feigns.UserService;

import javax.servlet.http.HttpServletRequest;


/**
 * @author gray
 */
@RestController
public class RouteController {

    private final ServerService serverService;
    private final UserService userService;

    @Autowired
    public RouteController(ServerService serverService, UserService userService) {
        this.serverService = serverService;
        this.userService = userService;
    }

    @PostMapping("login")
    public Resp login(@RequestBody User user) {
        Resp<User> login = userService.login(user);
        if (login.getCode() == Status.OK.getCode()) {
            return login;
        }
        return Resp.status(Status.FAILED, null).setMsg(login.getMsg());
    }

    @PostMapping("msg/send")
    public Resp sendMessage(MsgVo msgVo) {
        return serverService.sendMessage(msgVo);
    }

}
