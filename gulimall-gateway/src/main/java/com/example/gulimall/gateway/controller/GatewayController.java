package com.example.gulimall.gateway.controller;

import com.example.common.utils.R;
import com.example.gulimall.gateway.feign.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zp
 * @date 2022/11/23
 * @apiNote
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    @Autowired
    CouponService couponService;

    @RequestMapping("/test")
    public R test(){
        return couponService.test();
    }
}
