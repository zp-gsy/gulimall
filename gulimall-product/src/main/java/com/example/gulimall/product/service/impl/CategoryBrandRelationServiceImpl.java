package com.example.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.BrandDao;
import com.example.gulimall.product.dao.CategoryBrandRelationDao;
import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.BrandEntity;
import com.example.gulimall.product.entity.CategoryBrandRelationEntity;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.BrandService;
import com.example.gulimall.product.service.CategoryBrandRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
@RequiredArgsConstructor
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    private final CategoryDao categoryDao;

    private final BrandDao brandDao;

    private final BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<CategoryBrandRelationEntity> queryCategoryBrand(Long brandId) {
        List<CategoryBrandRelationEntity> list = this.lambdaQuery().eq(CategoryBrandRelationEntity::getBrandId, brandId).list();
        return list;
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        //保存品牌名  分类名
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public List<BrandEntity> getBrandsList(Long catId) {
        List<CategoryBrandRelationEntity> list = this.lambdaQuery().eq(CategoryBrandRelationEntity::getCatelogId, catId).list();
        List<Long> collect = list.stream().map(t -> {
            return t.getBrandId();
        }).collect(Collectors.toList());

        List<BrandEntity> brandEntityList = brandService.lambdaQuery().in(BrandEntity::getBrandId, collect).list();
        return brandEntityList;
    }

}