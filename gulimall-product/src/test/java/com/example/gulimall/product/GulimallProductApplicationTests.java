package com.example.gulimall.product;

import com.example.gulimall.product.entity.SpuInfoDescEntity;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.service.SpuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.*;

@SpringBootTest
@Slf4j
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

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

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



    @Test
    public void createExchange(){
        DirectExchange directExchange = new DirectExchange("my-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("createExchange 【{}】创建成功.", directExchange);
    }

    @Test
    public void createQueue(){
        Queue queue = new Queue("my-java-queue", true, false,false);
        amqpAdmin.declareQueue(queue);
        log.info("createExchange 【{}】创建成功.", queue);
    }

    @Test
    public void createBinding(){
        Binding binding = new Binding("my-java-queue", Binding.DestinationType.QUEUE,"my-java-exchange","hello",null);
       amqpAdmin.declareBinding(binding);
        log.info("createExchange 【{}】创建成功.", binding);
    }

    @Test
    public void testSendMsg(){
        rabbitTemplate.convertAndSend("my-java-exchange","hello","嘿");
        log.info("消息发送成功...");
    }

    @Test
    public void testSendObjMsg(){
        SpuInfoDescEntity entity = new SpuInfoDescEntity();
        entity.setDecript("哈哈");
        entity.setSpuId(1L);
        rabbitTemplate.convertAndSend("my-java-exchange","hello",entity);
        log.info("消息发送成功...");
    }

}
