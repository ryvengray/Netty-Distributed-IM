package xin.ryven.project.server.zk;

import lombok.extern.slf4j.Slf4j;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.server.config.ApplicationProperties;

/**
 * @author gray
 */
@Slf4j
public class ZkRegistryWorker implements Runnable {

    private String host;

    private Integer socketPort;

    private Integer httpPort;

    private RyZkClient ryZkClient;

    private ApplicationProperties applicationProperties;

    public ZkRegistryWorker(ServerAddress serverAddress) {
        this.host = serverAddress.getHost();
        this.socketPort = serverAddress.getPort();
        this.httpPort = serverAddress.getHttpPort();
        this.ryZkClient = SpringBeanUtils.getBean(RyZkClient.class);
        this.applicationProperties = SpringBeanUtils.getBean(ApplicationProperties.class);
    }

    @Override
    public void run() {
        //首先检查创建根节点
        ryZkClient.createRootNode();
        //需要将本机注册到zk
        if (applicationProperties.getRegistry()) {
            String path = applicationProperties.getZkClientRoot() + "/host-" + this.host + ":" + this.socketPort + ":" + this.httpPort;
            ryZkClient.createEphemeralNode(path);
            log.info("Registry success, path = {}", path);
        }
    }
}
