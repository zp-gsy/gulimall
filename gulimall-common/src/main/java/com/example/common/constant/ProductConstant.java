package com.example.common.constant;

import lombok.Getter;

/**
 * @author zp
 * @date 2022/12/14
 * @apiNote
 */
public class ProductConstant {

    @Getter
    public enum ProductAttrEnumConstant{
        ATTR_TYPE_SALE(0,"销售属性"),
        ATTR_TYPE_BASE(1, "基础属性");

        private Integer code;

        private String msg;

        ProductAttrEnumConstant(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
