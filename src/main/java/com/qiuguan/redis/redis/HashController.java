package com.qiuguan.redis.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fu yuan hui
 * @date 2023-06-29 16:28:13 Thursday
 *
 *
 * <p>
 *
 * hash 类似于java的Map<String,Object>; KV模式不变，但V是一个键值对
 *        案例:
 *           1)hset/hget/hmset/hmget/hgetall/hdel
 *              hset user id 11        #key是user，value是 id 11 ,这个value也是一个键值对(key=id,value=11)
 *              #type user            #hash.这个user是key。
 *              hget user id           #通过java理解，Map<String,Map<Stirng,Object>>  user --> id  --> 11
 *              #hgetall user
 *
 *              hmset customer name lisi age 23 sex 1  # m:more的意思。customer是key，然后value是3个键值对
 *              hmget customer name age sex            #取出值。customer是key;name,age,sex是value中的key
 *              hgetall customer                       #也是取出值，比上面简单一点
 *
 *              hdel user id                           #删除id，删除key对应的value的key
 *
 *            2)hlen
 *                hlen customer                        #3.说明这个key里面对应3个value
 *
 *            3)hexists key val_key                    #判断在key里面的value是否含有某个key
 *                hexists customer name                #判断customer这个key里面的value是否含有name这个key，有的话返回1，否则返回0
 *                #Map<String,Map<String,Object>>     #customer就是第一个String,name就是第二个String
 *
 *            4)hkeys / hvals(*)
 *                 hkeys customer                      #遍历所有的key
 *                 hvals customer                      #遍历所有的value
 *
 *            5)hincrby/hincrbyfloat
 *                 hincrby customer age 2              #年龄每次增加2。如果value的key不是数字，就会报错。
 *                 hincrbyfloat customer score 0.5     #成绩+0.5
 *
 *            6)hsetnx (nx:if not exists)              #如果不存在在往里设置值
 *                 hsetnx  customer age 30             #0 说明没有设置成功，因为age已经存在了
 *                 hsetnx  customer email abc@163.com  #1 说明设置成功。因为之前不存在email
 *
 * </p>
 */
@Slf4j
@AllArgsConstructor
@RequestMapping("/redis/hash/")
@RestController
public class HashController {

    private final RedisTemplate<Object, Object> redisTemplate;

    @GetMapping("/h1")
    public String hash(){
        //有点类似于java中 Map<k1, Map<k,v>>
        //redis 命令：hset key field value
        this.redisTemplate.opsForHash().put("h1", "name", "zhangxinyu");
        this.redisTemplate.opsForHash().put("h1", "age", 25);
        this.redisTemplate.opsForHash().put("h1", "sex", "female");
        //会覆盖旧值
        this.redisTemplate.opsForHash().put("h1", "name", "fuyuanhui");
        //可以存储null值，只不过内部会转成空字符串""
        this.redisTemplate.opsForHash().put("h1","country", null);

        //redis命令：hget key field
        Object sex = this.redisTemplate.opsForHash().get("h1", "sex");
        log.info("获取哈希h1中sex的值：{}", sex);

        //redis: type key
        DataType h1 = this.redisTemplate.type("h1");
        log.info("h1的数据类型是：{}", h1);


        Map<String, Object> map = new HashMap<>();
        map.put("age", 23);
        map.put("sex", "male");
        //redis命令：hmset k field value field value ...
        this.redisTemplate.opsForHash().putAll("h2", map);

        //获取所有k-v, redis命令：hgetall key
        Map<Object, Object> h2 = this.redisTemplate.opsForHash().entries("h2");
        log.info("哈希h2中的所有元素：{}", h2);

        //redis命令：hmget key field field ....
        List<Object> objects = this.redisTemplate.opsForHash().multiGet("h2", Arrays.asList("age", "sex"));
        log.info("hmget 指令执行结果：{}", objects);

        //redis命令：hexists key field
        Boolean aBoolean = this.redisTemplate.opsForHash().hasKey("h2", "name");
        log.info("哈希h2中是否有【name】属性: {}", aBoolean);

        //redis命令：
        //属性age值由之前的23变成了33
        this.redisTemplate.opsForHash().increment("h2", "age", 10);
        Map<Object, Object> nh = this.redisTemplate.opsForHash().entries("h2");
        log.info("哈希h2更新后的的所有元素：{}", nh);


        //列出h1的所有field
        //redis命令：hkeys
        Set<Object> keys = this.redisTemplate.opsForHash().keys("h1");
        log.info("哈希h1中包含的所有field：{}", keys); //[name, age, sex, country]


        List<Object> h11 = this.redisTemplate.opsForHash().values("h1");
        log.info("哈希h1中所有的value是：{}", h11);

        //redis命令：hlen key
        Long aLong = this.redisTemplate.opsForHash().size("h1");
        log.info("哈希h1包含了多少个【{}】field", aLong);


        //scan 方法制定扫描规则，正则匹配
        Cursor<Map.Entry<Object, Object>> cursor = this.redisTemplate.opsForHash().scan("h1", ScanOptions.scanOptions().match("*n*").build());
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> next = cursor.next();
            //field包含n字符的有: name=fuyuanhui
            //field包含n字符的有: country=null
            log.info("field包含n字符的有: {}", next);
        }

        cursor.close();

        return "redis hash";
    }
}
