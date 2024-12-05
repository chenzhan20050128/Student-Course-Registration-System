package com.scrs.pojo;/*
 * @date 12/05 18:51
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teacher_course")
public class TeacherCourse {
    @TableId
    private Integer id;
    private Integer tid;
    private Integer cid;

    @TableField(exist = false)//表示实际上数据库字段不存在这个字段
    private Teacher teacher;
    @TableField(exist = false)
    private Course course;
}
