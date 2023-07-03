package com.qiuguan.redis.redis.plus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fu yuan hui
 * @date 2023-07-03 11:01:14 Monday
 *
 * 地图数据统计的，记录经纬度
 */

@Slf4j
@RequestMapping("/redis/geo/")
@RestController
@AllArgsConstructor
public class GeospatialController {

    private final RedisTemplate<Object, Object> redisTemplate;
}
