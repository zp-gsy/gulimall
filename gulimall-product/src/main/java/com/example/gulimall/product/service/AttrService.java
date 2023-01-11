package com.example.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.product.entity.AttrEntity;
import com.example.gulimall.product.entity.ProductAttrValueEntity;
import com.example.gulimall.product.vo.AttrVo;
import com.example.gulimall.product.vo.ProductAttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 19:15:39
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId, String attrType);

    void saveAttr(AttrVo attrVo);

    AttrVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attrVo);

    PageUtils getAttrgroupNoRelation(Map<String, Object> params, Long attrgroupId);

    List<ProductAttrValueEntity> getListForSpu(Long spuId);

    void updateBySpuId(Long spuId, List<ProductAttrVo> productAttrVo);

    void test(String id);

    List<AttrVo> testRedis();

    List<AttrVo> testRedisLock();

}

