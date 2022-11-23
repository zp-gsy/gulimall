package com.example.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.example.gulimall.coupon.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GulimallCouponApplication {

    public static void main(String[] args) {
        //test
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
