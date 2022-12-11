package com.example.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zp
 * @date 2022/12/11
 * @apiNote
 */
@Configuration
@MapperScan("com.example.gulimall.product.dao")
@EnableTransactionManagement //开启事务
public class MyBatisConfig {

    //分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLimit(1000);
        paginationInterceptor.setOverflow(true);
        return paginationInterceptor;

    }
}
