package com.example.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.to.SkuReductionTo;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.coupon.dao.SkuFullReductionDao;
import com.example.gulimall.coupon.entity.MemberPriceEntity;
import com.example.gulimall.coupon.entity.SkuFullReductionEntity;
import com.example.gulimall.coupon.entity.SkuLadderEntity;
import com.example.gulimall.coupon.service.MemberPriceService;
import com.example.gulimall.coupon.service.SkuFullReductionService;
import com.example.gulimall.coupon.service.SkuLadderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
@RequiredArgsConstructor
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    private final SkuLadderService skuLadderService;

    private final MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveReductionInfo(SkuReductionTo skuReductionTo) {
        //sms_sku_ladder(打折表)
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        //满几件 或者 打几折 过滤0
        if(skuLadderEntity.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }


        //sms_sku_full_reduction 满减表
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(skuFullReductionEntity);
        }


        //sms_member_price会员价格表
        List<MemberPriceEntity> collect = skuReductionTo.getMemberPrice().stream().map(t -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(t.getId());
            memberPriceEntity.setMemberLevelName(t.getName());
            memberPriceEntity.setMemberPrice(t.getPrice());
            memberPriceEntity.setAddOther(0);
            return memberPriceEntity;
        }).filter(t->{
            return t.getMemberPrice()!= null ||t.getMemberPrice().compareTo(new BigDecimal("0"))==1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}