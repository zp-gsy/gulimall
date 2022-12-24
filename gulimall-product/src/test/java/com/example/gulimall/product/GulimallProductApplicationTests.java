package com.example.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.gulimall.product.entity.SpuInfoEntity;
import com.example.gulimall.product.service.SpuInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    SpuInfoService spuInfoService;
    @Test
    void contextLoads() {

//        SpuInfoEntity spuInfo = new SpuInfoEntity();
//        spuInfo.setSpuName("cesi");
//        spuInfo.setBrandId(99L);
//        spuInfoService.save(spuInfo);
        SpuInfoEntity entity = new SpuInfoEntity();
        entity.setSpuName("zhangsan");
        UpdateWrapper<SpuInfoEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("brand_id", 99L);
        spuInfoService.update(entity, updateWrapper);
//        SpuInfoEntity entity = new SpuInfoEntity();
//        entity.setBrandId(99L);
//        UpdateWrapper<SpuInfoEntity> updateWrapper = new UpdateWrapper<SpuInfoEntity>(entity);
//        updateWrapper.set("spu_name", "ceshihhh");
//        spuInfoService.update(updateWrapper);
    }



}
