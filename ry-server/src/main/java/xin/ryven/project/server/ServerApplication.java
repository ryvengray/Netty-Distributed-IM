package xin.ryven.project.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.server.config.ApplicationProperties;
import xin.ryven.project.server.zk.ZkRegistryWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gray
 */
@Slf4j
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@ComponentScan(basePackages = {"xin.ryven.project.server", "xin.ryven.project.common"})
public class ServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public ServerAddress serverAddress(ApplicationProperties properties) {
        ServerAddress serverAddress = ServerAddress.builder()
                .host(properties.getServerHost())
                .port(properties.getServerImPort())
                .httpPort(properties.getServerPort()).build();
        serverAddress.setDynamicHost();
        if (StringUtils.isEmpty(serverAddress.getHost())) {
            serverAddress.setHost(serverAddress.getLocaleHost());
        }
        return serverAddress;
    }

    @Override
    public void run(String... args) {
        ServerAddress serverAddress = SpringBeanUtils.getBean(ServerAddress.class);
        log.info("Start registry");
        //启动zk注册临时节点
        ExecutorService singleExecutorService = new ThreadPoolExecutor(1,
                1,
                0,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("Registry-Worker-%d").build());
        singleExecutorService.submit(new ZkRegistryWorker(serverAddress));
        log.info("End registry");
    }
}
