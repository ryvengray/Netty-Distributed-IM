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
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.enums.MsgType;
import xin.ryven.project.common.service.HeartBeatService;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.common.vo.feign.SaveUserServer;
import xin.ryven.project.server.config.ApplicationProperties;
import xin.ryven.project.server.handler.RyServerHandler;
import xin.ryven.project.server.holder.NettyAttrHolder;
import xin.ryven.project.server.holder.SocketHolder;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author gray
 */
@Component
@Slf4j
public class RyServer implements DisposableBean {

    @Value("${server.im-port}")
    private int imPort;

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();

    private final ServerAddress serverAddress;
    private final AmqpTemplate amqpTemplate;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public RyServer(ServerAddress serverAddress, AmqpTemplate amqpTemplate, ApplicationProperties applicationProperties) {
        this.serverAddress = serverAddress;
        this.amqpTemplate = amqpTemplate;
        this.applicationProperties = applicationProperties;
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
                                .addLast(new IdleStateHandler(20, 0, 0))
                                .addLast("http-codec", new HttpServerCodec())
                                .addLast("aggregator", new HttpObjectAggregator(65536))
                                .addLast("http-chunked", new ChunkedWriteHandler())
                                // 服务端的Handler
                                .addLast("handler", new RyServerHandler(RyServer.this, SpringBeanUtils.getBean(HeartBeatService.class)));
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
            MsgVo logoutVo = MsgVo.builder().userId(vo.getToUserId()).username(vo.getToUsername()).type(MsgType.NOTIFY_LOGOUT.getType()).build();
            amqpTemplate.convertAndSend(applicationProperties.getMqExchange(), "", JSON.toJSONString(logoutVo));
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
     * 用户的上线下线通知所有的客户端
     *
     * @param jsonMessage 通知的消息，字符串格式
     */
    public void notifyAll(String jsonMessage) {
        Collection<NioSocketChannel> nioSocketChannels = SocketHolder.allChannels();
        nioSocketChannels.forEach(c -> c.writeAndFlush(new TextWebSocketFrame(jsonMessage)));
    }

    /**
     * 上线
     */
    public void online(MsgVo msgVo) {
        //需要route处理的内容
        SaveUserServer saveUserServer = new SaveUserServer(msgVo.getUserId(), serverAddress);
        msgVo.setContent(JSON.toJSONString(saveUserServer));
        msgVo.setType(MsgType.NOTIFY_LOGIN.getType());
        amqpTemplate.convertAndSend(applicationProperties.getMqExchange(), "", JSON.toJSONString(msgVo));
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
            MsgVo msgVo = MsgVo.builder().userId(user.getUserId())
                    .username(user.getUsername()).type(MsgType.NOTIFY_LOGOUT.getType()).build();
            amqpTemplate.convertAndSend(applicationProperties.getMqExchange(), "", JSON.toJSONString(msgVo));
        }
        channel.close();
        SocketHolder.remove(channel);
    }

    @Override
    public void destroy() {
        log.info("关闭服务...");
        //清空本服务器的在线用户
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
