package com.example.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.example.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.gulimall.product.service.AttrAttrgroupRelationService;
import com.example.gulimall.product.vo.AttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteRelation(AttrGroupVo[] attrGroupVos) {
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        for (AttrGroupVo vo : attrGroupVos) {
            wrapper.or((obj)->{
                obj.eq("attr_id", vo.getAttrId()).eq("attr_group_id", vo.getAttrGroupId());
            });
        }
        this.remove(wrapper);
    }

    @Override
    public void saveBatch(List<AttrGroupVo> vos) {
        List<AttrAttrgroupRelationEntity> entityList = vos.stream().map(obj -> {
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(obj, relation);
            return relation;
        }).collect(Collectors.toList());
        this.saveBatch(entityList);
    }

}