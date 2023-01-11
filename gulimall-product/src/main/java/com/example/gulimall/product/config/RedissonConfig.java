package com.example.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author zp
 * @date 2023/1/11
 * @apiNote redisson 分布式锁
 */
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config(); //192.168.56.10
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");
        return Redisson.create(config);
        /**
         * Config config = new Config();
         * config.useSingleServer().setAddress("myredisserver:6379");
         * RedissonClient redisson = Redisson.create(config);
         */
    }
}
