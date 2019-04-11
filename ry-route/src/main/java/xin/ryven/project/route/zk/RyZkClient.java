package xin.ryven.project.route.zk;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xin.ryven.project.route.cache.ServerCache;
import xin.ryven.project.route.config.ApplicationProperties;

import java.util.List;

/**
 * @author gray
 */
@Component
@Slf4j
public class RyZkClient {

    private final ZkClient zkClient;
    private final ApplicationProperties applicationProperties;
    private final ServerCache serverCache;

    @Autowired
    public RyZkClient(ZkClient zkClient, ApplicationProperties applicationProperties, ServerCache serverCache) {
        this.zkClient = zkClient;
        this.applicationProperties = applicationProperties;
        this.serverCache = serverCache;
    }

    public void subscribe(String path) {
        zkClient.subscribeChildChanges(path, (s, list) -> {
            log.info("Server list change. Path: 【{}】, children: 【{}】", s, list);
            serverCache.updateCache(list);
        });
    }

    public List<String> getAllNode() {
        return zkClient.getChildren(applicationProperties.getZkClientRoot());
    }
}
