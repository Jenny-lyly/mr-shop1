package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @ClassName RunZuulServerApplicarion
 * @Description: RunZuulServerApplicarion
 * @Author jinluying
 * @create: 2020-08-28 09:39
 * @Version V1.0
 **/
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class RunZuulServerApplicarion {
    public static void main(String[] args) {
        SpringApplication.run(RunZuulServerApplicarion.class);
    }
}