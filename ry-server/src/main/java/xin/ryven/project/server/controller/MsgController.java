package xin.ryven.project.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xin.ryven.project.common.enums.Status;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.server.init.RyServer;

import javax.validation.Valid;

/**
 * @author gray
 */
@RestController
@RequestMapping("server/msg")
public class MsgController {

    private final RyServer server;

    @Autowired
    public MsgController(RyServer server) {
        this.server = server;
    }

    @PostMapping("send")
    public Resp sendMessage(@Valid MsgVo msgVo, BindingResult result) {
        if (result.hasErrors()) {
            return Resp.status(Status.FAILED).setMsg(result.getAllErrors().get(0).getDefaultMessage());
        }
        try {
            server.sendMsg(msgVo);
            return Resp.status(Status.OK);
        } catch (RuntimeException e) {
            return Resp.status(Status.FAILED).setMsg(e.getMessage());
        }

    }

}
