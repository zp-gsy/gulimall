package com.example.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.common.utils.R;
import com.example.gulimall.product.dao.SpuInfoDao;
import com.example.gulimall.product.entity.*;
import com.example.gulimall.product.feign.CouponFeignService;
import com.example.gulimall.product.service.*;
import com.example.gulimall.product.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private final SpuInfoDescService spuInfoDescService;

    private final SpuImagesService spuImagesService;

    private final AttrService attrService;

    private final ProductAttrValueService productAttrValueService;

    private final SkuInfoService skuInfoService;

    private final SkuImagesService skuImagesService;

    private final SkuSaleAttrValueService skuSaleAttrValueService;

    private final CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {
        // 1 spu 基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2 spu 描述信息 pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        List<String> listDecript = spuInfo.getDecript();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", listDecript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3 spu 图片集信息 pms_spu_images
        List<String> images = spuInfo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //4 spu 规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(id.getAttrName());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntities);
        //5 spu mall_sms 积分信息
        Bounds bounds = spuInfo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSkuId(spuInfoEntity.getId());
        R r1 = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r1.getCode() != 0) {
            log.info("新增商品调用优惠系统保存积分信息失败: {}", r1);
        }

        //5 保存当前spu 对应 sku 信息
        List<Skus> skus = spuInfo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                List<Images> skuImages = sku.getImages();
                String defaultImages = "";
                for (int i = 0; i < skuImages.size(); i++) {
                    if (skuImages.get(i).getDefaultImg() == 1) {
                        defaultImages = skuImages.get(i).getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId())
                        .setBrandId(spuInfo.getBrandId())
                        .setCatalogId(spuInfo.getCatalogId())
                        .setSaleCount(0L)
                        .setSkuDefaultImg(defaultImages);
                //5.1 sku 基本信息 pms_sku_info
                skuInfoService.save(skuInfoEntity);
                // 5.2 sku 图片集信息 pms_sku_images
                List<SkuImagesEntity> skuImagesEntityList = skuImages.stream().map(img -> {
                            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                            skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                            skuImagesEntity.setImgUrl(img.getImgUrl());
                            skuImagesEntity.setDefaultImg(img.getDefaultImg());
                            return skuImagesEntity;
                        }).filter((t) -> {
                            return StringUtils.hasLength(t.getImgUrl());
                        })
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntityList);

                //5.3 sku 销售属性信息 pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);

                // 5.4 sku 优惠信息 满减信息等 mall_sms -> sms_sku_ladder(打折表) sms_sku_full_reduction 满减表  sms_member_price会员价格表
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                skuReductionTo.setMemberPrice(sku.getMemberPrice());
                R r = couponFeignService.saveReductionInfo(skuReductionTo);
                if (r.getCode() != 0) {
                    log.info("新增商品调用优惠系统保存优惠信息失败: {}", r);
                }

            });
        }
        // 5.1 sku 基本信息 pms_sku_info
        // 5.2 sku 图片集信息 pms_sku_images
        // 5.3 sku 销售属性信息 pms_sku_sale_attr_value
        // 5.4 sku 优惠信息 满减信息等 mall_sms -> sms_sku_ladder(打折表) sms_sku_full_reduction 满减表  sms_member_price会员价格表
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.save(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.hasLength(catelogId) && !Objects.equals("0", catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.hasLength(brandId) && !Objects.equals("0", brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String status = (String) params.get("status");
        if (StringUtils.hasLength(status)) {
            queryWrapper.eq("publish_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}