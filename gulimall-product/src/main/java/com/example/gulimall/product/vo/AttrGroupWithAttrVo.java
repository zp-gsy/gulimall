package com.example.gulimall.product.vo;

import com.example.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author zp
 * @date 2022/12/18
 * @apiNote
 */
@Data
public class AttrGroupWithAttrVo {


    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 属性
     */
    private List<AttrEntity> attrs;
}
