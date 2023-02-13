package com.example.gulimall.product.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author zp
 * @date 2023/2/12
 * @apiNote
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    /**
     * rabbitmq 消息确认分为服务端确认 和 消费端 确认
     * 1：服务端
     *  1.1 消息到达broker 时候回调
     *  1.2 消息从exchange成功到达queue时候回调
     * 2：服务端 ack 默认是自动ack 消息收到后 channel内所有的都返回到queue 并且删除
     * 2.1 关闭自动ack 并且设置未手动回调
     *
     */

    @PostConstruct //表示这个类创建完成之后执行
    public void initRabbitTemplate(){
        //设置到达broker之后自动回调,需要配置  publisher-confirm-type: correlated
        /**
         * correlationData 唯一标识
         * ack 是否成功 true 成功
         * cause 失败原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> System.out.println("correlationData:["+correlationData+"]+ack:["+ack+"]+cause:["+cause+"]"));

        /**
         * 只要消息没有投递给指定的queue  就会触发这个回调 需要配置 spring.rabbitmq.publisher-returns=true
         */
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returned) {
                System.out.println("ReturnedMessage:[" + returned + "]");
            }
        });
    }
}
