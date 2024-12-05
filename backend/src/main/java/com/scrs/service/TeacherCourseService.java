package com.scrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.TeacherCourse;

import java.util.List;

public interface TeacherCourseService extends IService<TeacherCourse> {
    List<TeacherCourse> listTeacherCourse(Integer teacherCourseId);
}
