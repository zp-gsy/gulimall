package com.example.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.member.dao.UmsMemberCollectSubjectDao;
import com.example.gulimall.member.entity.UmsMemberCollectSubjectEntity;
import com.example.gulimall.member.service.UmsMemberCollectSubjectService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("umsMemberCollectSubjectService")
public class UmsMemberCollectSubjectServiceImpl extends ServiceImpl<UmsMemberCollectSubjectDao, UmsMemberCollectSubjectEntity> implements UmsMemberCollectSubjectService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UmsMemberCollectSubjectEntity> page = this.page(
                new Query<UmsMemberCollectSubjectEntity>().getPage(params),
                new QueryWrapper<UmsMemberCollectSubjectEntity>()
        );

        return new PageUtils(page);
    }

}