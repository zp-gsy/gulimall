package com.example.gulimall.product.controller;

import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.gulimall.product.entity.ProductAttrValueEntity;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.vo.AttrVo;
import com.example.gulimall.product.vo.ProductAttrVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;


/**
 * 商品属性
 *
 * @author zp
 * @email 381057593@qq.com
 * @date 2022-11-20 19:15:39
 */
@RestController
@RequestMapping("product/attr")
@RequiredArgsConstructor
public class AttrController {
    private final AttrService attrService;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;



    @GetMapping("/testRedisson")
    public String test() {

        RLock lock = redissonClient.getLock("test-lock");
     //   RLock xx = redissonClient.getFairLock("xx"); 公平锁
//        redissonClient.getReadWriteLock()  读写锁
        lock.lock();
//        lock.lock(10,TimeUnit.SECONDS);
        /**
         *  lock  指定超时时间，就会发送给redis lua脚本，并且不会自动续期
         *
         *  lock 未指定超时时间，会自动默认一个时间 internalLockLeaseTime 【30s】 ，并且加锁之后会有个定时任务
         *  internalLockLeaseTime/3 即每隔30/3 = 10s 之后会重新设置过期时间
         *
         */
        // 最佳实战
        /**
         * lock 指定超时时间 并且 业务执行成功之后 手动解锁
         */
        try {
            System.out.println("加锁成功,执行业务代码...." + Thread.currentThread().getName() + " " +Thread.currentThread().getId());
            TimeUnit.SECONDS.sleep(40);
        }catch (Exception e){

        }finally {
            System.out.println("解锁..." + Thread.currentThread().getName() + " " +Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }

    @PostMapping("/testRedis")
    public R testRedis() {
//        List<AttrVo> list = attrService.testRedis();
        List<AttrVo> list = attrService.testRedisLock();
        return R.ok().put(list);
    }

    /**
     * 获取spu规格
     * /product/attr/update/{spuId}
     */
    @PostMapping("/update/{spuId}")
    public R updateBySpuId(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrVo> productAttrVo) {
        attrService.updateBySpuId(spuId, productAttrVo);
        return R.ok();
    }

    /**
     * 获取spu规格
     * product/attr/base/listforspu/{spuId}
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R getListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> list = attrService.getListForSpu(spuId);
        return R.ok().put(list);
    }

    /**
     * 获取分类规格参数
     * /product/attr/base/list/{catelogId}
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R getBaseAttr(@RequestParam Map<String, Object> params,
                         @PathVariable("catelogId") Long catelogId,
                         @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryBaseAttr(params, catelogId, attrType);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
//        AttrEntity attr = attrService.getById(attrId);
        AttrVo attrVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo) {
        attrService.saveAttr(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo) {
//        attrService.updateById(attr);
        attrService.updateAttr(attrVo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

    /**
     * redis 写锁
     */
    @RequestMapping("/write-lock")
    public String testWriteLock() throws InterruptedException {
        RReadWriteLock lock = redissonClient.getReadWriteLock("test-read-lock");
        RLock rLock = lock.readLock();
        rLock.lock();
        String s;
        try {
            s = UUID.randomUUID().toString();

            Thread.sleep(5000);
            stringRedisTemplate.opsForValue().set("testLock", s);
        }finally {
            rLock.unlock();
        }
        return s;
    }

    @RequestMapping("read-lock")
    public String testReadLock() throws InterruptedException {
        RReadWriteLock lock = redissonClient.getReadWriteLock("test-read-lock");
        RLock rLock = lock.writeLock();
        rLock.lock();
        String str = "";
        try {
            str = stringRedisTemplate.opsForValue().get("testLock");
        }finally {
            rLock.unlock();
        }
        return str;
    }

    /**
     * 闭锁
     */
    @RequestMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch("lockDoor");
        rCountDownLatch.trySetCount(5);
        // 阻塞等待
        rCountDownLatch.await();

        return "关门了";

    }

    @RequestMapping("/go")
    public String go() throws InterruptedException {
        RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch("lockDoor");
        rCountDownLatch.countDown();
        System.out.println("走一个");
        return "ok";

    }

    @RequestMapping("ss")
    public String ssTest(){
        System.out.println("ss");
        RSemaphore rSemaphore = redissonClient.getSemaphore("park");
        rSemaphore.release();
        return "ok";
    }

    @RequestMapping("ss1")
    public String ss1Test() throws InterruptedException {
        System.out.println("ss1");
        RSemaphore rSemaphore = redissonClient.getSemaphore("park");
        rSemaphore.acquire();
        return "ok";
    }

    /**
     * redis 保证最终一致性的解决办法： 先更新数据库再删除数据+ 自动过期时间 或者 加 分布式读写锁 或者 第三方开源框架 canal
     */

    public static ExecutorService executors = Executors.newFixedThreadPool(4);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main 执行");
//        Thread01 thread01 = new Thread01();
//        thread01.start();

//        Thread02 thread02 = new Thread02();
//        new Thread(thread02).start();

//        Thread03 thread03 = new Thread03();
//        FutureTask<Object> future = new FutureTask<>(thread03);
//        new Thread(future).start();
//        Object o = future.get();
        executors.submit(new Thread01());
        System.out.println("main 结束:");
    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("子线程 执行");
            int i = 10 /2;
            System.out.println("子线程 结束...." + i);
        }
    }

    public static class Thread02 implements Runnable{

        @Override
        public void run() {
            System.out.println("子线程 执行");
            int i = 10 /2;
            System.out.println("子线程 结束...." + i);
        }
    }

    public static class Thread03 implements Callable {


        @Override
        public Object call() throws Exception {
            System.out.println("子线程 执行");
            int i = 10 /2;
            System.out.println("子线程 结束...." + i);
            return i;
        }
    }

    /**
     * 线程池运行原理
     *                               int corePoolSize,  核心线程数 线程池创建好就就绪的线程数量
     *                               int maximumPoolSize, 最大线程池数量
     *                               long keepAliveTime, 等到任务全部执行完成,最大线程池数-核心线程池数 保持存活时间
     *                               TimeUnit unit, 存活时间单位
     *                               BlockingQueue<Runnable> workQueue, 阻塞队列，
     *                               ThreadFactory threadFactory, 线程工厂
     *                               RejectedExecutionHandler handler 拒绝策略
     *
     *   任务进来，先分配给核心线程数,如果超过核心线程池数量，则放入阻塞队列，阻塞队列满了 则开启最大线程池数量
     *   如果还是有任务进来，则执行拒绝策略
     *   如果全部执行完成，则存活时间过期之后， 关闭 最大线程池数量-核心线程数
     *   LinkedBlockingQueue 不传入参数 默认是 integer最大值
     *   线程工厂
     *
     */
    public static class Thread04{
       ThreadPoolExecutor executor =  new ThreadPoolExecutor(4,
               200,
               20,
               TimeUnit.SECONDS,
               new LinkedBlockingQueue<>(5),
               Executors.defaultThreadFactory(),
               new ThreadPoolExecutor.AbortPolicy());

        public void test() throws ExecutionException, InterruptedException {
            Future<String> submit = executor.submit(() -> {
                return "111";
            });
            submit.get();
        }

        /**
         * 异步编排
         */
        public void test02(){
            CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(()->{
                System.out.println("main 开始");
                System.out.println("查询商品信息");
                return "华为";
            },executor);

            CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(()->{
                System.out.println("查询商品图片");
                return "img";
            },executor);

            CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(()->{
                System.out.println("查询商品描述");
                return "desc";
            },executor);

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futureAttr, futureImg, futureDesc);
            System.out.println("main 主线程结束");
        }
    }

    static class Vo<V extends List,M extends List>{

        private List<V> leftList;

        private List<M> rightList;

        boolean put(List collection, ZPredicate zpredicate){
            if(zpredicate.test()){
                 return leftList.add((V) collection);
            }else {
                return rightList.add((M) collection);
            }
        }

        public List<M> getRightList() {
            return rightList;
        }

        public List<V> getLeftList() {
            return leftList;
        }


    }


}
@FunctionalInterface
interface ZPredicate{
    boolean test();
}
