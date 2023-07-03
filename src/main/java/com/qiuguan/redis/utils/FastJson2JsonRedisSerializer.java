package com.qiuguan.redis.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author fu yuan hui
 * @date 2023-06-29 17:34:42 Thursday
 *
 * 仿照 {@link Jackson2JsonRedisSerializer } 写一个自定义的序列化器
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private ObjectMapper objectMapper;

    private Class<T> clazz;

    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        this(clazz, true);
        this.clazz = clazz;
    }

    public FastJson2JsonRedisSerializer(Class<T> clazz, boolean defaultObjectMapper) {
        this.clazz = clazz;
        if (defaultObjectMapper) {
            createDefaultObjectMapper();
        }
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (null == t) {
            return new byte[0];
        }

        return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (null == bytes || bytes.length == 0) {
            return null;
        }

        /**
         * JSONReader.Feature.SupportAutoType 很重要，比如反序列化时，无法从json 转成 bean
         */
        return JSON.parseObject(new String(bytes, DEFAULT_CHARSET), clazz,
                JSONReader.Feature.SupportAutoType,
                JSONReader.Feature.SupportArrayToBean);
    }


    private void createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        this.objectMapper = mapper;

    }

    protected JavaType getJavaType(Class<?> clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }

    protected void configureObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
