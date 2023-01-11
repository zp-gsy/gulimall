package com.example.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.ware.vo.MergeVo;
import com.example.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 20:27:07
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils getUnreceiveList();

    void merge(MergeVo mergeVo);

    void received(List<Long> ids);
}

