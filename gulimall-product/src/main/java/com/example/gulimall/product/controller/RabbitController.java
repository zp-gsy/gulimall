package com.example.gulimall.product.controller;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.UUID;

/**
 * @author zp
 * @date 2023/2/12
 * @apiNote
 */
@Controller
@Slf4j
//@RabbitListener(queues = {"my-java-queue"})
@RequestMapping("/rabbit")
@ResponseBody
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 类型重载  参数 就是类型参数
     * @param message
     */
    @RabbitListener(queues = {"my-java-queue"})
    public void getMsg(Message message, Channel channel) throws IOException {
        log.info("接收到消息:{}", message);

        log.info("接收到消息类型是:{}", message.getClass());
        byte[] body = message.getBody();
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("deliveryTag:{}", deliveryTag);
        if (deliveryTag%2==0) {
            channel.basicAck(deliveryTag,false);
            System.out.println("签收了:"+deliveryTag);
        }else {
            //long deliveryTag, id
            // boolean multiple, 是否批量提交
            // boolean requeue 是否放回队列
            channel.basicNack(deliveryTag, false,true );
            System.out.println("拒绝了:"+deliveryTag);
//            channel.basicReject();
        }


    }

    @GetMapping(value = "/test")
    public String sendMsg(@RequestParam(name = "num", defaultValue = "10") Integer num){
        for (Integer i = 0; i < num; i++) {
            if(i%2==0){
                rabbitTemplate.convertAndSend("my-java-exchange","hello","哈哈-"+i,new CorrelationData(UUID.randomUUID().toString()));
            }else {
                rabbitTemplate.convertAndSend("my-java-exchange","hello-","嘿嘿-"+i,new CorrelationData(UUID.randomUUID().toString()));
            }

        }
        return "ok";
    }
}
