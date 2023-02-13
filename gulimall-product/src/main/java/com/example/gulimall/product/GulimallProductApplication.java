package com.example.gulimall.product;

import com.alibaba.cloud.nacos.NacosConfigAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.example.gulimall.product.dao")
@SpringBootApplication(exclude = NacosConfigAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
@EnableRabbit
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
