package com.example.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.CategoryBrandRelationDao;
import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.CategoryBrandRelationEntity;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private final CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> source = this.list();

        List<CategoryEntity> list = source.stream()
                .filter(t -> Objects.equals(0L, t.getParentCid()))
                .map(t -> {
                    t.setChildren(getChildren(t, source));
                    return t;
                })
                .sorted((s1, s2) -> {
                    return (s1.getSort() == null ? 0 : s1.getSort()) - (s2.getSort() == null ? 0 : s2.getSort());
                })
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void removeMenusByIds(Long[] catIds) {
        //TODO 再使用的id不可删除掉
        this.removeByIds(Arrays.asList(catIds));
    }

    @Override
    public Long[] findPathByCatId(Long catelogId) {
        List<Long> list = new ArrayList<>();
        getParentCatId(catelogId, list);
        Collections.reverse(list);
        return list.toArray(new Long[list.size()]);
    }

    @Transactional
    @Override
    public void updateDeatil(CategoryEntity category) {
        //TODO 同步修改关联表
        this.updateById(category);
        CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
        categoryBrandRelation.setCatelogName(category.getName());

        UpdateWrapper<CategoryBrandRelationEntity> wrapper = new UpdateWrapper<CategoryBrandRelationEntity>()
                .eq("catelog_id", category.getCatId());
        categoryBrandRelationDao.update(categoryBrandRelation,wrapper);
    }

    private List<Long> getParentCatId(Long catId, List<Long> list){
        list.add(catId);
        CategoryEntity category = this.getById(catId);
        if(category.getParentCid()!=0){
            //递归查找当前节点父节点
            getParentCatId(category.getParentCid(), list);
        }
        //【孙/子/父】
        return list;
    }

    private List<CategoryEntity> getChildren(CategoryEntity obj, List<CategoryEntity> source) {

        List<CategoryEntity> childrenList = source.stream()
                .filter(t -> Objects.equals(obj.getCatId(), t.getParentCid()))
                .map(t -> {
                    t.setChildren(getChildren(t, source));
                    return t;
                })
                .sorted((s1, s2) -> {
                    return (s1.getSort() == null ? 0 : s1.getSort()) - (s2.getSort() == null ? 0 : s2.getSort());
                })
                .collect(Collectors.toList());
        return childrenList;
    }

}