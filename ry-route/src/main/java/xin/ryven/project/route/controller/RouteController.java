package xin.ryven.project.route.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.enums.Status;
import xin.ryven.project.common.exception.MsgServerException;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.route.feigns.UserService;
import xin.ryven.project.route.service.RouteService;


/**
 * @author gray
 */
@RestController
@Slf4j
public class RouteController {

    private final UserService userService;
    private final RouteService routeService;

    @Autowired
    public RouteController(UserService userService, RouteService routeService) {
        this.userService = userService;
        this.routeService = routeService;
    }

    @PostMapping("login")
    public Resp login(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            return Resp.status(Status.FAILED).setMsg("用户名及密码不能为空");
        }
        Resp<User> login = userService.login(user);
        if (login.getCode() == Status.OK.getCode()) {
            return login;
        }
        return Resp.status(Status.FAILED, null).setMsg(login.getMsg());
    }

    @PostMapping("register")
    public Resp register(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            return Resp.status(Status.FAILED).setMsg("用户名及密码不能为空");
        }
        return userService.register(user);
    }

    @GetMapping("serverAddress")
    public Resp<ServerAddress> routeServer(Integer userId, String username) {
        String s = routeService.routeServer(username);
        if (s == null) {
            return Resp.status(Status.FAILED, "没有可用的服务器", null);
        } else {
            String[] splits = s.split(":");
            ServerAddress addressVo = ServerAddress.builder()
                    .protocol("ws:")
                    .host(splits[0])
                    .port(Integer.valueOf(splits[1]))
                    .httpPort(Integer.valueOf(splits[2])).build();
            return Resp.status(Status.OK, addressVo);

        }
    }

    @PostMapping("msg/send")
    public Resp sendMessage(@RequestBody MsgVo msgVo) {
        if (msgVo.getUserId() == null || msgVo.getUsername() == null
                || msgVo.getToUserId() == null || msgVo.getToUsername() == null) {
            log.warn("{} Send Refuse", msgVo);
            return Resp.status(Status.FAILED).setMsg("错误的发送对象");
        }
        try {
            routeService.sendMessage(msgVo);
            return Resp.status(Status.OK);
        } catch (MsgServerException e) {
            log.error("消息发送失败 {}", e.getMessage());
            return Resp.status(Status.FAILED).setMsg(e.getMessage());
        }
    }

}
