package xin.ryven.project.server.zk;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xin.ryven.project.server.config.ApplicationProperties;

/**
 * @author gray
 */
@Component
public class RyZkClient {

    private final ZkClient zkClient;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public RyZkClient(ZkClient zkClient, ApplicationProperties applicationProperties) {
        this.zkClient = zkClient;
        this.applicationProperties = applicationProperties;
    }

    /**
     * 检查本项目的根节点是否存在，不存在就创建
     */
    public void createRootNode() {
        String rootPath = applicationProperties.getZkClientRoot();
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }
    }

    public void createEphemeralNode(String path) {
        zkClient.createEphemeral(path);
    }
}
