package xin.ryven.project.server.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.enums.MsgType;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.server.holder.NettyAttrHolder;
import xin.ryven.project.server.holder.SocketHolder;
import xin.ryven.project.server.init.RyServer;
import xin.ryven.project.server.service.ChannelReadService;

/**
 * 处理登录的逻辑
 *
 * @author gray
 */
@Slf4j
public class LoginChannelReadServiceImpl implements ChannelReadService {

    @Override
    public void process(ChannelHandlerContext ctx, MsgVo msgVo) {
        Integer userId = msgVo.getUserId();
        User user = new User(userId, msgVo.getUserName());
        //检查userId是否注册了channel
        NioSocketChannel oldChannel = SocketHolder.channel(userId);
        if (oldChannel != null && oldChannel != ctx.channel()) {
            log.warn("{} 已经登录，关闭已有的channel", user);
            RyServer ryServer = SpringBeanUtils.getBean(RyServer.class);
            ryServer.offline(oldChannel);
        }
        //保存channel信息
        SocketHolder.save(userId, (NioSocketChannel) ctx.channel());
        //保存用户信息
        NettyAttrHolder.saveUser(ctx, user);
        log.info("{} login success", user);
        //返回成功消息
        MsgVo ret = MsgVo.builder().content("OK").type(MsgType.LOGIN.getType()).build();
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ret)))
                .addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.warn("Send login message to {} failed, close channel.", userId);
                        future.channel().close();
                    }
                });
    }

}
