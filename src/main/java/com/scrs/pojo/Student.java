package com.scrs.pojo;/*
 * @date 12/04 21:22
 */

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student")
public class Student {
    private Integer id;

    private String sname;

    private String password;

    private String sex;

    private Integer age;

    private String major;

    private String college;

    private String simage;
}
