package com.scrs.controller;/*
                            * @date 12/05 18:56
                            */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.pojo.Course;
import com.scrs.pojo.Teacher;
import com.scrs.pojo.TeacherCourse;
import com.scrs.service.CourseService;
import com.scrs.service.TeacherCourseService;
import com.scrs.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teacherCourse")
public class TeacherCourseController {
    @Autowired
    private TeacherCourseService teacherCourseService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TeacherService teacherService;

    @RequestMapping("/listTeacherCourse")
    public String listTeacherCourse(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            Model model,
            TeacherCourse teacherCourse) {

        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);

        List<TeacherCourse> teacherCourseList;
        if (teacherCourse.getId() != null) {
            teacherCourseList = teacherCourseService.listTeacherCourse(teacherCourse.getId());
        } else {
            teacherCourseList = teacherCourseService.list(null);
        }
        PageInfo<TeacherCourse> pageInfo = new PageInfo<>(teacherCourseList);
        model.addAttribute("pageInfo", pageInfo);
        return "admin-teacher-course-list";
    }

    @RequestMapping("/preSaveTeacherCourse")
    public String preSaveTeacherCourse(Model model) {
        List<Course> courseList = courseService.list(null);
        List<Teacher> teacherList = teacherService.list(null);
        model.addAttribute("courseList", courseList);
        model.addAttribute("teacherList", teacherList);
        return "admin-teacher-course-save";
    }


    @RequestMapping("/saveTeacherCourse")
    public String saveTeacherCourse(Integer tid, Integer cid, Model model, HttpSession session) {
        QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid);
        queryWrapper.eq("cid", cid);
        TeacherCourse one = teacherCourseService.getOne(queryWrapper);
        if (one != null) {
            model.addAttribute("msg", "该老师已经教过该课程");
            List<Course> courseList = courseService.list(null);
            List<Teacher> teacherList = teacherService.list(null);
            model.addAttribute("courseList", courseList);
            model.addAttribute("teacherList", teacherList);
            return "admin-teacher-course-save";
        }
        Teacher teacher = teacherService.getById(tid);
        Course course = courseService.getById(cid);
        if (!teacher.getMajor().equals(course.getMajor())) {
            model.addAttribute("msg", "该老师不是该课程的专业的老师");
            List<Course> courseList = courseService.list(null);
            List<Teacher> teacherList = teacherService.list(null);
            model.addAttribute("courseList", courseList);
            model.addAttribute("teacherList", teacherList);
            return "admin-teacher-course-save";
        }
        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setTid(tid);
        teacherCourse.setCid(cid);
        teacherCourseService.save(teacherCourse);
        return "redirect:/teacherCourse/listTeacherCourse";
    }

    @RequestMapping("/preUpdateTeacherCourse")
    public String preUpdateTeacherCourse(Integer id, Model model,HttpSession session) {
        session.setAttribute("teacherCourseId", id);
        TeacherCourse teacherCourse = teacherCourseService.getById(id);
        List<Course> courseList = courseService.list(null);
        List<Teacher> teacherList = teacherService.list(null);
        model.addAttribute("teacherCourse", teacherCourse);
        model.addAttribute("courseList", courseList);
        model.addAttribute("teacherList", teacherList);
        return "admin-teacher-course-update";
    }

    @RequestMapping("/updateTeacherCourse")
    public String updateTeacherCourse(Integer tid, Integer cid, Model model, HttpSession session) {
        Integer teacherCourseId = (Integer) session.getAttribute("teacherCourseId");

        Teacher teacher = teacherService.getById(tid);
        Course course = courseService.getById(cid);
        if (!teacher.getMajor().equals(course.getMajor())) {
            model.addAttribute("msg", "该老师不是该课程的专业的老师");
            TeacherCourse teacherCourse = teacherCourseService.getById(teacherCourseId);
            List<Course> courseList = courseService.list(null);
            List<Teacher> teacherList = teacherService.list(null);
            model.addAttribute("teacherCourse", teacherCourse);
            model.addAttribute("courseList", courseList);
            model.addAttribute("teacherList", teacherList);
            return "admin-teacher-course-update";
        }
        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setId(teacherCourseId);
        teacherCourse.setTid(tid);
        teacherCourse.setCid(cid);
        teacherCourseService.updateById(teacherCourse);
        return "redirect:/teacherCourse/listTeacherCourse";
    }

    @RequestMapping("/deleteTeacherCourse/{id}")
    public String deleteTeacherCourse(@PathVariable Integer id) {
        teacherCourseService.removeById(id);
        return "redirect:/teacherCourse/listTeacherCourse";
    }
    @RequestMapping("/deleteBatchTeacherCourse")
    public String deleteBatchTeacherCourse(@RequestBody String ids,Model model) {
        String[] split = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            idList.add(Integer.parseInt(s));
        }
        boolean b = teacherCourseService.removeByIds(idList);
        if (!b) {
            model.addAttribute("msg", "删除失败");
        }
        return "redirect:/teacherCourse/listTeacherCourse";
    }
}
