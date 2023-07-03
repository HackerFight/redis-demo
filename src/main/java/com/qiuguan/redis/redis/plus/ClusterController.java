package com.qiuguan.redis.redis.plus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fu yuan hui
 * @date 2023-07-03 10:58:34 Monday
 *
 * 集群
 */
@Slf4j
@RequestMapping("/redis/cluster/")
@RestController
@AllArgsConstructor
public class ClusterController {

    private final RedisTemplate<Object, Object> redisTemplate;

    @GetMapping("/C")
    public String test1(){
        this.redisTemplate.opsForCluster();

        return "CLUSTER";
    }
}
