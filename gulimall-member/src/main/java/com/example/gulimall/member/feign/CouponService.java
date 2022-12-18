package com.example.gulimall.member.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zp
 * @date 2022/11/23
 * @apiNote
 */

@FeignClient("gulimall-coupon")
public interface CouponService {

    @RequestMapping("/coupon/coupon/test")
    R test();

}
