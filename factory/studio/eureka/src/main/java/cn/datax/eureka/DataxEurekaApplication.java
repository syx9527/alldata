package cn.datax.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author SYX
 */
@EnableEurekaServer
@SpringBootApplication
public class DataxEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataxEurekaApplication.class, args);
    }

}
