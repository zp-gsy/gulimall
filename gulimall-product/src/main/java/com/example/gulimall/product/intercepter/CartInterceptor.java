package com.example.gulimall.product.intercepter;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zp
 * @date 2023/2/7
 * @apiNote 拦截器
 */
public class CartInterceptor implements HandlerInterceptor {
    //同一个线程下共享数据
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
