package com.qiuguan.redis.redis;

import com.qiuguan.redis.controller.HelloRedisController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * @author fu yuan hui
 * @date 2023-06-29 13:18:02 Thursday
 *
 * redis 数据类型 String
 * @see HelloRedisController
 *
 * 直接使用 {@link RedisAutoConfiguration } 配置类配置的 {@link RedisTemplate } 即可
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/redis/string/")
public class StringController {

    /**
     * 注入它是会报错的，容器中没有, 请看{@link RedisAutoConfiguration}
     * 请看 {@link HelloRedisController} 类上的说明
     */
    //private RedisTemplate<String, Object> redisTemplate;

    /**
     * 如果是操作String, 推荐使用
     * {@link StringRedisTemplate }
     * {@link RedisTemplate<String, String> }
     */
    private final StringRedisTemplate redisTemplate;

    @GetMapping("/v1")
    public String test(){
        //设置值
        this.redisTemplate.opsForValue().set("k1", "123");

        //如果不存在在设置值，这里的k1仍然是123
        this.redisTemplate.opsForValue().setIfAbsent("k1", "456");

        //k1值是123，自增1变成124，注意：如果不是数字，自增将会报错
        this.redisTemplate.opsForValue().increment("k1");

        //设置20秒的失效时间
        this.redisTemplate.opsForValue().set("k11", "haha", 20, TimeUnit.SECONDS);

        //ttl, 过期时间，所有数据类型
        Long expire = this.redisTemplate.getExpire("k11");
        log.info("过期时间：{}", expire);

        //删除key,所有数据类型都有
        this.redisTemplate.delete("k11");

        //获取值并删除
        //String k1 = this.redisTemplate.opsForValue().getAndDelete("k1");

        return "success";
    }

    @GetMapping("/v2")
    public String test2(){
        //先绑定key在操作
        BoundValueOperations<String, String> k2 = this.redisTemplate.boundValueOps("k2");
        k2.set("789");
        k2.expire(20, TimeUnit.SECONDS);
        /**
         * 将对789 + 1 = 790, 注意：如果是非数字将会报错。
         */
        Long increment = k2.increment();
        log.info("auto increment value: {}", increment);

        //获取失效时间
        Long expire = k2.getExpire();
        log.info("k2 过期时间：{}", expire);

        //如果“k2”不存在，则将其设置为“1234”
        k2.setIfAbsent("1234");

        //如果存在，则设置值为“123456”，失效时间为30s
        k2.setIfPresent("123456", 30, TimeUnit.SECONDS);

        return "success";
    }
}
