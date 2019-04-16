package xin.ryven.project.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import xin.ryven.project.common.enums.MsgType;
import xin.ryven.project.common.service.HeartBeatService;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.server.holder.ChannelReadHolder;
import xin.ryven.project.server.holder.NettyAttrHolder;
import xin.ryven.project.server.holder.SocketHolder;
import xin.ryven.project.server.init.RyServer;
import xin.ryven.project.server.service.ChannelReadService;

/**
 * @author gray
 */
@Slf4j
@ChannelHandler.Sharable
public class RyServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("{} offline", NettyAttrHolder.getUser(ctx));
        //下线
        SpringBeanUtils.getBean(RyServer.class).offline((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            //空闲未读取到消息
            HeartBeatService heartBeatService = SpringBeanUtils.getBean(HeartBeatService.class);
            heartBeatService.process(ctx);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttp(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketMessage(ctx, (WebSocketFrame) msg);
        } else {
            log.warn("未知的消息", msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("异常", cause);
        //关闭连接
        ctx.channel().close();
        SocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    /**
     * 处理websocket消息
     *
     * @param ctx            ctx
     * @param webSocketFrame webSocketFrame
     */
    private void handleWebSocketMessage(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        //关闭连接的命令
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            //关闭channel
            ctx.channel().close();
        } else if (webSocketFrame instanceof PingWebSocketFrame) {
            //Ping消息
            ctx.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            log.info("{} Pong", NettyAttrHolder.getUser(ctx));
        } else if (webSocketFrame instanceof PongWebSocketFrame) {
            //ping客户端的返回pong
            NettyAttrHolder.updateReadTime(ctx);
        } else if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            log.warn("不支持的消息 {}", webSocketFrame.content().toString(CharsetUtil.UTF_8));
            throw new UnsupportedOperationException("不支持 TextWebSocketFrame 以外的内容");
        } else {
            String message = webSocketFrame.content().toString(CharsetUtil.UTF_8);
            log.info("Receive msg: {}", message);
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

    }

    /**
     * 处理http请求，第一次websocket的请求是http请求
     *
     * @param ctx     ctx
     * @param request request
     */
    private void handleHttp(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (!request.decoderResult().isSuccess() || !"websocket".equals(request.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
        }
        //正常的请求，构建握手
        WebSocketServerHandshakerFactory wsf = new WebSocketServerHandshakerFactory("ws://" + request.headers().get(HttpHeaderNames.HOST), null, false);
        WebSocketServerHandshaker handShaker = wsf.newHandshaker(request);
        if (handShaker == null) {
            //无法处理的 websocket 版本
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            //返回握手
            handShaker.handshake(ctx.channel(), request);
        }

    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        //返回response
        if (response.status().code() != HttpStatus.OK.value()) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        //如果 费 keep-alive
        if (!HttpUtil.isKeepAlive(request) || response.status().code() != HttpStatus.OK.value()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
