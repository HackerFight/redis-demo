package com.qiuguan.redis.utils;

import com.qiuguan.redis.bean.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author fu yuan hui
 * @date 2023-07-01 23:10:18 Saturday
 */
public abstract class BeanCreator {

    public static User createUser(){
        return User.builder().id(1001L)
                .name("qiuguan")
                .birthday(LocalDateTime.now())
                .salary(100.0)
                .favorites(Arrays.asList("basketball", "football"))
                .extend(new HashMap<>())
                .build();
    }


    public static List<User> createUserList() {
        List<User> list = new ArrayList<>();
        User user = com.qiuguan.redis.bean.User.builder().id(1001L)
                .name("qiuguan")
                .birthday(LocalDateTime.now())
                .salary(100.0)
                .favorites(Arrays.asList("basketball", "football"))
                .extend(new HashMap<>())
                .build();

        User user2 = com.qiuguan.redis.bean.User.builder().id(1002L)
                .name("qiuguan222")
                .birthday(LocalDateTime.now())
                .salary(200.0)
                .favorites(Arrays.asList("SONG", "SWIMMING"))
                .extend(new HashMap<>())
                .build();

        list.add(user);
        list.add(user2);

        return list;
    }
}
