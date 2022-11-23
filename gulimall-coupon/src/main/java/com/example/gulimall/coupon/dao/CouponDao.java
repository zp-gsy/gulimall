package com.example.gulimall.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gulimall.coupon.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 20:16:14
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
