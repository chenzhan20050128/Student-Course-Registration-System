package com.scrs.service.impl;/*
 * @date 12/07 12:57
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.StudentCourseMapper;
import com.scrs.pojo.StudentCourse;
import com.scrs.service.StudentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements StudentCourseService {
    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Override
    public List<StudentCourse> listStudentCourse(Integer id) {
        return studentCourseMapper.listStudentCourse(id);
    }


}
