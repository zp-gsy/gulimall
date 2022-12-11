package com.example.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.example.gulimall.product.dao.AttrDao;
import com.example.gulimall.product.dao.AttrGroupDao;
import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.gulimall.product.entity.AttrEntity;
import com.example.gulimall.product.entity.BrandEntity;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.AttrAttrgroupRelationService;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.vo.AttrResVo;
import com.example.gulimall.product.vo.AttrVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("attrService")
@RequiredArgsConstructor
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

//    private final AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    private final CategoryDao categoryDao;

    private final AttrGroupDao attrGroupDao;

    private final CategoryServiceImpl categoryService;

    private final AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId) {

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(catelogId != 0){
            wrapper.eq("catelog_id", catelogId);
        }
        if(!ObjectUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrResVo> list = page.getRecords().stream().map((obj) -> {
            AttrResVo resVo = new AttrResVo();
            BeanUtils.copyProperties(obj, resVo);
            CategoryEntity category = categoryDao.selectById(obj.getCatelogId());
            //设置分类名字
            if(Objects.nonNull(category)){
                resVo.setCatelogName(category.getName());
            }

            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", obj.getAttrId());
            AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.getOne(queryWrapper);
            //设置分组名字
            if(Objects.nonNull(relation)){
                Long attrGroupId = relation.getAttrGroupId();
                resVo.setGroupName(attrGroupDao.selectById(attrGroupId).getAttrGroupName());
            }
            return resVo;
        }).collect(Collectors.toList());

        pageUtils.setList(list);
        return pageUtils;
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attr = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attr);
        //保存基本信息
        this.save(attr);
        //保存关联关系
        AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
        entity.setAttrGroupId(attrVo.getAttrGroupId());
        entity.setAttrId(attr.getAttrId());
        attrAttrgroupRelationService.save(entity);

    }

    @Override
    public AttrVo getAttrInfo(Long attrId) {
        AttrVo attrVo = new AttrVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrVo);
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id", attrEntity.getAttrId());
        AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.getOne(queryWrapper);
        if(Objects.nonNull(relation)){
            attrVo.setAttrGroupId(relation.getAttrGroupId());
        }

        Long[] pathByCatId = categoryService.findPathByCatId(attrEntity.getCatelogId());
        attrVo.setCatelogPath(pathByCatId);
        return attrVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,entity);
        this.updateById(entity);
        //更新关联关系
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
        relation.setAttrId(entity.getAttrId());
        relation.setAttrGroupId(attrVo.getAttrGroupId());
        wrapper.eq("attr_id", entity.getAttrId());
        //存在记录就更新 没有记录说明之前没有关联 需要新增关联关系
        attrAttrgroupRelationService.saveOrUpdate(relation, wrapper);

    }

}