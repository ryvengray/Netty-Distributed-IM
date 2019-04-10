package xin.ryven.project.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author gray
 */
@SpringBootApplication
@ComponentScan(basePackages = {"xin.ryven.project.common.spring", "xin.ryven.project.user"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
