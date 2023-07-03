package com.qiuguan.redis.redis.plus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fu yuan hui
 * @date 2023-07-03 11:02:09 Monday
 *
 * 消息队列
 */
@Slf4j
@RequestMapping("/redis/stream/")
@RestController
@AllArgsConstructor
public class StreamController {

    private final RedisTemplate<Object, Object> redisTemplate;
}
