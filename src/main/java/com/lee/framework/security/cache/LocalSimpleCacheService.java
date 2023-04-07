package com.lee.framework.security.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 借助jvm内存实现的缓存
 */
public class LocalSimpleCacheService implements SimpleCacheService {

    private final Map<String, CacheBody> localStore;

    public LocalSimpleCacheService() {
        localStore = new ConcurrentHashMap<>();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new LocalCacheClearer(this), 60, 5, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        CacheBody cacheBody = localStore.get(key);
        if (cacheBody == null) {
            return null;
        }
        Long expireAt = cacheBody.getExpireAt();
        if (expireAt < System.currentTimeMillis()) {
            localStore.remove(key);
            return null;
        }
        return cacheBody.getValue();
    }

    @Override
    public Boolean set(String key, String value, int expireTimeSeconds) {
        if (expireTimeSeconds <= 0L) {
            throw new RuntimeException("错误的过期时间");
        }
        localStore.put(key, new CacheBody(value, expireTimeSeconds));
        return true;
    }

    @Override
    public Boolean set(String key, String value) {
        // 不允许内存缓存中放入不过期的缓存数据
        throw new RuntimeException("不支持");
    }

    @Override
    public Boolean exist(String key) {
        return localStore.get(key) != null;
    }

    @Override
    public Boolean delete(String key) {
        localStore.remove(key);
        return true;
    }

    @Override
    public void expireAfter(String key, int expireTimeSeconds) {
        CacheBody cacheBody = localStore.get(key);
        if (cacheBody == null || cacheBody.getValue() == null) {
            return;
        }
        cacheBody.setExpireAt(System.currentTimeMillis() + expireTimeSeconds * 1000L);
        localStore.put(key, cacheBody);
    }

    private void clear() {
        for (String key : localStore.keySet()) {
            CacheBody cacheBody = localStore.get(key);
            if (cacheBody == null || cacheBody.getExpireAt() == null) {
                localStore.remove(key);
                continue;
            }
            if (cacheBody.getExpireAt() < System.currentTimeMillis()) {
                localStore.remove(key);
            }
        }
    }

    /**
     * 清理缓存数据线程
     */
    static class LocalCacheClearer implements Runnable {

        private final LocalSimpleCacheService localCacheService;

        public LocalCacheClearer(LocalSimpleCacheService localCacheService) {
            this.localCacheService = localCacheService;
        }

        @Override
        public void run() {
            localCacheService.clear();
        }
    }

    /**
     * 缓存数据载体
     */
    static class CacheBody {

        private String value;

        private Long expireAt;

        public CacheBody(String value, int expireTimeSeconds) {
            this.value = value;
            this.expireAt = System.currentTimeMillis() + expireTimeSeconds * 1000L;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Long getExpireAt() {
            return expireAt;
        }

        public void setExpireAt(Long expireAt) {
            this.expireAt = expireAt;
        }
    }
}
