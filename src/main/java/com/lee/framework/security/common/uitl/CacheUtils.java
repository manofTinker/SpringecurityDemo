package com.lee.framework.security.common.uitl;


import com.lee.framework.security.cache.SimpleCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存工具类，可以支持两种实现：jvm内存缓存或者redis缓存
 */
@SuppressWarnings("all")
public class CacheUtils {

    private static final Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    private static SimpleCacheService simpleCacheService;

    public static void setCacheService(SimpleCacheService simpleCacheService) {
        CacheUtils.simpleCacheService = simpleCacheService;
    }

    private CacheUtils() {}

    /**
     * 从redis中取值
     *
     * @param key   键
     * @param clazz 值得类型
     * @param <T>   值的泛型
     * @return 返回存入redis的，经过json反序列化后数据
     */
    public static String get(String key) {
        try {
            return simpleCacheService.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 为key设置value，不过期
     *
     * @param key 键
     * @param <T> 值的泛型
     * @return 设置成功返回true
     */
    public static boolean set(String key, String value) {
        return set(key, value, -1);
    }

    /**
     * 为key设置value，并指定key的过期时间
     *
     * @param key 键
     * @param <T> 值的泛型
     * @return 设置成功返回true
     */
    public static boolean set(String key, String value, int expireTimeSeconds) {
        try {
            if (expireTimeSeconds <= 0) {
                simpleCacheService.set(key, value);
            } else {
                simpleCacheService.set(key, value, expireTimeSeconds);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key删除value
     *
     * @param key 键
     * @return 删除成功返回true
     */
    public static boolean delete(String key) {
        try {
            return simpleCacheService.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断redis是否存在key
     *
     * @param key 键
     * @return 存在key返回true
     */
    public static Boolean exists(String key) {
        try {
            return simpleCacheService.exist(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定key在多少秒后过期
     *
     * @param key               键
     * @param expireTimeSeconds 过期时间，单位秒
     */
    public static void expireAfter(String key, int expireTimeSeconds) {
        try {
           simpleCacheService.expireAfter(key, expireTimeSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
