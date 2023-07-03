package com.qiuguan.redis.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author fu yuan hui
 * @date 2023-06-29 12:51:02 Thursday
 *
 *
 * 这里一定要注意，使用 {@link Builder} 注解时，他会默认生成所有参数的构造器，而且修饰符是default的
 * 而框架一般都会用到反射，比如序列化框架jackson ，这样就容器出问题，所以推荐添加上
 * {@link AllArgsConstructor} 和 {@link NoArgsConstructor} 注解，主动生成有参和无参的构造器
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User implements Serializable {

    private Long id;

    private String name;

    private LocalDateTime birthday;

    private Double salary;

    private List<String> favorites;

    private Map<String, String> extend;
}
