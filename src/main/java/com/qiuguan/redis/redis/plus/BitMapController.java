package com.qiuguan.redis.redis.plus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @author fu yuan hui
 * @date 2023-07-03 11:16:13 Monday
 *
 * 1. Redis的Bitmaps)这个“数据结构"可以实现对位的操作。Bitmaps本身不是一种数据结构，实际上就是字符
 *    串，但是它可以对字符串的位进行操作
 * 2. 可把Bitmaps想象成一个以位为单位数组，数组中的每个单元只能存0或者1，数组的下标在bitmaps中叫做偏移量
 * 3. 单个bitmapsl的最大长度是512MB,即232个比特位, bitmaps的最大优势是节省存储空间。比如在一个以自增id代表不同用户的系统中，我们只需要512MB空
 *    间就可以记录40亿用户的某个单一信息，相比mysql节省了大量的空间
 * 4. 有两种类型的位操作：一类是对特定bit位的操作，比如设置获取某个特定比特位的值。另一类是批量bit位操作，例如在给定范围内统计为1的比特位个数
 *
 *
 * 参考文档：https://www.cnblogs.com/liujiduo/p/10396020.html
 */
@Slf4j
@RequestMapping("/redis/bit/")
@RestController
@AllArgsConstructor
public class BitMapController {

    private final RedisTemplate<Object, Object> redisTemplate;

    public static void main(String[] args) {
        String format = String.format("u%d", LocalDate.now().getDayOfMonth());
        System.out.println("format = " + format);
    }
}
