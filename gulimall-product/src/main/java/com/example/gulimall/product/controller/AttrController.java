package com.example.gulimall.product.controller;

import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.gulimall.product.entity.ProductAttrValueEntity;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.vo.AttrVo;
import com.example.gulimall.product.vo.ProductAttrVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 商品属性
 *
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 19:15:39
 */
@RestController
@RequestMapping("product/attr")
@RequiredArgsConstructor
public class AttrController {
    private final AttrService attrService;

    private final RedissonClient redissonClient;



    @GetMapping("/testRedisson")
    public String test() {
        RLock lock = redissonClient.getLock("test-lock");
        lock.lock();
//        lock.lock(10,TimeUnit.SECONDS);
        /**
         *  lock  指定超时时间，就会发送给redis lua脚本，并且不会自动续期
         *
         *  lock 未指定超时时间，会自动默认一个时间 internalLockLeaseTime 【30s】 ，并且加锁之后会有个定时任务
         *  internalLockLeaseTime/3 即每隔30/3 = 10s 之后会重新设置过期时间
         *
         */
        // 最佳实战
        /**
         * lock 指定超时时间 并且 业务执行成功之后 手动解锁
         */
        try {
            System.out.println("加锁成功,执行业务代码...." + Thread.currentThread().getName() + " " +Thread.currentThread().getId());
            TimeUnit.SECONDS.sleep(40);
        }catch (Exception e){

        }finally {
            System.out.println("解锁..." + Thread.currentThread().getName() + " " +Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }

    @PostMapping("/testRedis")
    public R testRedis() {
//        List<AttrVo> list = attrService.testRedis();
        List<AttrVo> list = attrService.testRedisLock();
        return R.ok().put(list);
    }

    /**
     * 获取spu规格
     * /product/attr/update/{spuId}
     */
    @PostMapping("/update/{spuId}")
    public R updateBySpuId(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrVo> productAttrVo) {
        attrService.updateBySpuId(spuId, productAttrVo);
        return R.ok();
    }

    /**
     * 获取spu规格
     * product/attr/base/listforspu/{spuId}
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R getListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> list = attrService.getListForSpu(spuId);
        return R.ok().put(list);
    }

    /**
     * 获取分类规格参数
     * /product/attr/base/list/{catelogId}
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R getBaseAttr(@RequestParam Map<String, Object> params,
                         @PathVariable("catelogId") Long catelogId,
                         @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryBaseAttr(params, catelogId, attrType);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
//        AttrEntity attr = attrService.getById(attrId);
        AttrVo attrVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo) {
        attrService.saveAttr(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo) {
//        attrService.updateById(attr);
        attrService.updateAttr(attrVo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
