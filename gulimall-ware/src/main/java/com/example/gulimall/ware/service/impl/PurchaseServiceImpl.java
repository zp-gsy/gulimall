package com.example.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.WareConstant;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.ware.dao.PurchaseDao;
import com.example.gulimall.ware.entity.PurchaseDetailEntity;
import com.example.gulimall.ware.entity.PurchaseEntity;
import com.example.gulimall.ware.service.PurchaseService;
import com.example.gulimall.ware.vo.MergeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    private final PurchaseDetailServiceImpl purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils getUnreceiveList() {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(new HashMap<>()),
                new QueryWrapper<PurchaseEntity>().in("status",
                        WareConstant.PurchaseStatusEnum.CREATED.getCode()
                        , WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void merge(MergeVo mergeVo) {
        //如果有整单id 则合并 没有就新建
        Long purchaseId = mergeVo.getPurchaseId();

        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // 确认采购单状态是新建或已分配
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = purchaseDetailService.listByIds(mergeVo.getItems())
                .stream()
                .filter(t -> {
                    return t.getStatus() == WareConstant.PurchaseDetailStatusEnum.CREATED.getCode() ||
                            t.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode();
                })
                .map(t -> {
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(t.getId());
                    purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                    purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                    return purchaseDetailEntity;
                }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);
    }

    @Transactional
    @Override
    public void received(List<Long> ids) {

        // 1 确认采购单状态
        List<PurchaseEntity> collect = this.listByIds(ids)
                .stream()
                .filter(t -> {
                    return t.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                            t.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode();
                })
                .map(t -> {
                    t.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    return t;
                }).collect(Collectors.toList());

        // 2 改变采购单状态
        this.updateBatchById(collect);
        // 3 改变采购项状态
        collect.forEach(t->{
            List<PurchaseDetailEntity> list = purchaseDetailService.listByPurchaseId(t.getId());
            List<PurchaseDetailEntity> entityList = list.stream().map(obj -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                entity.setId(obj.getId());
                return entity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(entityList);
        });

    }

}