package xin.ryven.project.server.holder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import xin.ryven.project.common.entity.User;

/**
 * 保存netty的attr
 *
 * @author gray
 */
public class NettyAttrHolder {

    private static final AttributeKey<Long> ATTR_READ_TIME = AttributeKey.valueOf("ATTR_READ_TIME");
    private static final AttributeKey<User> ATTR_USER = AttributeKey.valueOf("ATTR_USER");

    public static void updateReadTime(ChannelHandlerContext ctx) {
        ctx.channel().attr(ATTR_READ_TIME).set(System.currentTimeMillis());
    }

    /**
     * 获取channel的上次读取时间
     *
     * @return 如果没有readTime，返回0，可以保证时差很大
     */
    public static long getReadTime(ChannelHandlerContext ctx) {
        Long lastReadTime = ctx.channel().attr(ATTR_READ_TIME).get();
        return lastReadTime == null ? 0 : lastReadTime;
    }

    public static void saveUser(ChannelHandlerContext ctx, User user) {
        ctx.channel().attr(ATTR_USER).set(user);
    }

    public static User getUser(ChannelHandlerContext ctx) {
        return getUser(ctx.channel());
    }

    public static User getUser(Channel channel) {
        return channel.attr(ATTR_USER).get();
    }

}
