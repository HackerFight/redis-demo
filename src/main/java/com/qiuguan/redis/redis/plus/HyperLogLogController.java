package com.qiuguan.redis.redis.plus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fu yuan hui
 * @date 2023-07-03 11:02:46 Monday
 *
 * 1. Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定 的、并且是很小的。
 *
 * 2. 在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基 数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。
 *
 * 3. 但是，因为 HyperLogLog 只会根据输入元素来计算基数，而不会储存输入元素本身，所以 HyperLogLog 不能像集合那样，返回输入的各个元素
 *
 *
 * 应用场景
 *  1.基数不大，数据量不大就用不上，会有点大材小用浪费空间
 *  2.有局限性，就是只能统计基数数量，而没办法去知道具体的内容是什么
 *  3.和bitmap相比，属于两种特定统计情况，简单来说，HyperLogLog 去重比 bitmap 方便很多
 *  4.一般可以bitmap和hyperloglog配合使用，bitmap标识哪些用户活跃，hyperloglog计数
 *
 * 一般使用：
 *  1.统计注册 IP 数
 *  2.统计每日访问 IP 数
 *  3.统计页面实时 UV 数
 *  4.统计在线用户数
 *  5.统计用户每天搜索不同词条的个数
 */

@Slf4j
@RequestMapping("/redis/hyper/")
@RestController
@AllArgsConstructor
public class HyperLogLogController {

    private final RedisTemplate<Object, Object> redisTemplate;

}
