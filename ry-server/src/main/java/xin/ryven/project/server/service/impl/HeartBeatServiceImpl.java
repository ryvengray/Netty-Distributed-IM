package xin.ryven.project.server.service.impl;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xin.ryven.project.common.service.HeartBeatService;
import xin.ryven.project.server.config.ApplicationProperties;
import xin.ryven.project.server.holder.NettyAttrHolder;

/**
 * @author gray
 */
@Service
@Slf4j
public class HeartBeatServiceImpl implements HeartBeatService {

    private final int heartBeatTime;

    @Autowired
    public HeartBeatServiceImpl(ApplicationProperties properties) {
        this.heartBeatTime = properties.getHeartBeatTime();
    }

    @Override
    public void process(ChannelHandlerContext ctx) {
        long lastReadTime = NettyAttrHolder.getReadTime(ctx);
        if (System.currentTimeMillis() - lastReadTime > heartBeatTime) {
            log.info("{} heart beat timeout, shutdown", NettyAttrHolder.getUser(ctx));
            ctx.channel().close();
        } else {
            ctx.writeAndFlush(new PingWebSocketFrame()).addListener((ChannelFutureListener) f -> {
                if (!f.isSuccess()) {
                    log.warn("Send ping to {} failed, close channel", NettyAttrHolder.getUser(ctx));
                    ctx.close();
                }
            });
        }
    }

}
