package xin.ryven.project.server.init;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.common.vo.feign.SaveUserServer;
import xin.ryven.project.server.config.ApplicationProperties;
import xin.ryven.project.server.handler.RyServerHandler;
import xin.ryven.project.server.holder.NettyAttrHolder;
import xin.ryven.project.server.holder.SocketHolder;
import xin.ryven.project.server.feigns.RouteService;

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

    private final RouteService routeService;
    private final ServerAddress serverAddress;

    @Autowired
    public RyServer(RouteService routeService, ApplicationProperties properties) {
        this.routeService = routeService;
        this.serverAddress = ServerAddress.builder()
                .host(properties.getServerHost())
                .port(properties.getServerImPort())
                .httpPort(properties.getServerPort()).build();
    }

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
        toChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(vo)))
                .addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        log.warn("Send message to {} failed, close channel", NettyAttrHolder.getUser(toChannel));
                        toChannel.close();
                    }
                });
    }

    /**
     * 上线
     */
    public void online(Integer userId) {
        routeService.saveUserChannel(new SaveUserServer(userId, serverAddress));
    }

    /**
     * 下线
     * 1、重复登录，下线之前的channel
     *
     * @param channel channel
     */
    public void offline(NioSocketChannel channel) {
        User user = NettyAttrHolder.getUser(channel);
        if (user != null) {
            routeService.offline(user);
        }
        channel.close();
        SocketHolder.remove(channel);
    }

}
