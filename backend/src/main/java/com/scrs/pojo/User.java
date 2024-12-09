package com.scrs.pojo;/*
 * @date 12/04 20:22
 */

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    private Integer id;

    //private String email;

    private String password;

    private String username;

    //private String phone;

    //private String image;
}
