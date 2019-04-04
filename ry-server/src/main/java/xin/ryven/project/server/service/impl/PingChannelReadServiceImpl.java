package xin.ryven.project.server.service.impl;

import io.netty.channel.ChannelHandlerContext;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.server.service.ChannelReadService;

/**
 * 处理收到 PING 消息的逻辑
 *
 * @author gray
 */
public class PingChannelReadServiceImpl implements ChannelReadService {
    @Override
    public void process(ChannelHandlerContext ctx, MsgVo vo) {

    }
}
