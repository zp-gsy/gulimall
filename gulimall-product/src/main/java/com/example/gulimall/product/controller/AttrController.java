package com.example.gulimall.product.controller;

import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.vo.AttrVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


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


    /**
     * 获取分类规格参数
     * /product/attr/base/list/{catelogId}
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R getBaseAttr(@RequestParam Map<String, Object> params,
                         @PathVariable("catelogId") Long catelogId,
                         @PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryBaseAttr(params, catelogId, attrType);
        return R.ok().put("page",page);
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
