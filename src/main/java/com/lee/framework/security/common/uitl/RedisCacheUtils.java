package com.lee.framework.security.common.uitl;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.params.ScanParams;
//import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisCacheUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);

    private static JedisPool jedisPool;

    public static void setJedisPool(JedisPool jedisPool) {
        RedisCacheUtils.jedisPool = jedisPool;
    }

    private RedisCacheUtils() {}

    /**
     * 从redis中取值
     *
     * @param key   键
     * @param clazz 值得类型
     * @param <T>   值的泛型
     * @return 返回存入redis的，经过json反序列化后数据
     */
    public static <T> T get(String key, Class<T> clazz) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            String value = redis.get(key);
            return stringToBean(value, clazz);
        } catch (Exception e) {
            logger.error("从redis读数据{get}操作发生异常", e);
            return null;
        } finally {
            release(redis);
        }
    }

    /**
     * 从redis中取值
     *
     * @param key 键
     * @return 返回存入redis的，经过json反序列化后数据
     */
    public static String get(String key) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            return redis.get(key);
        } catch (Exception e) {
            logger.error("从redis读数据{get}操作发生异常", e);
            return null;
        } finally {
            release(redis);
        }
    }

    /**
     * 为key设置value，不过期
     *
     * @param key 键
     * @param <T> 值的泛型
     * @return 设置成功返回true
     */
    public static <T> Boolean set(String key, T value) {
        return set(key, value, -1);
    }

    /**
     * 为key设置value，并指定key的过期时间
     *
     * @param key 键
     * @param <T> 值的泛型
     * @return 设置成功返回true
     */
    public static <T> Boolean set(String key, T value, int expireTimeSeconds) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null) {
                return false;
            }
            if (expireTimeSeconds <= 0) {
                redis.set(key, str);
            } else {
                redis.setex(key, expireTimeSeconds, str);
            }
            return true;
        } catch (Exception e) {
            logger.error("从redis写数据{set}操作发生异常", e);
            return null;
        } finally {
            release(redis);
        }
    }

    /**
     * 根据key删除value
     *
     * @param key 键
     * @return 删除成功返回true
     */
    public static Boolean delete(String key) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            Long del = redis.del(key);
            return del > 0L;
        } catch (Exception e) {
            logger.error("从redis写数据{delete}操作发生异常", e);
            return null;
        } finally {
            release(redis);
        }
    }

    /**
     * 判断redis是否存在key
     *
     * @param key 键
     * @return 存在key返回true
     */
    public static boolean exists(String key) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            Boolean exists = redis.exists(key);
            return exists != null && exists;
        } catch (Exception e) {
            logger.error("从redis读数据{exists}操作发生异常", e);
            return false;
        } finally {
            release(redis);
        }
    }

    /**
     * redis中key自增
     *
     * @param key 键
     * @return 自增后的数据
     */
    public static Long incr(String key) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            return redis.incr(key);
        } catch (Exception e) {
            logger.error("从redis写数据{incr}操作发生异常", e);
            return null;
        } finally {
            release(redis);
        }
    }

    /**
     * 指定key在多少秒后过期
     *
     * @param key               键
     * @param expireTimeSeconds 过期时间，单位秒
     */
    public static void expireAfter(String key, int expireTimeSeconds) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            redis.expire(key, expireTimeSeconds);
        } catch (Exception e) {
            logger.error("从redis写数据{expire}操作发生异常", e);
        } finally {
            release(redis);
        }
    }

    /**
     * 获取hash结构中field的属性值
     *
     * @param key   hash键名
     * @param field hash字段名
     * @return hash字段对应的值
     */
    public static String hget(String key, String field) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            return redis.hget(key, field);
        } catch (Exception e) {
            logger.error("从redis读数据{hget}操作发生异常", e);
            return null;
        } finally {
            release(redis);
        }
    }

    /**
     * 获取hash结构中所有field的属性键值对
     *
     * @param key hash键名
     * @return hash结构
     */
    public static void hmset(String key, Map<String, String> map) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            redis.hmset(key, map);
        } catch (Exception e) {
            logger.error("从redis读数据{hgetAll}操作发生异常", e);
        } finally {
            release(redis);
        }
    }

    public static void hmset(String key, Map<String, String> map, int expireTimeSeconds) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            redis.hmset(key, map);
            if (expireTimeSeconds > 0) {
                expireAfter(key, expireTimeSeconds);
            }
        } catch (Exception e) {
            logger.error("从redis读数据{hgetAll}操作发生异常", e);
        } finally {
            release(redis);
        }
    }

    /**
     * 获取hash结构中所有field的属性键值对
     *
     * @param key hash键名
     * @return hash结构
     */
    public static Map<String, String> hgetAll(String key) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            return redis.hgetAll(key);
        } catch (Exception e) {
            logger.error("从redis读数据{hgetAll}操作发生异常", e);
            return null;
        } finally {
            release(redis);
        }
    }

    /**
     * 获取hash结构中field的属性值
     *
     * @param key        hash键名
     * @param field      hash字段名
     * @param fieldValue hash字段值
     */
    public static void hset(String key, String field, String fieldValue) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            redis.hset(key, field, fieldValue);
        } catch (Exception e) {
            logger.error("从redis写数据{hset}操作发生异常", e);
        } finally {
            release(redis);
        }
    }

    /**
     * 获取hash结构中field的属性值
     *
     * @param key        hash键名
     * @param field      hash字段名
     * @param fieldValue hash字段值
     */
    public static void hset(String key, String field, String fieldValue, int expireTimeSeconds) {
        Jedis redis = null;
        try {
            redis = jedisPool.getResource();
            hset(key, field, fieldValue);
            if (expireTimeSeconds > 0) {
                expireAfter(key, expireTimeSeconds);
            }
        } catch (Exception e) {
            logger.error("从redis写数据{hset}操作发生异常", e);
        } finally {
            release(redis);
        }
    }

    /**
     * 查询返回符合某一表达式规则的key的列表集合，例如：user:*，返回以user:开头的key的列表
     *
     * @param keyPattern 表达式
     * @return 列表集合
     */
//    public static List<String> scan(String keyPattern) {
//        Jedis redis = null;
//        try {
//            redis = jedisPool.getResource();
//            ScanParams scanParams = new ScanParams();
//            scanParams.match(keyPattern);
//            scanParams.count(Integer.MAX_VALUE);
//            ScanResult<String> scanResult = redis.scan(ScanParams.SCAN_POINTER_START, scanParams);
//            return scanResult.getResult();
//        } catch (Exception e) {
//            logger.error("从redis读数据{scan}操作发生异常", e);
//            return new ArrayList<>();
//        } finally {
//            release(redis);
//        }
//    }

    /**
     * java bean转字符串
     *
     * @param value 值
     * @param <T>   泛型
     * @return json工具序列化
     */
    private static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if ((clazz == Integer.TYPE) || (clazz == Integer.class) || (clazz == Long.TYPE) || (clazz == Long.class)) {
            return String.valueOf(value);
        } else if (clazz == String.class) {
            return (String)value;
        }
        return JSONUtil.toJsonStr(value);
    }

    /**
     * 字符串转java bean
     *
     * @param value 值
     * @param clazz 类型
     * @param <T>   泛型
     * @return json工具反序列化
     */
    @SuppressWarnings("unchecked")
    private static <T> T stringToBean(String value, Class<T> clazz) {
        if (value == null || clazz == null) {
            return null;
        }
        if ((clazz == Integer.TYPE) || (clazz == Integer.class)) {
            return (T)Integer.valueOf(value);
        }
        if ((clazz == Long.TYPE) || (clazz == Long.class)) {
            return (T)Long.valueOf(value);
        }
        if (clazz == String.class) {
            return (T)value;
        }
        return JSONUtil.toBean(value, clazz);
    }

    /**
     * 释放链接
     *
     * @param jedis {@link Jedis}
     */
    private static void release(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
