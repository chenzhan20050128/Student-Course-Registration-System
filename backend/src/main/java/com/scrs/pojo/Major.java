package com.scrs.pojo;/*
 * @date 12/05 12:38
 */

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("major")
public class Major {
    private Integer id;
    private String mname;//注意是mname不是name！
    private String college;
    private String descr;
}
