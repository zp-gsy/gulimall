package com.example.common.Constant;

import lombok.Getter;

/**
 * @author zp
 * @date 2022/12/14
 * @apiNote
 */
public class Constant {

    @Getter
    public enum ProductConstant{
        ATTR_TYPE_SALE(0,"销售属性"),
        ATTR_TYPE_BASE(1, "基础属性");

        private Integer code;

        private String msg;

        ProductConstant(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
