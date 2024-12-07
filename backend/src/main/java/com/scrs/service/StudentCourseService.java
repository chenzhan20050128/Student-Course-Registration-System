package com.scrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.StudentCourse;

import java.util.List;

public interface StudentCourseService extends IService<StudentCourse> {
    List<StudentCourse> listStudentCourse(Integer id);
}
