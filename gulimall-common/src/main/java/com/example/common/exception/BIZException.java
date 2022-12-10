package com.example.common.exception;

/**
 * @author zp
 * @date 2022/12/10
 * @apiNote
 */

import lombok.Getter;

/**
 * 错误码和错误信息异常类
 * 1：错误码定义规则为5位数字
 * 2：前两位表示业务场景，后三位表示错误码
 * 3：维护错误码需要维护错误信息，定义为枚举方式
 * 错误码列表：
 * 10：通用
 * 000: 系统内部异常
 * 001：参数格式校验
 * 11：商品
 * 12：订单
 * 13：购物车
 * 14：物流
 */
@Getter
public enum BIZException {

    UNKNOWN_EXCEPTION(10000, "系统内部异常"),
    VALIDATOR_EXCEPTION(10001, "参数格式校验异常");

    Integer code;

    String msg;

    BIZException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
