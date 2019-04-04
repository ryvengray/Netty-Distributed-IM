package xin.ryven.project.server.service;

import io.netty.channel.ChannelHandlerContext;
import xin.ryven.project.common.vo.MsgVo;

/**
 * channel收到消息的处理类
 * 实现类有多个
 *
 * @author gray
 */
public interface ChannelReadService {

    /**
     * 处理消息
     *
     * @param vo 收到的消息
     */
    void process(ChannelHandlerContext ctx, MsgVo vo);
}
