package com.lee.framework.security.cache;

/**
 * 缓存接口，支持jvm内存缓存和redis分布式缓存，如果存在redis相关配置，则实现是{@link RedisSimpleCacheService}，
 * 否则是{@link LocalSimpleCacheService}
 */
public interface SimpleCacheService {


    String get(String key);

    Boolean set(String key, String value, int expireTimeSeconds);

    Boolean set(String key, String value);

    Boolean exist(String key);

    Boolean delete(String key);

    void expireAfter(String key, int expireTimeSeconds);
}
