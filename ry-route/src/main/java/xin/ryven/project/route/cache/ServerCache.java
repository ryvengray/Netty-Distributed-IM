package xin.ryven.project.route.cache;

import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.route.zk.RyZkClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gray
 */
@Component
public class ServerCache {

    private final LoadingCache<String, String> cache;

    @Autowired
    public ServerCache(LoadingCache<String, String> cache) {
        this.cache = cache;
    }

    public void addServer(String server) {
        String prefixSeparator = "-";
        if (server.contains(prefixSeparator)) {
            server = server.split(prefixSeparator)[1];
        }
        cache.put(server, server);
    }

    public void updateCache(List<String> servers) {
        cache.invalidateAll();
        servers.forEach(this::addServer);
    }

    public List<String> allServer() {
        if (cache.size() == 0) {
            //在这里获取Bean，解决bean循环引用的问题
            RyZkClient ryZkClient = SpringBeanUtils.getBean(RyZkClient.class);
            //没有的话获取一次
            List<String> allNode = ryZkClient.getAllNode();
            allNode.forEach(this::addServer);
        }
        return new ArrayList<>(cache.asMap().keySet());
    }
}
