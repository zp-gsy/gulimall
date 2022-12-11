package com.example.gulimall.product.dao;

import com.example.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-12-11 19:09:30
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateBrandNameById(@Param("brandId") Long brandId, @Param("barndName") String barndName);
}
