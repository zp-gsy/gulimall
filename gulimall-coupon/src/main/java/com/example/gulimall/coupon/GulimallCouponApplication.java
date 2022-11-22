package com.example.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.example.gulimall.coupon.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallCouponApplication {

    public static void main(String[] args) {
        //test
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
