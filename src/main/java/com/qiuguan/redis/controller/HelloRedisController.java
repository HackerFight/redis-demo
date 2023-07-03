package com.qiuguan.redis.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author fu yuan hui
 * @date 2023-06-29 12:02:19 Thursday
 *
 * @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
 *
 * <p>
 *    @Bean
 *    @ConditionalOnMissingBean(name = "redisTemplate")
 *    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
 *    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory) {
 * 		RedisTemplate<Object, Object> template = new RedisTemplate<>();
 * 		template.setConnectionFactory(redisConnectionFactory);
 * 		return template;
 *    }
 *
 *
 *    @Bean
 *    @ConditionalOnMissingBean
 *    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
 *    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory) {
 * 		return new StringRedisTemplate(redisConnectionFactory);
 *    }
 * </p>
 *
 * 配置类 {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration} 中导入了2个Template:
 *  RedisTemplate<Object, Object>  泛型的k-v全是Object
 *  RedisTemplate<String, String>【或者StringRedisTemplate】  泛型的k-v全是String
 */


@AllArgsConstructor
@RequestMapping("/hello/")
@Slf4j
@RestController
public class HelloRedisController {

    private static final String REDIS_KEY = "eft_redis_fyh";

    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * <p>
     * 这里的 RedisTemplate 不用做任何配置，只需要导入[spring-boot-starter-data-redis]
     * 然后配置连接就可以使用了
     * </p>
     */
    @GetMapping("/redis/{value}")
    public String redis(@PathVariable String value){
        log.info("hello redis......");

        //设置k-v, 没有失效时间
        this.redisTemplate.opsForValue().set("k1", "123");

        //设置失效时间
        this.redisTemplate.opsForValue().set(REDIS_KEY, value, 30, TimeUnit.SECONDS);

        //获取v
        Object cv = this.redisTemplate.opsForValue().get(REDIS_KEY);

        log.info("redis cache value: {}", cv);
        return String.format("hello redis: %s", cv);

    }

}
