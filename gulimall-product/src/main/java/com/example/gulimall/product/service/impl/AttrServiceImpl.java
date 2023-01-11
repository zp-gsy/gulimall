package com.example.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.ProductConstant;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.AttrDao;
import com.example.gulimall.product.dao.AttrGroupDao;
import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.gulimall.product.entity.AttrEntity;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.entity.ProductAttrValueEntity;
import com.example.gulimall.product.service.AttrAttrgroupRelationService;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.service.ProductAttrValueService;
import com.example.gulimall.product.vo.AttrResVo;
import com.example.gulimall.product.vo.AttrVo;
import com.example.gulimall.product.vo.ProductAttrVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("attrService")
@RequiredArgsConstructor
@Slf4j
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    private final CategoryDao categoryDao;

    private final AttrGroupDao attrGroupDao;

    private final CategoryServiceImpl categoryService;

    private final AttrAttrgroupRelationService attrAttrgroupRelationService;

    private final ProductAttrValueService productAttrValueService;

    private Map<String, Object> map = new HashMap<>();

    private final StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId, String attrType) {

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type",
                        attrType.equalsIgnoreCase("base") ?
                                ProductConstant.ProductAttrEnumConstant.ATTR_TYPE_BASE.getCode() :
                                ProductConstant.ProductAttrEnumConstant.ATTR_TYPE_SALE.getCode()
                );
        String key = (String) params.get("key");
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        if (!ObjectUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );


        List<AttrResVo> list = page.getRecords().stream().map((obj) -> {
            AttrResVo resVo = new AttrResVo();
            BeanUtils.copyProperties(obj, resVo);
            CategoryEntity category = categoryDao.selectById(obj.getCatelogId());
            //设置分类名字
            if (Objects.nonNull(category)) {
                resVo.setCatelogName(category.getName());
            }
            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", obj.getAttrId());
            AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.getOne(queryWrapper);
            //设置分组名字
            if (Objects.nonNull(relation)) {
                Long attrGroupId = relation.getAttrGroupId();
                resVo.setGroupName(attrGroupDao.selectById(attrGroupId).getAttrGroupName());
            }
            return resVo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(list);
        return pageUtils;
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attr = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attr);
        //保存基本信息
        this.save(attr);
        if (Objects.equals(ProductConstant.ProductAttrEnumConstant.ATTR_TYPE_BASE.getCode(), attrVo.getAttrType()) && attrVo.getAttrGroupId() != null) {
            //保存关联关系
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrGroupId(attrVo.getAttrGroupId());
            entity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationService.save(entity);
        }

    }

    @Override
    public AttrVo getAttrInfo(Long attrId) {
        AttrVo attrVo = new AttrVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrVo);
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id", attrEntity.getAttrId());
        AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.getOne(queryWrapper);
        if (Objects.nonNull(relation)) {
            attrVo.setAttrGroupId(relation.getAttrGroupId());
        }

        Long[] pathByCatId = categoryService.findPathByCatId(attrEntity.getCatelogId());
        attrVo.setCatelogPath(pathByCatId);
        return attrVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, entity);
        this.updateById(entity);
        if (Objects.equals(ProductConstant.ProductAttrEnumConstant.ATTR_TYPE_BASE.getCode(), attrVo.getAttrType()) && attrVo.getAttrGroupId() != null) {
            //更新关联关系
            QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrId(entity.getAttrId());
            relation.setAttrGroupId(attrVo.getAttrGroupId());
            wrapper.eq("attr_id", entity.getAttrId());
            //存在记录就更新 没有记录说明之前没有关联 需要新增关联关系
            attrAttrgroupRelationService.saveOrUpdate(relation, wrapper);
        }
    }

    @Override
    public PageUtils getAttrgroupNoRelation(Map<String, Object> params, Long attrgroupId) {

        List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationService
                .lambdaQuery()
                .list();

        List<Long> attrIds = relationEntityList.stream().map(obj -> {
            return obj.getAttrId();
        }).collect(Collectors.toList());


        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.notIn("attr_id", attrIds).eq("attr_type", ProductConstant.ProductAttrEnumConstant.ATTR_TYPE_BASE.getCode());
        String key = (String) params.get("key");
        if (Objects.nonNull(key)) {
            wrapper.and(t -> {
                t.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Override
    public List<ProductAttrValueEntity> getListForSpu(Long spuId) {
        return productAttrValueService.lambdaQuery().eq(ProductAttrValueEntity::getSpuId, spuId).list();
    }

    @Transactional
    @Override
    public void updateBySpuId(Long spuId, List<ProductAttrVo> productAttrVo) {
        //先删后插入
        productAttrValueService.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        List<ProductAttrValueEntity> collect = productAttrVo.stream().map(t -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuId);
            BeanUtils.copyProperties(t, productAttrValueEntity);
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);
    }

    @Override
    public void test(String id) {
        System.out.println("===");
        System.out.println(Thread.currentThread().getName() + "========" + map.hashCode());
        System.out.println(Thread.currentThread().getName() + "========" + map.toString());
        if ("0".equals(id)) {
            map.put(id, id);
        }

        System.out.println(Thread.currentThread().getName() + "====" + map.get(id.equals("1") ? "0" : "0"));
    }

    @Override
    public List<AttrVo> testRedis() {
        /**
         * 缓存穿透： 大量线程进来请求数据库不存在的数据，导致大量请求落到DB中，解决办法: 如果查不到，则进行一个空的存储到数据库中
         * 缓存雪崩： 大量缓存同一时间失效，导致大量查询落到db, 解决办法：缓存的时候添加一个随机时间（1-5分钟）
         * 缓存击穿： 一个热点数据失效，导致大量查询落到DB, 解决办法：添加一把分布式锁，获得锁先查询缓存，缓存没有则进行查询db，如果是本地锁，则会多少个实例，，最多查询多少次
         */
        String json = redisTemplate.opsForValue().get("attrVoJson");
        if (StringUtils.hasLength(json)) {
            List<AttrVo> list = JSON.parseObject(json, new TypeReference<List<AttrVo>>() {
            });
            log.info("testRedis 从redis中获取数据---");
            return list;
        }
        log.info("testRedis 从DB中获取数据---");
        List<AttrVo> list = getAttrVoList();

        return list;
    }

    @Override
    public List<AttrVo> testRedisLock() {
        /**
         * 加锁：占位和 设置 过期时间必须是一个原子操作
         * 解锁：判断值 和 删除key 必须是一个原子操作
         */
        String json = redisTemplate.opsForValue().get("attrVoJson");
        if (StringUtils.hasLength(json)) {
            List<AttrVo> list = JSON.parseObject(json, new TypeReference<List<AttrVo>>() {
            });
            log.info("testRedis 从redis中获取数据---");
            return list;
        }
        //缓存中没有
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            log.info("redis 占锁成功...." + uuid);
            //开始业务代码
            List<AttrVo> attrVos;
            try {
                attrVos = getAttrVoListFromRedis();
            } finally {
                //释放锁
                log.info("redis 释放锁...." + uuid);
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);

            }
            return attrVos;

        } else {
            log.info("redis 占锁失败....等待200ms 继续..");
            //自旋
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
            }
            return testRedisLock();
        }

    }

    private List<AttrVo> getAttrVoList() {
        synchronized (this) {
            String json = redisTemplate.opsForValue().get("attrVoJson");
            if (StringUtils.hasLength(json)) {
                List<AttrVo> list = JSON.parseObject(json, new TypeReference<List<AttrVo>>() {
                });
                log.info("testRedis 从redis中获取数据---");
                return list;
            }
            log.info("进入锁中...");
            List<AttrVo> list = new ArrayList<>();
            for (long i = 0; i < 100; i++) {
                AttrVo attrVo = new AttrVo();
                attrVo.setAttrId(i);
                attrVo.setAttrName("attr_" + i);
                attrVo.setIcon("\n" +
                        "\n" +
                        "<!DOCTYPE html>\n" +
                        "<html lang=\"zh-CN\">\n" +
                        "                                    </a>\n" +
                        "                                    <span class=\"special-column-num\">1篇</span>\n" +
                        "                                </li>\n" +
                        "                                <li>\n" +
                        "                                    <a class=\"clearfix special-column-name\" target=\"_blank\" href=\"https://blog.csdn.net/niuzaiwenjie/category_8083982.html\" data-report-click='{\"mod\":\"popu_537\",\"spm\":\"1001.2101.3001.4137\",\"strategy\":\"pc付费专栏左侧入口\",\"dest\":\"https://blog.csdn.net/niuzaiwenjie/category_8083982.html\",\"ab\":\"new\"}'>\n" +
                        "                                        <div class=\"special-column-bar \"></div>\n" +
                        "                                        <img src=\"https://img-blog.csdnimg.cn/20201014180756927.png?x-oss-process=image/resize,m_fixed,h_64,w_64\" alt=\"\" onerror=\"this.src='https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64'\">\n" +
                        "                                        <span class=\"\">\n" +
                        "                                            idea vim\n" +
                        "                                        </span>\n" +
                        "                                    </a>\n" +
                        "                                    <span class=\"special-column-num\">1篇</span>\n" +
                        "                                </li>\n" +
                        "                                <li>\n" +
                        "                                    <a class=\"clearfix special-column-name\" target=\"_blank\" href=\"https://blog.csdn.net/niuzaiwenjie/category_8113000.html\" data-report-click='{\"mod\":\"popu_537\",\"spm\":\"1001.2101.3001.4137\",\"strategy\":\"pc付费专栏左侧入口\",\"dest\":\"https://blog.csdn.net/niuzaiwenjie/category_8113000.html\",\"ab\":\"new\"}'>\n" +
                        "                                        <div class=\"special-column-bar \"></div>\n" +
                        "                                        <img src=\"https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64\" alt=\"\" onerror=\"this.src='https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64'\">\n" +
                        "                                        <span class=\"\">\n" +
                        "                                            keepalived\n" +
                        "                                        </span>\n" +
                        "                                    </a>\n" +
                        "                                    <span class=\"special-column-num\">1篇</span>\n" +
                        "                                </li>\n" +
                        "                                <li>\n" +
                        "                                    <a class=\"clearfix special-column-name\" target=\"_blank\" href=\"https://blog.csdn.net/niuzaiwenjie/category_8361441.html\" data-report-click='{\"mod\":\"popu_537\",\"spm\":\"1001.2101.3001.4137\",\"strategy\":\"pc付费专栏左侧入口\",\"dest\":\"https://blog.csdn.net/niuzaiwenjie/category_8361441.html\",\"ab\":\"new\"}'>\n" +
                        "                                        <div class=\"special-column-bar \"></div>\n" +
                        "                                        <img src=\"https://img-blog.csdnimg.cn/20201014180756928.png?x-oss-process=image/resize,m_fixed,h_64,w_64\" alt=\"\" onerror=\"this.src='https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64'\">\n" +
                        "                                        <span class=\"\">\n" +
                        "                                            websocket\n" +
                        "                                        </span>\n" +
                        "                                    </a>\n" +
                        "                                    <span class=\"special-column-num\">1篇</span>\n" +
                        "                                </li>\n");
                list.add(attrVo);
            }
            redisTemplate.opsForValue().set("attrVoJson", JSON.toJSONString(list));
            return list;
        }

    }

    private List<AttrVo> getAttrVoListFromRedis() {
        String json = redisTemplate.opsForValue().get("attrVoJson");
        if (StringUtils.hasLength(json)) {
            List<AttrVo> list = JSON.parseObject(json, new TypeReference<List<AttrVo>>() {
            });
            log.info("testRedis 从redis中获取数据---");
            return list;
        }
        log.info("进入锁中...");
        List<AttrVo> list = new ArrayList<>();
        for (long i = 0; i < 100; i++) {
            AttrVo attrVo = new AttrVo();
            attrVo.setAttrId(i);
            attrVo.setAttrName("attr_" + i);
            attrVo.setIcon("\n" +
                    "\n" +
                    "<!DOCTYPE html>\n" +
                    "<html lang=\"zh-CN\">\n" +
                    "                                    </a>\n" +
                    "                                    <span class=\"special-column-num\">1篇</span>\n" +
                    "                                </li>\n" +
                    "                                <li>\n" +
                    "                                    <a class=\"clearfix special-column-name\" target=\"_blank\" href=\"https://blog.csdn.net/niuzaiwenjie/category_8083982.html\" data-report-click='{\"mod\":\"popu_537\",\"spm\":\"1001.2101.3001.4137\",\"strategy\":\"pc付费专栏左侧入口\",\"dest\":\"https://blog.csdn.net/niuzaiwenjie/category_8083982.html\",\"ab\":\"new\"}'>\n" +
                    "                                        <div class=\"special-column-bar \"></div>\n" +
                    "                                        <img src=\"https://img-blog.csdnimg.cn/20201014180756927.png?x-oss-process=image/resize,m_fixed,h_64,w_64\" alt=\"\" onerror=\"this.src='https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64'\">\n" +
                    "                                        <span class=\"\">\n" +
                    "                                            idea vim\n" +
                    "                                        </span>\n" +
                    "                                    </a>\n" +
                    "                                    <span class=\"special-column-num\">1篇</span>\n" +
                    "                                </li>\n" +
                    "                                <li>\n" +
                    "                                    <a class=\"clearfix special-column-name\" target=\"_blank\" href=\"https://blog.csdn.net/niuzaiwenjie/category_8113000.html\" data-report-click='{\"mod\":\"popu_537\",\"spm\":\"1001.2101.3001.4137\",\"strategy\":\"pc付费专栏左侧入口\",\"dest\":\"https://blog.csdn.net/niuzaiwenjie/category_8113000.html\",\"ab\":\"new\"}'>\n" +
                    "                                        <div class=\"special-column-bar \"></div>\n" +
                    "                                        <img src=\"https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64\" alt=\"\" onerror=\"this.src='https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64'\">\n" +
                    "                                        <span class=\"\">\n" +
                    "                                            keepalived\n" +
                    "                                        </span>\n" +
                    "                                    </a>\n" +
                    "                                    <span class=\"special-column-num\">1篇</span>\n" +
                    "                                </li>\n" +
                    "                                <li>\n" +
                    "                                    <a class=\"clearfix special-column-name\" target=\"_blank\" href=\"https://blog.csdn.net/niuzaiwenjie/category_8361441.html\" data-report-click='{\"mod\":\"popu_537\",\"spm\":\"1001.2101.3001.4137\",\"strategy\":\"pc付费专栏左侧入口\",\"dest\":\"https://blog.csdn.net/niuzaiwenjie/category_8361441.html\",\"ab\":\"new\"}'>\n" +
                    "                                        <div class=\"special-column-bar \"></div>\n" +
                    "                                        <img src=\"https://img-blog.csdnimg.cn/20201014180756928.png?x-oss-process=image/resize,m_fixed,h_64,w_64\" alt=\"\" onerror=\"this.src='https://img-blog.csdnimg.cn/20201014180756922.png?x-oss-process=image/resize,m_fixed,h_64,w_64'\">\n" +
                    "                                        <span class=\"\">\n" +
                    "                                            websocket\n" +
                    "                                        </span>\n" +
                    "                                    </a>\n" +
                    "                                    <span class=\"special-column-num\">1篇</span>\n" +
                    "                                </li>\n");
            list.add(attrVo);
        }
        redisTemplate.opsForValue().set("attrVoJson", JSON.toJSONString(list));
        return list;
    }

}