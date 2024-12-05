package com.scrs.controller;/*
 * @date 12/05 18:56
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.pojo.TeacherCourse;
import com.scrs.service.TeacherCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/teacherCourse")
public class TeacherCourseController {
    @Autowired
    private TeacherCourseService teacherCourseService;

    @RequestMapping("/listTeacherCourse")
    public String listTeacherCourse(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                    Model model, TeacherCourse teacherCourse) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<TeacherCourse> teacherCourseList = teacherCourseService.listTeacherCourse(teacherCourse.getId());
        PageInfo<TeacherCourse> pageInfo = new PageInfo(teacherCourseList);
        model.addAttribute("pageInfo", pageInfo);
        return "admin-teacher-course-list";
    }
}
