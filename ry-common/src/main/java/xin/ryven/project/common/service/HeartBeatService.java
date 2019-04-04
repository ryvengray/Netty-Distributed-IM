package xin.ryven.project.common.service;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author gray
 */
public interface HeartBeatService {

    /**
     * 心跳处理
     */
    void process(ChannelHandlerContext ctx);

}
