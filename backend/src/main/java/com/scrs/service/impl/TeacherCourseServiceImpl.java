package com.scrs.service.impl;/*
 * @date 12/05 18:54
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.TeacherCourseMapper;
import com.scrs.pojo.TeacherCourse;
import com.scrs.service.TeacherCourseService;
import com.scrs.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherCourseServiceImpl extends ServiceImpl<TeacherCourseMapper, TeacherCourse> implements TeacherCourseService{
    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

    @Override
    public List<TeacherCourse> listTeacherCourse(Integer teacherCourseId) {
        return teacherCourseMapper.listTeacherCourse(teacherCourseId);
    }
}
