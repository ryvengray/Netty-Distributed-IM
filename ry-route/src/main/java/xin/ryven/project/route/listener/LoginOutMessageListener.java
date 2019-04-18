package xin.ryven.project.route.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import xin.ryven.project.common.enums.MsgType;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.feign.SaveUserServer;
import xin.ryven.project.route.service.RouteService;

/**
 * @author gray
 */
@Component
@Slf4j
public class LoginOutMessageListener {

    private final RouteService routeService;

    @Autowired
    public LoginOutMessageListener(RouteService routeService) {
        this.routeService = routeService;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue("logout.message"), exchange = @Exchange(value = "login.out.exchange", type = ExchangeTypes.FANOUT, durable = "false")))
    public void message(@Payload String data) {
        try {
            MsgVo msgVo = JSON.parseObject(data, MsgVo.class);
            if (MsgType.NOTIFY_LOGOUT.getType() == msgVo.getType()) {
                //登出消息
                routeService.offline(msgVo.getUserId());
            } else if (MsgType.NOTIFY_LOGIN.getType() == msgVo.getType()) {
                String content = msgVo.getContent();
                SaveUserServer saveUserServer = JSON.parseObject(content, SaveUserServer.class);
                routeService.saveUserChannel(saveUserServer.getUserId(), saveUserServer.getServerAddress());
            } else {
                log.warn("未知消息 {}", data);
            }
        } catch (Exception e) {
            log.error("消息处理失败 {}", data);
        }


    }

}
