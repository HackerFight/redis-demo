package com.qiuguan.redis.bean;

import lombok.Data;

/**
 * @author fu yuan hui
 * @date 2023-06-29 12:52:35 Thursday
 */
@Data
public class Person {

    private Long id;

    private String country;

    private User user;
}
