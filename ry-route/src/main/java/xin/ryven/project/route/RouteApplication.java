package xin.ryven.project.route;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import xin.ryven.project.route.zk.ZkWatcherWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gray
 */
@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableEurekaClient
@ComponentScan(basePackages = {"xin.ryven.project.route", "xin.ryven.project.common"})
public class RouteApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
    }

    @Override
    public void run(String... args) {
        //启动zk注册临时节点
        ExecutorService singleExecutorService = new ThreadPoolExecutor(1,
                1,
                0,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("Watcher-Worker-%d").build());
        singleExecutorService.submit(new ZkWatcherWorker());
        log.info("ZooKeeper Watcher started");
    }
}