package com.example.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.ware.dao.PurchaseDetailDao;
import com.example.gulimall.ware.entity.PurchaseDetailEntity;
import com.example.gulimall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
        /**
         *    key: '华为',//检索关键字
         *    status: 0,//状态
         *    wareId: 1,//仓库id
         */

        String key = (String) params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and(t -> {
                t.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }

        String status = (String) params.get("status");
        if (StringUtils.hasLength(status)) {
            queryWrapper.eq("status", status);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.hasLength(wareId)) {
            queryWrapper.and(t -> {
                t.eq("ware_id", wareId);
            });
        }


        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listByPurchaseId(Long id) {
        return  this.lambdaQuery().eq(PurchaseDetailEntity::getPurchaseId, id).list();
    }

}