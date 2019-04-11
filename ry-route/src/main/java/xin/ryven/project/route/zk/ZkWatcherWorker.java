package xin.ryven.project.route.zk;

import lombok.extern.slf4j.Slf4j;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.route.cache.ServerCache;
import xin.ryven.project.route.config.ApplicationProperties;

import java.util.List;

/**
 * @author gray
 */
@Slf4j
public class ZkWatcherWorker implements Runnable {

    private RyZkClient ryZkClient;
    private ApplicationProperties properties;

    public ZkWatcherWorker() {
        this.ryZkClient = SpringBeanUtils.getBean(RyZkClient.class);
        this.properties = SpringBeanUtils.getBean(ApplicationProperties.class);
    }

    @Override
    public void run() {
        this.ryZkClient.subscribe(this.properties.getZkClientRoot());
        //初始化获取一次Server
        List<String> strings = SpringBeanUtils.getBean(ServerCache.class).allServer();
        log.info("初始化获取Server列表: {}", strings);
    }
}
