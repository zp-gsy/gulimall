package com.example.gulimall.product.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author zp
 * @date 2022/12/18
 * @apiNote
 */
@Data
@Builder
public class BrandVo {

    /**
     *
     * 		"brandId": 0,
     * 		"brandName": "string",
     */

    private Long brandId;

    private String brandName;
}
