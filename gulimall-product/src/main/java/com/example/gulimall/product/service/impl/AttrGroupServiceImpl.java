package com.example.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.AttrDao;
import com.example.gulimall.product.dao.AttrGroupDao;
import com.example.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.gulimall.product.entity.AttrEntity;
import com.example.gulimall.product.entity.AttrGroupEntity;
import com.example.gulimall.product.service.AttrAttrgroupRelationService;
import com.example.gulimall.product.service.AttrGroupService;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.vo.AttrGroupWithAttrVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
@RequiredArgsConstructor
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    private final AttrAttrgroupRelationService attrAttrgroupRelationService;

    private final AttrDao attrDao;

    private final AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        //catelogId=0 则查询全部，否则查询指定catelogId
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        //带检索条件 key  => select * from pms_attr_group where catelog_id = ? and (attr_group_id = key or attr_group_name like '%key%')
        if (!ObjectUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> getAttrRelation(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationService
                .lambdaQuery()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId)
                .list();

        List<Long> listAttrId = list.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        return listAttrId.isEmpty() == true ?
                new ArrayList<AttrEntity>() :
                attrDao.selectBatchIds(listAttrId);
    }

    @Override
    public List<AttrGroupWithAttrVo> getAttrgroupWithAttrByCatelogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntityList = this.lambdaQuery()
                .eq(AttrGroupEntity::getCatelogId, catelogId)
                .list();

        List<AttrGroupWithAttrVo> collect = attrGroupEntityList.stream().map(t -> {
            AttrGroupWithAttrVo attrVo = new AttrGroupWithAttrVo();
            BeanUtils.copyProperties(t, attrVo);
            List<AttrEntity> attrs = this.getAttrRelation(attrVo.getAttrGroupId());
            attrVo.setAttrs(attrs);
            return attrVo;
        }).collect(Collectors.toList());
        return collect;
    }


}