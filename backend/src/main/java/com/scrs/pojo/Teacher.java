package com.scrs.pojo;/*
 * @date 12/04 21:10
 */

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teacher")
public class Teacher {
    private Integer id;

    private String tname;//注意是tname不是name！

    private String password;

    private String email;

    private String sex;

    private String phone;

    private Integer age;

    private String major;

    private String timage;
}
