package com.scrs.pojo;/*
 * @date 12/07 12:53
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_course")
public class StudentCourse {
    @TableId
    private Integer id;

    private Integer sid;
    private Integer cid;

    private Integer status;

    @TableField(exist = false)
    private Student student;

    @TableField(exist = false)
    private Course course;

    @TableField(exist = false)
    private String name;
}
