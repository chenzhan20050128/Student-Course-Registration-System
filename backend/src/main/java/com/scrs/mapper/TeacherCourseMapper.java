package com.scrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scrs.pojo.TeacherCourse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Mapper
public interface TeacherCourseMapper extends BaseMapper<TeacherCourse> {
    List<TeacherCourse> listTeacherCourse(Integer teacherCourseId);

    List<TeacherCourse> listMyCourse(Integer userId, String cname);
}
