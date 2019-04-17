package xin.ryven.project.server.holder;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存用户与channel的联系
 *
 * @author gray
 */
public class SocketHolder {

    private static final Map<Integer, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>(16);

    public static void save(Integer userId, NioSocketChannel channel) {
        assert userId != null;
        CHANNEL_MAP.put(userId, channel);
    }

    public static NioSocketChannel channel(Integer userId) {
        return CHANNEL_MAP.get(userId);
    }

    public static void remove(Integer userId) {
        CHANNEL_MAP.remove(userId);
    }

    public static void remove(NioSocketChannel channel) {
        CHANNEL_MAP.entrySet().stream().filter(e -> e.getValue() == channel).forEach(e -> CHANNEL_MAP.remove(e.getKey()));
    }

    public static Collection<NioSocketChannel> allChannels() {
        return CHANNEL_MAP.values();
    }

}
