package com.jsdc.iotpt.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Order(1)
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    private static RedisUtils redisUtils;

    private ValueOperations<String, Object> valueOperations;

    @PostConstruct
    public void init() {
        redisUtils = this;
        redisUtils.redisTemplate = this.redisTemplate;
        this.valueOperations = redisUtils.redisTemplate.opsForValue();
    }

    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;


    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public static boolean existsKey(String key) {
        return redisUtils.redisUtils.redisTemplate.hasKey(key);
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     *
     * @param oldKey
     * @param newKey
     */
    public static void renameKey(String oldKey, String newKey) {
        redisUtils.redisTemplate.rename(oldKey, newKey);
    }

    /**
     * newKey不存在时才重命名
     *
     * @param oldKey
     * @param newKey
     * @return 修改成功返回true
     */
    public static boolean renameKeyNotExist(String oldKey, String newKey) {
        return redisUtils.redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 删除key
     *
     * @param key
     */
    public static void deleteKey(String key) {
        redisUtils.redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     *
     * @param keys
     */
    public static void deleteKey(String... keys) {
        Set<String> kSet = Stream.of(keys).map(k -> k).collect(Collectors.toSet());
        redisUtils.redisTemplate.delete(kSet);
    }

    /**
     * 删除Key的集合
     *
     * @param keys
     */
    public static void deleteKey(Collection<String> keys) {
        Set<String> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
        redisUtils.redisTemplate.delete(kSet);
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    public static void expireKey(String key, long time, TimeUnit timeUnit) {
        redisUtils.redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public static void expireKeyAt(String key, Date date) {
        redisUtils.redisTemplate.expireAt(key, date);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public static long getKeyExpire(String key, TimeUnit timeUnit) {
        return redisUtils.redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public static void persistKey(String key) {
        redisUtils.redisTemplate.persist(key);
    }

    /**
     * 根据key取值
     * @param key
     * @return
     */
    public static Object getBeanValue(String key) {
        return redisUtils.valueOperations.get(key);
    }

    /**
     * 保存bean至redis
     * @param key
     * @param bean
     */
    public static void setBeanValue(String key, Object bean) {
        redisUtils.valueOperations.set(key, bean);
    }

    private static void setDbIndex(Integer dbIndex) {
        if (dbIndex == null || dbIndex > 15 || dbIndex < 0) {
            dbIndex = 0;
        }
        LettuceConnectionFactory jedisConnectionFactory = (LettuceConnectionFactory) redisUtils.redisTemplate.getConnectionFactory();
        jedisConnectionFactory.setDatabase(dbIndex);
        redisUtils.redisTemplate.setConnectionFactory(jedisConnectionFactory);
    }
    /**
     * 保存bean至redis并设置过期时间
     * @param bean
     * @param key
     * @param date 分钟
     */
    public static void setBeanValue(String key, Object bean, Integer date) {
        redisUtils.valueOperations.set(key, bean, date, TimeUnit.MINUTES);
    }
    /**
     * 保存bean至redis并设置过期时间
     * @param bean
     * @param key
     * @param date 分钟
     */
    public static void setBeanValue(String key, Object bean, long date) {
        redisUtils.valueOperations.set(key, bean, date, TimeUnit.MINUTES);
    }
    /**
     * 保存bean至redis并设置过期时间
     * @param bean
     * @param key
     * @param date 分钟
     */
    public static void setBeanValueTimeOutTime(String key, Object bean, long date,final TimeUnit timeUnit) {
        redisUtils.valueOperations.set(key, bean, date, timeUnit);
    }

    /**
     * 获取提醒信息
     * @param key
     * @return
     */
    public static String reminding(String key){
        List<Map<String, String>> list2 = (List<Map<String, String>>) redisUtils.getBeanValue("sys_configs");
        for (Map<String, String> map : list2) {
            if (map.get("config_key").equals(key)){
                return map.get("config_value");
            }
        }
        return "";
    }


    /**
     * redis乐观锁
     * @param key
     * @return
     */
    public static boolean OptimisticLock(String key) {
        Integer outTime = 120000;
        return (Boolean) redisUtils.redisTemplate.execute((RedisCallback) connection -> {
            Boolean flag = connection.setNX(key.getBytes(), String.valueOf(System.currentTimeMillis() + outTime + 1).getBytes());
            if (!flag) {
                byte[] value = connection.get(key.getBytes());
                if (Objects.nonNull(value) && value.length > 0) {
                    long expireTime = Long.parseLong(new String(value));
                    if (expireTime < System.currentTimeMillis()) {
                        return Long.parseLong(new String(connection.getSet(key.getBytes(), String.valueOf(System.currentTimeMillis() + outTime + 1).getBytes()))) < System.currentTimeMillis();
                    }
                }
            }
            return flag;
        });
    }

    /**
     * 删除乐观锁
     * @param key
     * @return
     */
    public static Long delOptimisticLock(String key) {
        return (Long) redisUtils.redisTemplate.execute((RedisCallback) connection -> {
            return connection.del(key.getBytes());
        });
    }
}
