package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zp
 * @date 2022/12/20
 * @apiNote
 */

@Data
public class SpuBoundTo {

    private Long skuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
