package com.scrs.controller;/*
 * @date 12/07 12:57
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.common.R;
import com.scrs.pojo.Course;
import com.scrs.pojo.Student;
import com.scrs.pojo.StudentCourse;
import com.scrs.service.CourseService;
import com.scrs.service.StudentCourseService;
import com.scrs.service.StudentCourseService;
import com.scrs.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/studentCourse")
public class StudentCourseController {

    @Autowired
    private StudentCourseService studentCourseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;


    /*
     * 前端注意：这个id是studentCourse的id，可以不提供
     */
    @RequestMapping("/listStudentCourse")
    public R<PageInfo> listStudentCourse(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                         @RequestParam(required = false) Integer id) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StudentCourse> studentCourseList = studentCourseService.listStudentCourse(id);
        PageInfo<StudentCourse> pageInfo = new PageInfo<>(studentCourseList);
        //model.addAttribute("pageInfo", pageInfo);

        return R.success(pageInfo);
    }

    @GetMapping("/preSaveStudentCourse")
    public R<HashMap<String,Object>> preSaveStudentCourse() {
        List<Student> studentList = studentService.list(null);
        List<Course> courseList = courseService.list(null);
        //model.addAttribute("studentList", studentList);
        //model.addAttribute("courseList", courseList);
        HashMap<String,Object> map = new HashMap<>();
        map.put("studentList", studentList);
        map.put("courseList", courseList);

        return R.success(map);
    }

    @GetMapping("/saveStudentCourse")
    public R<String> saveStudentCourse(@RequestParam Integer sid,@RequestParam Integer cid, HttpSession session) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid", sid);
        queryWrapper.eq("cid", cid);
        StudentCourse one = studentCourseService.getOne(queryWrapper);
        if (one != null) {
            //model.addAttribute("msg", "该学生已经选过该课程");
            return R.error("该学生已经选过该课程");
        }
        Student student = studentService.getById(sid);
        Course course = courseService.getById(cid);

        if (course.getNum() >= course.getStock()){
            //model.addAttribute("msg", "该课程已经选满");
            return R.error("该课程已经选满");
        }

        course.setNum(course.getNum() + 1);
        courseService.updateById(course);
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setSid(sid);
        studentCourse.setCid(cid);
        studentCourseService.save(studentCourse);

        return R.success("选课成功");
    }

    @GetMapping("/preUpdateStudentCourse/{id}")
    public R<HashMap<String,Object>> preUpdateStudentCourse(@PathVariable Integer id,HttpSession session) {
        session.setAttribute("studentCourseId", id);
        StudentCourse studentCourse = studentCourseService.getById(id);
        List<Course> courseList = courseService.list(null);
        List<Student> studentList = studentService.list(null);
        //model.addAttribute("studentList", studentList);
        //model.addAttribute("courseList", courseList);
        //model.addAttribute("studentCourse", studentCourse);
        HashMap<String,Object> map = new HashMap<>();
        map.put("studentList", studentList);
        map.put("courseList", courseList);
        map.put("studentCourse", studentCourse);
        return R.success(map);
    }

    @GetMapping("/updateStudentCourse")
    public R<String> updateStudentCourse(@RequestParam Integer sid,@RequestParam Integer cid, Model model, HttpSession session) {
        Integer studentCourseId = (Integer) session.getAttribute("studentCourseId");
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid", sid);
        queryWrapper.eq("cid", cid);
        StudentCourse one = studentCourseService.getOne(queryWrapper);
        if (one != null) {
            //model.addAttribute("msg", "该学生已经选过该课程");
            R.error("该学生已经选过该课程");
        }

        Student student = studentService.getById(sid);
        Course course = courseService.getById(cid);
        if (student.getMajor() != null){
            if (!student.getMajor().equals(course.getMajor())){
                //model.addAttribute("msg", "该学生的专业与课程不符");
                return R.error("该学生的专业与课程不符");
            }
        }

        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setId(studentCourseId);
        studentCourse.setSid(sid);
        studentCourse.setCid(cid);
        boolean b = studentCourseService.updateById(studentCourse);
        if (!b){
            //model.addAttribute("msg", "更新失败");
            return R.error("更新失败");
        }
        return R.success("更新成功");
    }

    @GetMapping("/deleteStudentCourse/{id}")
    public R<String> deleteStudentCourse(@PathVariable Integer id) {
        StudentCourse studentCourse = studentCourseService.getById(id);
        studentCourseService.removeById(id);
        return R.success("删除成功");
    }

    @PostMapping("/deleteBatchStudentCourse")
    public R<String> deleteBatchStudentCourse(@RequestBody String ids) {
        String[] split = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            idList.add(Integer.parseInt(s));
        }
        boolean b = studentCourseService.removeByIds(idList);
        if (!b) {
            //model.addAttribute("error", "批量删除失败");
            return R.error("批量删除失败");
        }
        return R.success("批量删除成功");
    }

}
