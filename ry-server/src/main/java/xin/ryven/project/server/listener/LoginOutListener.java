package xin.ryven.project.server.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.server.init.RyServer;

/**
 * @author gray
 */
@Component
@Slf4j
public class LoginOutListener implements ChannelAwareMessageListener {

    private final RyServer ryServer;

    @Autowired
    public LoginOutListener(RyServer ryServer) {
        this.ryServer = ryServer;
    }

    @Override
    public void onMessage(Message message, Channel channel) {
        String msg = new String(message.getBody());
        //登入登出消息
        try {
            MsgVo msgVo = JSON.parseObject(msg, MsgVo.class);
            //Clear server information
            msgVo.setContent(null);
            ryServer.notifyAll(JSON.toJSONString(msgVo));
        } catch (Exception e) {
            log.error("mq消息处理失败 :{}", msg, e);
        }
    }

}
