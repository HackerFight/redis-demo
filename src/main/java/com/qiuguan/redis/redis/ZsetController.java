package com.qiuguan.redis.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * @author fu yuan hui
 * @date 2023-07-02 23:18:55 Sunday
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/redis/zset/")
public class ZsetController {

    private final RedisTemplate<Object, Object> redisTemplate;

    @GetMapping("/zs")
    public String test(){
        this.redisTemplate.opsForZSet().add("zs1", "zhangxinyu", 79);
        this.redisTemplate.opsForZSet().add("zs1", "fuyuanhui", 70);
        this.redisTemplate.opsForZSet().add("zs1", "mimi", 80);
        this.redisTemplate.opsForZSet().add("zs1","duoduo", 65);

        //redis命令：zrange key 0 -1 (遍历所有）
        //[duoduo, fuyuanhui, zhangxinyu, mimi] 按照分数从小到大排序
        Set<Object> zs1 = this.redisTemplate.opsForZSet().range("zs1", 0, -1);
        log.info("zset集合的内容是：{}", zs1);


        //遍历的同时将分数也获取出来
        //redis命令：zrange key 0 -1 withscores
        Set<ZSetOperations.TypedTuple<Object>> zs11 = this.redisTemplate.opsForZSet().rangeWithScores("zs1", 0, -1);
        log.info("遍历zset集合的元素并携带分数：{}", zs11);


        Long l = this.redisTemplate.opsForZSet().count("zs1", 60, 70);
        log.info("分数在[60,70]之间的有 {} 个", l);

        //zset集合中【zhangxinyu】的分数是：79.0
        Double score = this.redisTemplate.opsForZSet().score("zs1", "zhangxinyu");
        log.info("zset集合中【zhangxinyu】的分数是：{}", score);

        //zset集合中【zhangxinyu】的索引是 2
        //[duoduo, fuyuanhui, zhangxinyu, mimi]
        Long rank = this.redisTemplate.opsForZSet().rank("zs1", "zhangxinyu");
        log.info("zset集合中【zhangxinyu】的索引是：{}", rank);

        //逆序获取索引，这回 【zhangxinyu】 的索引就是1了
        //[duoduo, fuyuanhui, zhangxinyu, mimi]
        Long aLong = this.redisTemplate.opsForZSet().reverseRank("zs1", "zhangxinyu");
        log.info("zset集合中【zhangxinyu】的逆序索引是：{}", aLong); //1


        //[fuyuanhui, zhangxinyu]
        //reverseRange 就是逆序遍历
        Set<Object> zs12 = this.redisTemplate.opsForZSet().range("zs1", 1, 2);
        log.info("zset集合中索引1到2之间的元素是：{}", zs12);


        //注意：pop会出栈
        ZSetOperations.TypedTuple<Object> zset = this.redisTemplate.opsForZSet().popMax("zs1");
        log.info("从集合中弹出分数最大的元素是：{}, 之后的集合是：{}", zset, this.redisTemplate.opsForZSet().range("zs1", 0, -1));


        //统计集合里面有多少个元素
        Long zcard = this.redisTemplate.opsForZSet().zCard("zs1");
        Long size = this.redisTemplate.opsForZSet().size("zs1");
        log.info("zcard统计集合元素个数 : {}, size 统计元素个数：{}", zcard, size);


        List<Object> objects = this.redisTemplate.opsForZSet().randomMembers("zs1", 2);
        log.info("随机获取2个元素：【{}】，不会出栈哦", objects);


        //数学运算：交集，并集，差集，这个和set一样，就不演示了。

        this.redisTemplate.delete("zs1");
        return "redis zset";
    }


    /**
     * 参考文档 {@link https://blog.csdn.net/succing/article/details/121081595 }
     *
     * ZRANGEBYLEX key min max [LIMIT offset count]
     */
    @GetMapping("/zs2")
    public String test2(){

        this.redisTemplate.opsForZSet().add("zs2", "aa", 60);
        this.redisTemplate.opsForZSet().add("zs2", "bb", 60);
        this.redisTemplate.opsForZSet().add("zs2", "cc", 60);
        this.redisTemplate.opsForZSet().add("zs2", "dd", 60);
        this.redisTemplate.opsForZSet().add("zs2", "ee", 60);


        RedisZSetCommands.Range range = RedisZSetCommands.Range.range().gte(60);
        RedisZSetCommands.Limit limit = RedisZSetCommands.Limit.limit().offset(0).count(2);

        Set<Object> zs13 = this.redisTemplate.opsForZSet().rangeByLex("zs1", range);
        this.redisTemplate.opsForZSet().rangeByLex("zs1", range, limit);
        log.info("rangebylex: {}", zs13);


        return "redis zset";
    }
}
