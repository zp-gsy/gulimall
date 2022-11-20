package com.example.gulimall.product.dao;

import com.example.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 19:15:39
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
