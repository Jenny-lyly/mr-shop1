package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName RunOauthServerApplication
 * @Description: RunOauthServerApplication
 * @Author jinluying
 * @create: 2020-10-15 11:36
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class RunOauthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunOauthServerApplication.class);
    }
}
