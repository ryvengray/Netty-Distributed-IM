package xin.ryven.project.route.zk;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xin.ryven.project.route.config.ApplicationProperties;
import xin.ryven.project.route.service.RouteService;

import java.util.List;

/**
 * @author gray
 */
@Component
@Slf4j
public class RyZkClient {

    private final ZkClient zkClient;
    private final ApplicationProperties applicationProperties;
    private final RouteService routeService;

    @Autowired
    public RyZkClient(ZkClient zkClient, ApplicationProperties applicationProperties, RouteService routeService) {
        this.zkClient = zkClient;
        this.applicationProperties = applicationProperties;
        this.routeService = routeService;
    }

    public void subscribe(String path) {
        zkClient.subscribeChildChanges(path, (s, list) -> {
            log.info("Server list change. Path: 【{}】, children: 【{}】", s, list);
            routeService.refreshServers(list);
        });
    }

    public void initServers(){
        List<String> allNode = getAllNode();
        routeService.refreshServers(allNode);
    }

    public List<String> getAllNode() {
        return zkClient.getChildren(applicationProperties.getZkClientRoot());
    }
}
