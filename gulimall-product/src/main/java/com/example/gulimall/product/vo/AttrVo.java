package com.example.gulimall.product.vo;

import com.example.gulimall.product.entity.AttrEntity;
import lombok.Data;

/**
 * @author zp
 * @date 2022/12/11
 * @apiNote
 */
@Data
public class AttrVo extends AttrEntity {

    /**
     * 属性分组id
     */
    private Long attrGroupId;

    /**
     * 分类完整路径
     */
    private Long[] catelogPath;


}
