package com.example.gulimall.product;

import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.service.SpuInfoService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.*;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    AttrService attrService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }

    @Test
    void contextLoads() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.SECONDS,new LinkedBlockingQueue<>(1));
         for (int i = 0; i < 2; i++) {
            String a = String.valueOf(i);
            executor.execute(()->{
                attrService.test(a);
//                attrService.updateById();
                System.out.println("================================");
//                attrService.test(a);

            });
             try {
                 TimeUnit.SECONDS.sleep(2);
                 System.out.println(Thread.currentThread().getName()+"睡眠两秒");
             }catch (Exception e){

             }
        }
    }

    @Test
    public void testRedis(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        ops.set("hello", "world_"+ UUID.randomUUID());

        String hello = ops.get("hello");

        System.out.println("redis保存的值：" + hello);

        String name = redisConnectionFactory.getClass().getName();
        System.out.println(name);
    }

    @Test
    void test(){
        ExecutorService service = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            service.submit(() -> {
                Singleton instance = Singleton.getInstance();
                System.out.println(instance.hashCode());
            });
        }

    }





}
