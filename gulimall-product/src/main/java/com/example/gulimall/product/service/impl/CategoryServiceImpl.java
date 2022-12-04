package com.example.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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