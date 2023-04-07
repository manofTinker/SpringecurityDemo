package com.lee.framework.security.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 借助redis实现的缓存，
 */
public class RedisSimpleCacheService implements SimpleCacheService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String get(String key) {
        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    @Override
    public Boolean set(String key, String value, int expireTimeSeconds) {
        try {
            if (expireTimeSeconds > 0) {
                redisTemplate.opsForValue().set(key, value, expireTimeSeconds, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    @Override
    public Boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    @Override
    public Boolean exist(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public void expireAfter(String key, int expireTimeSeconds) {
        redisTemplate.expire(key, expireTimeSeconds, TimeUnit.SECONDS);
    }

}
