package com.example.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.gulimall.product.vo.AttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 19:15:39
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteRelation(AttrGroupVo[] attrGroupVos);

    void saveBatch(List<AttrGroupVo> vos);
}

