package com.example.common.constant;

import lombok.Getter;

/**
 * @author zp
 * @date 2022/12/27
 * @apiNote
 */
public class WareConstant {

    @Getter
    public enum PurchaseStatusEnum{
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        RECEIVE(2,"已领取"),
        FINISH(3,"已完成"),
        HASERROR(4,"有异常");


        private Integer code;
        private String msg;

        PurchaseStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Getter
    public enum PurchaseDetailStatusEnum{
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISH(3,"已完成"),
        HASERROR(4,"采购失败");


        private Integer code;
        private String msg;

        PurchaseDetailStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
