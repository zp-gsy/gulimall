package com.example.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.gulimall.product.dao.AttrDao;
import com.example.gulimall.product.entity.AttrEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    AttrDao attrDao;
    @Test
    void contextLoads() {

        AttrEntity attrEntity = new AttrEntity();
        attrEntity.setAttrName("张三");
        attrDao.insert(attrEntity);
        List<AttrEntity> list = attrDao.selectList(new QueryWrapper<>());
        list.forEach(t->{
            System.out.println(t);
        });
    }

}
