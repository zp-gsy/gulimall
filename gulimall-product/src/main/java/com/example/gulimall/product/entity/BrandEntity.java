package com.example.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.validator.Constraints.ListValue;
import com.example.common.validator.group.AddGroup;
import com.example.common.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 19:15:39
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "不可自定义新增品牌id", groups = {AddGroup.class})
	@NotNull(message = "品牌id必填", groups = {UpdateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必填", groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "品牌logo地址必填", groups = {AddGroup.class})
	@URL(message = "品牌logo地址需要是一个合法的URL", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(value={0,1}, groups = {UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "首字母必填", groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]",message = "首字母需要满足a-z或者A-Z", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;

	/**
	 * 排序
	 */
	@NotNull(message = "排序必填", groups = {AddGroup.class})
	@Min(value = 0, message = "排序字段必须正数" , groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
