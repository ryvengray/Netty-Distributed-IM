package xin.ryven.project.server.init;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.server.handler.RyServerHandler;
import xin.ryven.project.server.holder.NettyAttrHolder;
import xin.ryven.project.server.holder.SocketHolder;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * @author gray
 */
@Component
@Slf4j
public class RyServer {

    @Value("${server.im-port}")
    private int imPort;

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();

    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(imPort))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                // 空闲心跳检测
                                .addLast(new IdleStateHandler(30, 0, 0))
                                .addLast("http-codec", new HttpServerCodec())
                                .addLast("aggregator", new HttpObjectAggregator(65536))
                                .addLast("http-chunked", new ChunkedWriteHandler())
                                // 服务端的Handler
                                .addLast("handler", new RyServerHandler());
                    }
                });
        ChannelFuture future = b.bind().sync();
        if (future.isSuccess()) {
            log.info("启动Server成功");
        }
    }

    public void sendMsg(MsgVo vo) {
        NioSocketChannel toChannel = SocketHolder.channel(vo.getToUserId());
        if (toChannel == null) {
            log.warn("User offline, user id = {}", vo.getToUserId());
            throw new RuntimeException("用户已经下线");
        }
        toChannel.writeAndFlush(Unpooled.copiedBuffer(JSON.toJSONString(vo), CharsetUtil.UTF_8))
                .addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        log.warn("Send message to {} failed, close channel", NettyAttrHolder.getUser(toChannel));
                        toChannel.close();
                    }
                });
    }

    /**
     * 下线
     * 1、重复登录，下线之前的channel
     *
     * @param channel channel
     */
    public void offline(NioSocketChannel channel) {
        channel.close();
        SocketHolder.remove(channel);
    }

}
