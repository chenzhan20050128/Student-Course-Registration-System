package com.scrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scrs.pojo.TeacherCourse;

import java.util.List;

public interface TeacherCourseMapper extends BaseMapper<TeacherCourse> {
    List<TeacherCourse> listTeacherCourse(Integer teacherCourseId);
}
