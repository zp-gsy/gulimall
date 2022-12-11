package com.example.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gulimall.member.entity.UmsMemberLoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 20:22:34
 */
@Mapper
public interface UmsMemberLoginLogDao extends BaseMapper<UmsMemberLoginLogEntity> {
	
}
