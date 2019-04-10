package xin.ryven.project.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author gray
 */
@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableEurekaClient
public class RouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
    }

}