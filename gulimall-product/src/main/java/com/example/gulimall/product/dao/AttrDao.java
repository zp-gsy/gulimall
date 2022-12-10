package com.example.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gulimall.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 19:15:39
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
