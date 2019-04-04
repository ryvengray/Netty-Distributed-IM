package xin.ryven.project.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import xin.ryven.project.common.enums.MsgType;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.server.holder.ChannelReadHolder;
import xin.ryven.project.server.holder.NettyAttrHolder;
import xin.ryven.project.server.holder.SocketHolder;
import xin.ryven.project.server.service.ChannelReadService;

/**
 * @author gray
 */
@Slf4j
@ChannelHandler.Sharable
public class RyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("{} offline", NettyAttrHolder.getUser(ctx));
        SocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            //空闲未读取到消息
            log.info("{} read idle event triggered", NettyAttrHolder.getUser(ctx));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        String message = msg.toString(CharsetUtil.UTF_8).trim();
        try {
            //接收到消息，更新最后读取的时间
            NettyAttrHolder.updateReadTime(ctx);
            MsgVo msgVo = JSON.parseObject(message, MsgVo.class);
            //获取实际的处理类
            ChannelReadService readService = ChannelReadHolder.readService(MsgType.type(msgVo.getType()));
            //处理消息
            readService.process(ctx, msgVo);
        } catch (JSONException e) {
            log.error("未知的消息 {}", message, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("异常", cause);
        //关闭连接
        ctx.channel().close();
        SocketHolder.remove((NioSocketChannel) ctx.channel());
    }

}
