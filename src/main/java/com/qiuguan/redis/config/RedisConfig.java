package com.qiuguan.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.qiuguan.redis.utils.FastJson2JsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author fu yuan hui
 * @date 2023-06-29 17:08:04 Thursday
 */
@Configuration
public class RedisConfig {

    /**
     * redis 提供了几种序列化机制：
     * @see JdkSerializationRedisSerializer
     * @see Jackson2JsonRedisSerializer
     * @see StringRedisSerializer
     *
     *
     * 如果是 {@link RedisTemplate<String, String> } 则默认使用的是 {@link StringRedisSerializer}
     * 如果是 {@link RedisTemplate<Object, Object> } 则默认使用的是 {@link JdkSerializationRedisSerializer}
     *
     * 当我用 {@link RedisTemplate<String, String> } 操作普通字符串的时候，没有任何问题，
     * 但当我使用 {@link RedisTemplate<Object, Object> } 操作时，发现用代码操作没有问题，但是我去
     * redis 终端中去操作时，发现没有这key。。请看图片【Object泛型操作字符串无法在终端中查看.png】
     *
     * 于是我猜测，应该是序列话的问题，所以我这里就重写 {@link RedisTemplate<Object, Object> }
     *
     * 当配置好后重写运行就没有上述问题了。
     */
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        //默认的序列化策略是DK的，所以在存储的时候键和值都会先被序列化后再存储
        RedisTemplate<Object,Object> t = new RedisTemplate<>();
        t.setConnectionFactory(redisConnectionFactory);

        //设置key的序列化规则
        t.setKeySerializer(new StringRedisSerializer());
        t.setHashKeySerializer(new StringRedisSerializer());

        //设置value的序列化规则
//        Jackson2JsonRedisSerializer<?> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
//        valueSerializer.setObjectMapper(objectMapper);

        //仿照Jackson2JsonRedisSerializer 写一个FastJson2的序列化器
        //FastJson2JsonRedisSerializer<?> valueSerializer = new FastJson2JsonRedisSerializer<>(Object.class);

        //使用默认的序列化器
        t.setValueSerializer(new JdkSerializationRedisSerializer());
        t.setHashValueSerializer(new JdkSerializationRedisSerializer());



        return t;
    }
}
