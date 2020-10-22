package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName RunPayServerApplication
 * @Description: RunPayServerApplication
 * @Author jinluying
 * @create: 2020-10-22 15:03
 * @Version V1.0
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaClient
@EnableFeignClients
public class RunPayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunPayServerApplication.class);
    }
}
