package com.scrs.utils;

/**
 * 缓存客户端工具类，提供与Redis的交互操作，包括数据的存储、获取和缓存重建等功能。
 *
 *
 */

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Component
public class CacheClient {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 默认缓存过期时间，单位：秒
    private static final Long CACHE_TTL = 60L;

    // 缓存重建线程池，大小为10
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 将任意Java对象序列化为JSON并存储到Redis中，设置过期时间
     *
     * @param key   Redis键
     * @param value 要缓存的Java对象
     * @param time  过期时间，单位：秒
     */
    public void set(String key, Object value, Long time) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, TimeUnit.SECONDS);
    }

    /**
     * 将任意Java对象序列化为JSON并存储到Redis中，设置逻辑过期时间，解决缓存击穿问题
     *
     * @param key   Redis键
     * @param value 要缓存的Java对象
     * @param time  逻辑过期时间，单位：秒
     */
    public void setWithLogicalExpire(String key, Object value, Long time) {
        // 创建封装了数据和逻辑过期时间的对象
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(time));

        // 将对象序列化后存入Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 解决缓存穿透：通过先查询缓存，再查询数据库，并将结果缓存起来的方法
     *
     * @param id         数据ID
     * @param type       数据类型的Class对象
     * @param dbFallback 缓存未命中时的数据库查询方法
     * @param time       缓存过期时间，单位：秒
     * @param <T>        数据类型
     * @param <ID>       ID类型
     * @return 查询到的数据
     */
    public <T, ID> T queryWithPassThrough(ID id, Class<T> type, Function<ID, T> dbFallback, Long time) {
        String key = "Cache:" + type.getName() + ":" + id;
        // 从Redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 判断缓存是否命中
        if (StrUtil.isNotBlank(json)) {
            // 命中，返回反序列化后的数据
            return JSONUtil.toBean(json, type);
        }
        // 判断缓存是否为空字符串，表示之前缓存过空值
        if (json != null) {
            // 返回null表示数据不存在
            return null;
        }
        // 未命中，查询数据库
        T data = dbFallback.apply(id);
        if (data == null) {
            // 数据不存在，缓存空值，防止缓存穿透
            stringRedisTemplate.opsForValue().set(key, "", time, TimeUnit.SECONDS);
            return null;
        }
        // 数据存在，写入Redis缓存
        this.set(key, data, time);
        return data;
    }

    /**
     * 尝试获取互斥锁，解决缓存击穿问题
     *
     * @param key 锁的键
     * @return 是否成功获取锁
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(flag);
    }

    /**
     * 释放互斥锁
     *
     * @param key 锁的键
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 解决缓存击穿：逻辑过期+互斥锁的方法
     */
    public <T> T queryWithLogicalExpire(String key, Class<T> type, Supplier<T> dbFallback, Long time) {
        // 从Redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 判断缓存是否存在
        if (StrUtil.isBlank(json)) {
            // 缓存不存在，从数据库中读取
            log.info("缓存不存在，从数据库中读取{}",key);
            String lockKey = "lock:" + key;
            boolean isLock = tryLock(lockKey);
            if (isLock) {
                // 成功获取锁，开启独立线程进行缓存重建
                try {
                    return CACHE_REBUILD_EXECUTOR.submit(() -> {
                        try {
                            // 查询数据库
                            T t = dbFallback.get();
                            // 写入Redis缓存
                            this.setWithLogicalExpire(key, t, time);
                            return t;

                        } catch (Exception e) {
                            log.error("缓存重建失败", e);
                            return null;
                        } finally {
                            // 释放锁
                            unlock(lockKey);
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("缓存重建线程执行失败", e);
                    return null;
                }
            }
        }

        //log.info("stringRedisTemplate.opsForValue().get(key){}", json);

        // 反序列化JSON为RedisData对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        // 获取实际数据
        T data = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        // 获取逻辑过期时间
        LocalDateTime expireTime = redisData.getExpireTime();
        // 判断缓存是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 未过期，直接返回数据
            return data;
        }
        // 已过期，尝试获取互斥锁
        String lockKey = "lock:" + key;
        boolean isLock = tryLock(lockKey);
        if (isLock) {
            // 成功获取锁，开启独立线程进行缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 查询数据库
                    T t = dbFallback.get();
                    // 写入Redis缓存
                    this.setWithLogicalExpire(key, t, time);

                } catch (Exception e) {
                    log.error("缓存重建失败", e);
                } finally {
                    // 释放锁
                    unlock(lockKey);
                }
            });
        }
        // 返回旧数据
        return data;
    }
}