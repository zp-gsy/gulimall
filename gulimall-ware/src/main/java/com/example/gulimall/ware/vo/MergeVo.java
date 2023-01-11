package com.example.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zp
 * @date 2022/12/27
 * @apiNote
 */
@Data
public class MergeVo {

    private Long purchaseId;
    private List<Long> items;

}
