package com.scrs.pojo;/*
                      * @date 12/05 13:45
                      */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("course")
public class Course {
    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;
    
    @TableField("course_name")
    private String courseName;
    
    @TableField("credits")
    private Integer credits;
    
    @TableField("description")
    private String description;
    
    @TableField("college")
    private String college;
    
    @TableField("instructor_name")
    private String instructorName;
    
    @TableField("campus")
    private String campus;
    
    @TableField("classroom")
    private String classroom;
    
    @TableField("start_week")
    private Integer startWeek;
    
    @TableField("end_week")
    private Integer endWeek;
    
    @TableField("capacity")
    private Integer capacity;
    
    @TableField("enrolled_count")
    private Integer enrolledCount;
    
    @TableField("type")
    private String type;
}
