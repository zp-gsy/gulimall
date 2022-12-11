package com.example.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gulimall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 20:27:07
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
