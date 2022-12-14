package com.example.gulimall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gulimall.order.entity.PaymentInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 20:04:54
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
