package com.qiuguan.redis.redis;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qiuguan.redis.bean.Person;
import com.qiuguan.redis.bean.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.qiuguan.redis.utils.FastJson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import static com.qiuguan.redis.utils.BeanCreator.createUser;
import static com.qiuguan.redis.utils.BeanCreator.createUserList;


/**
 * @author fu yuan hui
 * @date 2023-06-29 16:28:29 Thursday
 *
 * <p>
 * 案例:
 *            1)lpush/rpush/lrange
 *                lpush myist01 1 2 3 4 5   # key是mylist01,里面有5个值；顺序是5 4 3 2 1
 *                lrange mylist01 0 -1      # 取出mylist01中所有的值，顺序是 5 4 3 2 1
 *                lrange mylist01 0 2       # 取出0~2的3个值。顺序是 5 4 3
 *
 *                rpush mylist02 1 2 3 4 5  # key是mylist02,里面有5个值，顺序是 1 2 3 4 5.怎么进怎么出
 *                lrange mylist02 0 -2      # 取出列表中所有的值。顺序是 1 2 3 4 5
 *
 *            2)lpop/rpop
 *                lpop mylist01             #5
 *                lpop mylist02             #1; lpop是栈顶的先出来。取出来之后，列表中就没有了，用lrange看下就知道了
 *
 *                rpop mylist01             #1; rpop是取栈底的。出栈的操作
 *                rpop mylist02             #5
 *
 *            3)lindex      #按照索引下标获得元素(从上到下)
 *                lindex mylist02 2         #取出下标是2的值
 *
 *            4)llen        #显示列表的长度
 *                llen mylist02    #显示列表中元素的个数
 *
 *            5)lrem        #删除N个value
 *               lpush mylist03 1 1 2 2 2 2 2 3 4 5  #往key=mylist03的列表里加入一些值,其中有5个2
 *               lrem mylist03 4 2            #删除4个2，就剩下1个2了
 *
 *            6)ltrim key_name 开始index 结束index    #截取指定范围的值，将截取的值在重新赋给key_name
 *               lpush mylist04 1 2 3 4 5 6 7 8     #加入一些值
 *                 127.0.0.1:6379> lrange mylist 0 -1
 * 				1) "8"       --index = 0
 * 				2) "7"       --index = 1
 * 				3) "6"       -- ....
 * 				4) "5"
 * 				5) "4"
 * 				6) "3"
 * 				7) "2"
 * 				8) "1"
 *               ltrim mylist04 0 3                #结果是:8 7 6 5  。一定要注意lpush他是逆序排放的....
 *
 *            7)rpoplpush  源列表  目的列表
 *                 127.0.0.1:6379> lrange mylist 0 -1
 * 				1) "8"
 * 				2) "7"
 * 				3) "6"
 * 				4) "5"
 * 				5) "4"
 * 				127.0.0.1:6379> lrange mylist2 0 -1
 * 				1) "6"
 * 				2) "5"
 * 				3) "4"
 * 				4) "3"
 * 				127.0.0.1:6379> rpoplpush mylist2 mylist    #3。意思是从mylist2中取出栈底的值3，放到mylist中去
 * 				#他是意思是将mylist2的栈底值(rpop嘛)取出来给mylist,放到栈顶
 *
 * 		  8)lset
 * 		     lset key_name 下标 value
 * 		       lrange mylist 0 -1       #先看下mylist有哪些值
 * 				1) "3"     --index = 0
 * 				2) "8"     --index = 1
 * 				3) "7"
 * 				4) "6"
 * 				5) "5"
 * 				6) "4"
 * 			  lset mylist 1 45          #将index=1的值改为45
 *
 * 		   9)linsert
 * 		      linsert key_name before/after 值1 值2
 * 		        127.0.0.1:6379> lrange mylist 0 -1    #先看下mylist有哪些值
 * 				1) "3"
 * 				2) "45"
 * 				3) "7"
 * 				4) "6"
 * 				5) "5"
 * 				6) "4"
 * 			  linsert mylist before 7 java            #我在7前面插入一个java。如果有2个7，他会在最前面的7加。
 *
 * </p>
 *
 * @see RedisAutoConfiguration
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/redis/list/")
public class ListController {

    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * redis命令：
     * lpush k v[v....]
     * rpush k v[v....]
     * rpop k 从右边弹出一个元素
     * lpop k v 从左边弹出一个元素
     * llen k 集合长度
     * lrange 0 -1 遍历所有元素  lrange 0  5 遍历前6个元素
     * lindex k 0 获取索引为0位置的元素值, 注意和 lpush / rpush 有关
     *
     *
     */
    @GetMapping("/v1")
    public String test1() {
        //必须先有key,才可以往指定位置添加元素
        //this.redisTemplate.opsForList().set("a1", 0, 5);

        //从左边放入元素：redis 命令：lpush k [element....]
        this.redisTemplate.opsForList().leftPush("l1", 100);
        this.redisTemplate.opsForList().leftPush("l1", 101);
        this.redisTemplate.opsForList().leftPush("l1", 102);
        //设置失效时间
        this.redisTemplate.expire("l1", 30, TimeUnit.MINUTES);
        //102 -- 101  -- 100  因为100是最先从左边放入的，所以他在最右边

        //redis命令： type key
        DataType dataType = this.redisTemplate.type("l1");
        log.info("通过type查看数据类型：{}", dataType);

        //从右边放入，一次放入多个。  【102 -- 101  -- 100】从左往右放   【7 -- 8 --9】从右往左放入
        this.redisTemplate.opsForList().rightPushAll("l1", 7, 8, 9);

        Long size = this.redisTemplate.opsForList().size("l1");
        log.info("集合l1长度：{}", size);

        List<Object> list = this.redisTemplate.opsForList().range("l1", 0, -1);
        log.info("集合l1的内容：{}", list); //[102, 101, 100, 7, 8, 9]


        //元素将从集合中移除. [101, 100, 7, 8, 9]
        Object pop1 = this.redisTemplate.opsForList().leftPop("l1");
        log.info("从集合l1左边弹出一个元素：{}", pop1);

        size = this.redisTemplate.opsForList().size("l1");
        log.info("集合l1长度：{}", size);


        //集合l1  [101, 100, 7, 8], 集合 l2 [9]
        Object o = this.redisTemplate.opsForList().rightPopAndLeftPush("l1", "l2");
        log.info("从集合l1中取出【最右】边的一个元素，然后【从左】边插入l2中，取出的元素是：{}", o);

        //往l3中插入数据，从左边插入
        //this.redisTemplate.opsForList().leftPushAll("l3", "a", "b", "c");

        //去redis终端中创建一个l3集合. redis命令：rpush l3 v...
        //慎用，比如集合先是空的，然后在等待过程中，我在终端中push一个元素a,这里就会报错，如果push 1 就不会了。 ？
        //Object o1 = this.redisTemplate.opsForList().rightPop("l3", 30, TimeUnit.SECONDS);
        //log.info("从集合l3右边弹出一个元素：{}", o1);


        //将集合l1索引为0位置的元素修改为m, [m, 100, 7, 8]
        this.redisTemplate.opsForList().set("l1", 0, "m");
        log.info("将集合l1索引为0的元素替换为m, 集合l1: {}", this.redisTemplate.opsForList().range("l1", 0, -1));

        //redis 命令: lindex k index
        Object l1 = this.redisTemplate.opsForList().index("l1", 0);
        log.info("集合l1 = {}, 索引为0的元素是：{}", this.redisTemplate.opsForList().range("l1", 0, -1), l1);

        Long l11 = this.redisTemplate.opsForList().indexOf("l1", 100);
        log.info("集合l1 = {}, 元素为100的索引位置是：{}", this.redisTemplate.opsForList().range("l1", 0, -1), l11);

        //lmove指令使用前提是redis版本要大于6.2.0， 目前是6.0.9, 他就是代替 RPOPLPUSH 指令
        //Object move = this.redisTemplate.opsForList().move("l1", RedisListCommands.Direction.LEFT, "l1", RedisListCommands.Direction.RIGHT);
        //log.info("从列表的一端删除元素，在放入列表的另一端，所以可以当做一个列表循环命令 l1 = {}, move = {}", this.redisTemplate.opsForList().range("l1", 0, -1), move);


        /**
         * count = 0，删除所有匹配的元素
         * count > 0 删除匹配元素开始，从左到右最多count个元素
         * count < 0 删除匹配元素开始，从右到左最多count个元素
         * redis命令：lrem key count value
         */
        Long count = this.redisTemplate.opsForList().remove("l1", 1, "m");
        log.info("集合l1删除元素m，集合l1 = {}, 删除元素个数：{}", this.redisTemplate.opsForList().range("l1", 0, -1), count);


        //删除集合{del key [key....]}
        this.redisTemplate.delete(Arrays.asList("l1", "l2", "l3"));

        return String.format("redis_list: %s", LocalDateTime.now());
    }


    @GetMapping("/v2")
    public String test2(){
        //先绑定key在操作，其余的和上面一样
        BoundListOperations<Object, Object> list = this.redisTemplate.boundListOps("list");
        list.expire(1, TimeUnit.MINUTES);
        list.leftPushAll(1, 2, 3, 4, 5, 6);
        list.rightPop();
        //.......

        this.redisTemplate.delete("list");
        return "success";
    }


    /**
     * 存储对象
     * @return
     */
    @GetMapping("/object")
    public String testObject(){
        BoundListOperations<Object, Object> list = this.redisTemplate.boundListOps("list");

        User user = User.builder().id(1001L)
                .name("qiuguan")
                .birthday(LocalDateTime.now())
                .salary(100.0)
                .favorites(Arrays.asList("basketball", "football"))
                .extend(new HashMap<>())
                .build();

        /**
         * 使用 {@link FastJson2JsonRedisSerializer } 时做value的序列化时，redis里面是这样存储的：
         * <p>
         * {
         *     "@type": "com.qiuguan.redis.bean.User",
         *     "birthday": "2023-06-30 11:44:33.919",
         *     "extend":
         *     {
         *         "@type": "java.util.HashMap"
         *     },
         *     "favorites":
         *     [
         *         "basketball",
         *         "football"
         *     ],
         *     "id": 1001L,
         *     "name": "qiuguan",
         *     "salary": 100.0
         * }
         * </p>
         *
         *
         * 使用 {@link Jackson2JsonRedisSerializer } 时做value的序列化时，redis里面是这样存储的：这也不是json呀 ？继续往下看吧
         * <p>
         *     [
         *     "com.qiuguan.redis.bean.User",
         *     {
         *         "id": 1001,
         *         "name": "qiuguan",
         *         "birthday":
         *         [
         *             2023,
         *             6,
         *             30,
         *             23,
         *             1,
         *             29,
         *             897000000
         *         ],
         *         "salary": 100.0,
         *         "favorites":
         *         [
         *             "java.util.Arrays$ArrayList",
         *             [
         *                 "basketball",
         *                 "football"
         *             ]
         *         ],
         *         "extend":
         *         [
         *             "java.util.HashMap",
         *             {}
         *         ]
         *     }
         * ]
         * </p>
         */
        list.leftPush(user);

        /**
         *使用 {@link Jackson2JsonRedisSerializer } 时在 【Object o = list.rightPop()】报错,报错如下：
         *com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.qiuguan.redis.bean.User` (no Creators, like default constructor, exist):
         * cannot deserialize from Object value (no delegate- or property-based Creator)
         * 仔细看，好像是没有默认的构造器，why ? 后来才明白，原来是的 {@link lombok.Builder} 注解导致的，请问 {@link User } 的说明
         * 按照 {@link User } 说明调整后，就完全没有问题了。
         */
        //userBeanMapping();


        /**
         * 使用 {@link FastJson2JsonRedisSerializer } 时在【User u = (User) o】行报错报错如下：
         * com.alibaba.fastjson2.JSONObject cannot be cast to com.qiuguan.redis.bean.User
         *
         * why ?
         * 我猜想可能是我的 {@link FastJson2JsonRedisSerializer} 序列化器写的有问题的，于是就检查发现，原来是忘记写
         * JSONReader.Feature.SupportAutoType。
         */
        userBeanMapping();
        return "success";
    }


    @GetMapping("/objectList")
    public String testObjectList(){
        //1.集合中有2个元素
        List<User> userList = createUserList();

        //redis中有一个key=objList, value 是 userList 集合的内容，注意，他是一个整体，也就说redis中只有一个元素
        this.redisTemplate.opsForList().leftPushAll("objList", userList);
        List<Object> objList = this.redisTemplate.opsForList().range("objList", 0, -1);
        //size = 1
        log.info("集合元素个数：{}, redis list object: {}, 空集合: {}", objList.size(), objList,  new ArrayList<>());


        //放入3个元素，其中第三个元素是一个集合，里面有2个元素
        this.redisTemplate.opsForList().leftPushAll("objList2", createUser(), createUser(), createUserList());
        List<Object> objList2 = this.redisTemplate.opsForList().range("objList2", 0, -1);
        //size = 3
        log.info("集合元素个数：{}, redis objList2 object: {},  空集合: {}", objList2.size(), objList2, new ArrayList<>());


        //集合List<Object> -> List<User> ?
        Object o = this.redisTemplate.opsForList().leftPop("objList");
        log.info("集合objList弹出一个元素，元素类型是Object，但是他是一个包含了2个元素的JSONArray: {}, classType = {}", o, o.getClass());

        //com.alibaba.fastjson2.JSONArray cannot be cast to com.qiuguan.redis.bean.User
        //User user = (User) o;
        //log.info("类型转换：user = {}", user);

        //难道还需要每次都类型判断？？
        if (o instanceof JSONObject) {

            //TODO..
        } else if (o instanceof JSONArray) {
            //TODO..
        }

        //这也太麻烦了。。。。
        //所以说，使用redisTemplate 操作 list 集合时，要么就使用一个ArrayList, 将所有对象保存在这里，对于redis来说，list中就保存了一个
        //对象，就是ArrayList, 要么就一个一个填充对象bean对象，这对redis或者java来说，都是集合了。

        Person p = new Person();
        p.setId(2001L);
        p.setCountry("china");
        p.setUser(createUser());
        this.redisTemplate.opsForList().leftPush("cc",  p);
        Person pp = (Person) this.redisTemplate.opsForList().rightPop("cc");
        log.info("级联bean 保存到redis中，取出后映射成bean pp = {}", pp);
        return "redis list object";
    }


    private void userBeanMapping(){
        Object o = this.redisTemplate.opsForList().leftPop("list");
        User u = (User) o;
        System.out.println("u = " + u);
    }

}
