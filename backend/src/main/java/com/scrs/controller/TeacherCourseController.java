package com.scrs.controller;/*
                            * @date 12/05 18:56
                            */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.common.R;
import com.scrs.pojo.Course;
import com.scrs.pojo.Teacher;
import com.scrs.pojo.TeacherCourse;
import com.scrs.service.CourseService;
import com.scrs.service.TeacherCourseService;
import com.scrs.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/teacherCourse")
public class TeacherCourseController {
    @Autowired
    private TeacherCourseService teacherCourseService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TeacherService teacherService;

    /**
     * id为teacherCourse的id，可以不提供
     */
    @GetMapping("/listTeacherCourse")
    public R<PageInfo> listTeacherCourse(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            @RequestParam(required = false) Integer id) {

        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);

        List<TeacherCourse> teacherCourseList;
        if (id != null) {
            QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            teacherCourseList = teacherCourseService.list(queryWrapper);
        } else {
            teacherCourseList = teacherCourseService.list(null);
        }
        PageInfo<TeacherCourse> pageInfo = new PageInfo<>(teacherCourseList);
        return R.success(pageInfo);
    }

    @GetMapping("/preSaveTeacherCourse")
    public R<HashMap<String,Object>> preSaveTeacherCourse() {
        List<Course> courseList = courseService.list(null);
        List<Teacher> teacherList = teacherService.list(null);
        //model.addAttribute("courseList", courseList);
        //model.addAttribute("teacherList", teacherList);
        HashMap<String, Object> map = new HashMap<>();
        map.put("courseList", courseList);
        map.put("teacherList", teacherList);
        return R.success(map);
    }


    @GetMapping("/saveTeacherCourse")
    public R<String> saveTeacherCourse(@RequestParam Integer tid,@RequestParam Integer cid, HttpSession session) {
        QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid);
        queryWrapper.eq("cid", cid);
        TeacherCourse one = teacherCourseService.getOne(queryWrapper);
        if (one != null) {
            return R.error("该老师已经教授该课程");
        }
        Teacher teacher = teacherService.getById(tid);
        Course course = courseService.getById(cid);
        if (!teacher.getMajor().equals(course.getMajor())) {
            return R.error("该老师不是该课程的专业的老师");
        }
        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setTid(tid);
        teacherCourse.setCid(cid);
        teacherCourseService.save(teacherCourse);
        return R.success("新增教师课程成功");
    }

    @GetMapping("/preUpdateTeacherCourse")
    public R<HashMap<String,Object>> preUpdateTeacherCourse(Integer id,HttpSession session) {
        session.setAttribute("teacherCourseId", id);
        TeacherCourse teacherCourse = teacherCourseService.getById(id);
        List<Course> courseList = courseService.list(null);
        List<Teacher> teacherList = teacherService.list(null);
        //model.addAttribute("teacherCourse", teacherCourse);
        //model.addAttribute("courseList", courseList);
        //model.addAttribute("teacherList", teacherList);
        HashMap<String, Object> map = new HashMap<>();
        map.put("teacherCourse", teacherCourse);
        map.put("courseList", courseList);
        map.put("teacherList", teacherList);
        return R.success(map);
    }

    @GetMapping("/updateTeacherCourse")
    public R<String> updateTeacherCourse(@RequestParam Integer tid,@RequestParam Integer cid, HttpSession session) {
        Integer teacherCourseId = (Integer) session.getAttribute("teacherCourseId");

        Teacher teacher = teacherService.getById(tid);
        Course course = courseService.getById(cid);
        if (!teacher.getMajor().equals(course.getMajor())) {
            return R.error("该老师不是该课程的专业的老师");
        }
        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setId(teacherCourseId);
        teacherCourse.setTid(tid);
        teacherCourse.setCid(cid);
        teacherCourseService.updateById(teacherCourse);
        return R.success("修改教师课程成功");
    }

    @GetMapping("/deleteTeacherCourse/{id}")
    public R<String> deleteTeacherCourse(@PathVariable Integer id) {
        boolean b = teacherCourseService.removeById(id);
        if (!b) {
            return R.error("删除失败");
        }
        return R.success("删除成功");
    }
    @PostMapping("/deleteBatchTeacherCourse")
    public R<String> deleteBatchTeacherCourse(@RequestBody String ids) {
        String[] split = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            idList.add(Integer.parseInt(s));
        }
        boolean b = teacherCourseService.removeByIds(idList);
        if (!b) {
            return R.error("批量删除失败");
        }
        return R.success("批量删除成功");
    }
}
