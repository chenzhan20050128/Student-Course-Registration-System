package com.scrs.pojo;/*
 * @date 12/05 12:37
 */

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("college")
public class College {
    private Integer id;
    private String cname;//注意是cname不是name！
    private String descr;
}
