package com.qiuguan.redis.redis.object;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSON;
import com.qiuguan.redis.bean.User;
import com.qiuguan.redis.utils.BeanCreator;
import com.qiuguan.redis.utils.SerializerUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * @author fu yuan hui
 * @date 2023-07-03 09:55:33 Monday
 *
 * redis存储对象的几种方式
 * 1.将对象序列化后存储（序列化成byte[]然后存储）
 * 2.使用JSON工具转成字符串存储【请看test2()和test4()】
 * 3.用hash来存储对象
 *
 *
 * hash和string存储对象的差异：
 *  参考文档：https://zhuanlan.zhihu.com/p/373808682
 *  1.适合用 String 存储的情况：
 *     每次需要访问大量的字段
 *     存储的结构具有多层嵌套的时候
 *
 *  2.适合用 Hash 存储的情况：
 *     大多数情况中只需要访问少量字段
 *     自己始终知道哪些字段可用，防止使用 mget 时获取不到想要的数据
 */
@Slf4j
@AllArgsConstructor
@RequestMapping("/redis/obj/")
@RestController
public class ObjectController {

    private final RedisTemplate<Object, Object> redisTemplate;


    /**
     * 序列化存储对象
     * @return
     */
    @GetMapping("/serialize")
    public String test1(){
        User user = BeanCreator.createUser();
        this.redisTemplate.opsForValue().set("s", Objects.requireNonNull(SerializerUtils.serialize(user)));

        Object o = this.redisTemplate.opsForValue().get("s");

        log.info("序列化存储对象到redis：{}", SerializerUtils.deserialize((byte[]) o));

        this.redisTemplate.delete("s");

        return "serialize: " + SerializerUtils.deserialize((byte[]) o);
    }


    /**
     * 通过fastjson转成字符串存储
     * @return
     */
    @GetMapping("/json")
    public String test2(){
        User user = BeanCreator.createUser();
        String jsonString = JSON.toJSONString(user);

        this.redisTemplate.opsForValue().set("s2", jsonString);

        Object o = this.redisTemplate.opsForValue().get("s2");

        log.info("通过JSON工具转成jsonString存储：{}", o);

        this.redisTemplate.delete("s2");

        return "json: " + o;
    }


    @GetMapping("/hash")
    public String test3(){
        User user = BeanCreator.createUser();

        String key = "user_" + user.getId();
        this.redisTemplate.opsForHash().put(key, "id", user.getId());
        this.redisTemplate.opsForHash().put(key, "name", user.getName());
        this.redisTemplate.opsForHash().put(key, "birthday", user.getBirthday());
        this.redisTemplate.opsForHash().put(key, "salary", user.getSalary());
        this.redisTemplate.opsForHash().put(key, "favorites", user.getFavorites());
        this.redisTemplate.opsForHash().put(key, "extend", user.getExtend());

        Map<Object, Object> entries = this.redisTemplate.opsForHash().entries(key);
        log.info("entries: " + entries);

        User u = new User();
        BeanUtils.copyProperties(entries, u);
        log.info("Spring BeanUtils map转成bean: {}", u); //失败，属性没有映射成功

        User user1 = BeanUtil.mapToBean(entries, User.class, true, CopyOptions.create());
        log.info("hutool工具将Map转成Bean: {}", user1);

        this.redisTemplate.delete(key);

        return "hash";
    }

    /**
     * 直接存储对象
     *  注意：对象要实现序列化接口
     */
    @GetMapping("/direct")
    public String test4(){
        User user = BeanCreator.createUser();
        this.redisTemplate.opsForValue().set("u", user);

        Object o = this.redisTemplate.opsForValue().get("u");

        User u = (User) o;

        log.info("直接存储对象User {}",u);

        this.redisTemplate.delete("u");

        return "direct save object";
    }
}
