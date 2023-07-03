package com.qiuguan.redis.redis;


import com.qiuguan.redis.bean.User;
import com.qiuguan.redis.utils.BeanCreator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author fu yuan hui
 * @date 2023-06-29 16:28:40 Thursday
 *
 * <p>
 * 案例:
 *          1)sadd/smembers/sismember
 *             sadd set01 1 1 2 2 3 3       #key=set01,值是1 2 3，他不允许有重复的数据。注意：他是第二次插入不进去。
 *             smembers set01               #显示set01集合中的值。注意没有  0 -1哦
 *             sismember set01 值           #如果set01中有这个值，返回1，否则返回0
 *
 *          2)scard                         #获取集合里面元素的个数
 *             scard  set01                 #3
 *
 *          3)srem key_name value           #删除集合中的元素
 *             srem set01 3                 #原本集合中是1 2 3，现在删除了3，剩下了1和2
 *
 *          4)SRANDMEMBER key_name N(整数)      #从集合中随机取N个数
 *             srandmember set01  3            #从集合中随机取3个数
 *
 *          5)spop key_name                    #随机出栈
 *              spop set01                     #3;表示3从集合中出去了，出栈之后集合中就没有了
 *
 *          6)smove k1 k2 k1中的值              #将k1集合中的某个值迁移到k2集合中
 *               127.0.0.1:6379> SMEMBERS set01
 * 			  1) "3"
 * 			  2) "4"
 * 			  3) "5"
 *
 * 			  127.0.0.1:6379> SMEMBERS set02
 * 			  1) "y"
 * 			  2) "z"
 * 			  3) "x"
 * 		      smove set01 set02 5             #将5移到set02集合中去，注意：他放入set02中也是乱序的
 *
 * 		  7)数学集合类
 * 		      sadd set01 1 2 3 4 5
 *               sadd set02 1 2 3 a b     #往2个集合中插入值测试下面的内容
 *
 *               差集：sdiff
 *                   SDIFF set01 set02    #4,5 差集：就是用第一个集合 - 第二个与第一个的交集部分
 *               并集:sunion
 *                   SUNION set01 set02   #2个集合的所有内容
 *               交集:sinter
 *                   SINTER set01 set02   #1 2 3
 *
 *  </p>
 */
@Slf4j
@AllArgsConstructor
@RequestMapping("/redis/")
@RestController
public class SetController {

    private final RedisTemplate<Object, Object> redisTemplate;

    @GetMapping("/set")
    public String test(){
        this.redisTemplate.opsForSet().add("s1", 1, 2, 4, 6, 3);

        //SET
        DataType dataType = this.redisTemplate.type("s1");
        log.info("set 类型s1的数据类型是 {}", dataType);

        Set<Object> s1 = this.redisTemplate.opsForSet().members("s1");
        log.info("集合set 类型的s1 包含元素：{}", s1);


        //删除指定的元素
        Long count = this.redisTemplate.opsForSet().remove("s1", 2, 4, 6);
        //set 删除了【 3 】个元素, 删除的后的set集合：[1, 3
        log.info("set 删除了【 {} 】个元素, 删除的后的set集合：{}", count, this.redisTemplate.opsForSet().members("s1"));


        this.redisTemplate.delete("s1");


        //-----------------------------------------------------------------//

        this.redisTemplate.opsForSet().add("s2",1, 2, 2, 4, 5, 3, 9, 6, 5);
        //[1, 2, 3, 4, 5, 6, 9], 重复的元素第二次无法插入
        log.info("集合s2放入了重复的元素，看下集合的内容：{}",  this.redisTemplate.opsForSet().members("s2"));

        //随机弹出一个元素，并返回弹出元素的值
        Object s2 = this.redisTemplate.opsForSet().pop("s2");
        log.info("随机弹出一个元素: {}", s2);

        //随机弹出指定个数个元素，并返回弹出的值
        List<Object> s21 = this.redisTemplate.opsForSet().pop("s2", 3);
        log.info("随机弹出3个元素: {}", s21);


        this.redisTemplate.delete("s2");


        //-----------------------------------------------------------------//
        this.redisTemplate.opsForSet().add("s3", "上海", "北京", "杭州", "三亚", "苏州");

        //随机获取一个元素，元素不会出栈，pop才会出栈
        Object o = this.redisTemplate.opsForSet().randomMember("s3");
        log.info("随机从s3取出一个元素【{}】（不会出栈） ： s3集合内容：{}", o, this.redisTemplate.opsForSet().members("s3"));

        //随机获取N个元素
        List<Object> objects = this.redisTemplate.opsForSet().randomMembers("s3", 2);
        log.info("随机从s3取出2个元素【{}】（不会出栈）：  s3集合内容：{}", objects, this.redisTemplate.opsForSet().members("s3"));

        //redsi 命令：scard key
        Long size = this.redisTemplate.opsForSet().size("s3");
        log.info("集合s3的元素个数：{}", size);

        Boolean member = this.redisTemplate.opsForSet().isMember("s3", "南京");
        log.info("【南京】是集合S3里面的元素吗？：{}", member);

        this.redisTemplate.delete("s3");
        return "redis set";
    }


    /**
     * 交集，并集，差集
     * @return
     */
    @GetMapping("/set2")
    public String test2(){
        this.redisTemplate.opsForSet().add("s4", 1, 2, 3, 3, 4, 4);
        this.redisTemplate.opsForSet().add("s5", 2, 3, 6, 7);

        this.redisTemplate.expire("s4", 1, TimeUnit.MINUTES);
        this.redisTemplate.expire("s5", 1, TimeUnit.MINUTES);

        //s4的内容是 [1, 2, 3, 4]
        Set<Object> s4 = this.redisTemplate.opsForSet().members("s4");
        log.info("集合s4的元素内容：{}", s4);


        //s5的内容是 [2, 3, 6, 7]
        Set<Object> s5 = this.redisTemplate.opsForSet().members("s5");
        log.info("集合s4的元素内容：{}", s5);


        /**
         * 交集 [2, 3]
         *  集合s4 【[1, 2, 3, 4]】 和 集合s5 【[2, 3, 6, 7]】 的交集是 【[2, 3]】
         */
        Set<Object> intersect = this.redisTemplate.opsForSet().intersect("s4", "s5");
        log.info("集合s4 【{}】 和 集合s5 【{}】 的交集是 【{}】", this.redisTemplate.opsForSet().members("s4"), this.redisTemplate.opsForSet().members("s5"), intersect);


        //将集合s4和s5的交集，写到s6中去
        this.redisTemplate.opsForSet().intersectAndStore("s4", "s5", "s6");
        log.info("交集集合s6的内容是：{}", this.redisTemplate.opsForSet().members("s6"));
        this.redisTemplate.delete("s6");

        //--------------------------------------------------------------------//

        /**
         * 并集
         * 集合s4 【[1, 2, 3, 4]】 和 集合s5 【[2, 3, 6, 7]】 的并集是 【[1, 2, 3, 4, 6, 7]】
         */
        Set<Object> union = this.redisTemplate.opsForSet().union("s4", "s5");
        log.info("集合s4 【{}】 和 集合s5 【{}】 的并集是 【{}】", this.redisTemplate.opsForSet().members("s4"), this.redisTemplate.opsForSet().members("s5"), union);

        Long count = this.redisTemplate.opsForSet().unionAndStore("s4", "s5", "s66");
        log.info("并集集合s66的内容是：{}, 集合元素个数是：{}", this.redisTemplate.opsForSet().members("s66"), count);
        this.redisTemplate.delete("s66");


        //-----------------------------------------------------------------------//

        /**
         * 差集
         */
        this.redisTemplate.opsForSet().add("sa", "a", "b", "c", "d");
        this.redisTemplate.opsForSet().add("sb", "c", "d", "e", "f");

        //存在于sa,不存在于sb
        Set<Object> difference = this.redisTemplate.opsForSet().difference("sa", "sb");
        log.info("差集结果是：{}", difference); // [a, b]

        //存在于sb,不存在于sa
        Set<Object> difference2 = this.redisTemplate.opsForSet().difference("sa", "sb");
        log.info("差集结果是：{}", difference2); // [e, f]


        this.redisTemplate.delete(Arrays.asList("s4", "s5", "s6", "s66", "sa", "sb"));

        return String.format("success: %s", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));

    }


    @GetMapping("/set3")
    public String test3(){
        this.redisTemplate.opsForSet().add("ss", BeanCreator.createUser());

        User user = (User) this.redisTemplate.opsForSet().pop("ss");
        log.info("user info: {}", user);

        return "redis set object";
    }
}
