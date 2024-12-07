package com.scrs.controller;/*
 * @date 12/07 12:57
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/studentCourse")
public class StudentCourseController {

    @Autowired
    private StudentCourseService studentCourseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;

    @RequestMapping("/listStudentCourse")
    public String listStudentCourse(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                    Model model, StudentCourse studentCourse) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StudentCourse> studentCourseList = studentCourseService.listStudentCourse(studentCourse.getId());
        PageInfo<StudentCourse> pageInfo = new PageInfo<>(studentCourseList);
        model.addAttribute("pageInfo", pageInfo);

        return "student-course-list";
    }

    @RequestMapping("/preSaveStudentCourse")
    public String preSaveStudentCourse(Model model) {
        List<Student> studentList = studentService.list(null);
        List<Course> courseList = courseService.list(null);
        model.addAttribute("studentList", studentList);
        model.addAttribute("courseList", courseList);
        return "admin-select-course-save";
    }

    @RequestMapping("/saveStudentCourse")
    public String saveStudentCourse(Integer sid, Integer cid, Model model, HttpSession session) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid", sid);
        queryWrapper.eq("cid", cid);
        StudentCourse one = studentCourseService.getOne(queryWrapper);
        if (one != null) {
            model.addAttribute("msg", "该学生已经选过该课程");

            List<Student> studentList = studentService.list(null);
            List<Course> courseList = courseService.list(null);
            model.addAttribute("studentList", studentList);
            model.addAttribute("courseList", courseList);
            return "admin-select-course-save";
        }
        Student student = studentService.getById(sid);
        Course course = courseService.getById(cid);

        if (course.getNum() >= course.getStock()){
            model.addAttribute("msg", "该课程已经选满");
            List<Student> studentList = studentService.list(null);
            List<Course> courseList = courseService.list(null);
            model.addAttribute("studentList", studentList);
            model.addAttribute("courseList", courseList);
            return "admin-select-course-save";
        }

        course.setNum(course.getNum() + 1);
        courseService.updateById(course);
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setSid(sid);
        studentCourse.setCid(cid);
        studentCourseService.save(studentCourse);

        return "student-course-list";
    }

    @RequestMapping("/preUpdateStudentCourse/{id}")
    public String preUpdateStudentCourse(@PathVariable Integer id, Model model,HttpSession session) {
        session.setAttribute("studentCourseId", id);
        StudentCourse studentCourse = studentCourseService.getById(id);
        List<Course> courseList = courseService.list(null);
        List<Student> studentList = studentService.list(null);
        model.addAttribute("studentList", studentList);
        model.addAttribute("courseList", courseList);
        model.addAttribute("studentCourse", studentCourse);
        return "admin-select-course-update";
    }

    @RequestMapping("/updateStudentCourse")
    public String updateStudentCourse(Integer sid, Integer cid, Model model, HttpSession session) {
        Integer studentCourseId = (Integer) session.getAttribute("studentCourseId");
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid", sid);
        queryWrapper.eq("cid", cid);
        StudentCourse one = studentCourseService.getOne(queryWrapper);
        if (one != null) {
            model.addAttribute("msg", "该学生已经选过该课程");
            StudentCourse studentCourse = studentCourseService.getById(studentCourseId);
            List<Course> courseList = courseService.list(null);
            List<Student> studentList = studentService.list(null);
            model.addAttribute("studentList", studentList);
            model.addAttribute("courseList", courseList);
            model.addAttribute("studentCourse", studentCourse);
            return "admin-select-course-update";
        }

        Student student = studentService.getById(sid);
        Course course = courseService.getById(cid);
        if (student.getMajor() != null){
            if (!student.getMajor().equals(course.getMajor())){
                model.addAttribute("msg", "该学生不是该课程的专业");
                StudentCourse studentCourse = studentCourseService.getById(studentCourseId);
                List<Course> courseList = courseService.list(null);
                List<Student> studentList = studentService.list(null);
                model.addAttribute("studentList", studentList);
                model.addAttribute("courseList", courseList);
                model.addAttribute("studentCourse", studentCourse);
                return "admin-select-course-update";
            }
        }

        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setId(studentCourseId);
        studentCourse.setSid(sid);
        studentCourse.setCid(cid);
        boolean b = studentCourseService.updateById(studentCourse);
        if (!b){
            model.addAttribute("msg", "更新失败");
            return "admin-select-course-update";
        }
        return "student-course-list";
    }

    @RequestMapping("/deleteStudentCourse/{id}")
    public String deleteStudentCourse(@PathVariable Integer id) {
        StudentCourse studentCourse = studentCourseService.getById(id);
        studentCourseService.removeById(id);
        return "student-course-list";
    }

    @RequestMapping("/deleteBatchStudentCourse")
    public String deleteBatchStudentCourse(@RequestBody String ids, Model model) {
        String[] split = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            idList.add(Integer.parseInt(s));
        }
        boolean b = studentCourseService.removeByIds(idList);
        if (!b) {
            model.addAttribute("msg", "删除失败");
        }
        return "student-course-list";
    }

}
