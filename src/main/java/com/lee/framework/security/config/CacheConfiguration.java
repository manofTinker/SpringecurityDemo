package com.lee.framework.security.config;


import com.lee.framework.security.cache.LocalSimpleCacheService;
import com.lee.framework.security.cache.RedisSimpleCacheService;
import com.lee.framework.security.cache.SimpleCacheService;
import com.lee.framework.security.common.uitl.CacheUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class CacheConfiguration {

    @ConditionalOnProperty(prefix = "security.cache", name = "type", havingValue = "redis")
    @Bean
    public SimpleCacheService redisCacheService(RedisTemplate<String, Object> redisTemplate) {
        RedisSimpleCacheService redisCacheService = new RedisSimpleCacheService();
        redisCacheService.setRedisTemplate(redisTemplate);
        CacheUtils.setCacheService(redisCacheService);
        return redisCacheService;
    }

    @ConditionalOnMissingBean(SimpleCacheService.class)
    @Bean
    public SimpleCacheService localCacheService() {
        LocalSimpleCacheService localCacheService = new LocalSimpleCacheService();
        CacheUtils.setCacheService(localCacheService);
        return localCacheService;
    }

    @ConditionalOnProperty(prefix = "spring.redis", name = "host")
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 配置key序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // 配置value序列化方式
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有,包括private和public
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSerializer.setObjectMapper(mapper);

        template.setValueSerializer(jacksonSerializer);
        template.setHashValueSerializer(jacksonSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
