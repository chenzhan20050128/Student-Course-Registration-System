package com.scrs.controller;
/*
                            * @date 12/05 13:48
                            */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.pojo.Course;
import com.scrs.pojo.Major;
import com.scrs.service.CourseService;
import com.scrs.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private MajorService majorService;

    @Value("${file.location}") // 获取配置文件中的文件上传路径
    private String location;


    @RequestMapping("/listCourse")
    public String listCourse(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            Model model, Course course) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (course.getCname() != null) {
            queryWrapper.like("cname", course.getCname());
        }
        PageInfo pageInfo = new PageInfo(courseService.list(queryWrapper));
        model.addAttribute("pageInfo", pageInfo);
        return "admin-course-list";
    }

    /**
     * 需要preSave的原因是因为saveCourse前需要先获取majorList，然后有个单选框可以选择major
     */
    @RequestMapping("/preSaveCourse")
    public String preSaveCourse(Model model){
        List<Major> majorList = majorService.list(null);
        model.addAttribute("majorList", majorList);
        return "admin-course-save";
    }

    @RequestMapping("/saveCourse")
    public String saveCourse(Course course, MultipartFile file,MultipartFile fileBook) throws IOException {
        //两个文件，一个是课程的图片，一个是课程介绍和电子书的文档
        if (!file.isEmpty()) {
            try {
                transfile(course, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!fileBook.isEmpty()) {
            try {
                transfileBook(course, fileBook);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        courseService.save(course);
        return "redirect:/course/listCourse";
    }
    private void transfile(Course course, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        int i = 0;
        if (originalFilename != null) {
            i = originalFilename.lastIndexOf(".");
        }
        String suffix = originalFilename.substring(i);
        String prefix = System.nanoTime() + "";
        String filename = prefix + suffix;

        // 创建一个File对象，表示文件所在的目录路径
        File file1 = new File(location);
        // 检查该目录是否存在
        if (!file1.exists()) {
            // 如果目录不存在，则创建该目录（包括必要的父目录）
            file1.mkdirs();
        }
        // 创建一个File对象，表示具体的文件路径，将文件名添加到目录中
        File file2 = new File(file1, filename);
        try {
            // 使用传输文件的方法将内容写入到file2的路径中
            file.transferTo(file2);
        } catch (IOException e) {
            // 如果在文件传输过程中出现IO异常，则打印异常栈跟踪以进行调试
            e.printStackTrace();
        }
        course.setCimage(filename);
    }
    private void transfileBook(Course course, MultipartFile fileBook) throws IOException {
        String originalFilename = fileBook.getOriginalFilename();
        int i = 0;
        if (originalFilename != null) {
            i = originalFilename.lastIndexOf(".");
        }
        String suffix = originalFilename.substring(i);
        String prefix = System.nanoTime() + "";
        String filename = prefix + suffix;

        // 创建一个File对象，表示文件所在的目录路径
        File file1 = new File(location);
        // 检查该目录是否存在
        if (!file1.exists()) {
            // 如果目录不存在，则创建该目录（包括必要的父目录）
            file1.mkdirs();
        }
        // 创建一个File对象，表示具体的文件路径，将文件名添加到目录中
        File file2 = new File(file1, filename);
        try {
            // 使用传输文件的方法将内容写入到file2的路径中
            fileBook.transferTo(file2);
        } catch (IOException e) {
            // 如果在文件传输过程中出现IO异常，则打印异常栈跟踪以进行调试
            e.printStackTrace();
        }
        course.setCbook(filename);
    }

    @RequestMapping("/preUpdateCourse/{id}")
    public String preUpdateCourse(@PathVariable Integer id,Model model){
        Course course = courseService.getById(id);
        model.addAttribute("course", course);
        List<Major> majorList = majorService.list(null);
        model.addAttribute("majorList", majorList);
        return "admin-course-update";
    }

    //管理员只能修改课程图像，不能修改课程介绍电子书，只能由老师修改
    @RequestMapping("/updateCourse")
    public String updateCourse(Course course, MultipartFile file){
        if (!file.isEmpty()) {
            try {
                transfile(course, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        courseService.updateById(course);
        return "redirect:/course/listCourse";
    }

    @RequestMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable Integer id,Model model){
        boolean b = courseService.removeById(id);
        if (!b){
            model.addAttribute("error","删除课程失败");
        }
        return "redirect:/course/listCourse";
    }

    @RequestMapping("deleteBatchCourse")
    public String deleteBatchCourse(@RequestBody String ids, Model model){
        String[] split = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s: split){
            if (!s.isEmpty()){
                idList.add(Integer.parseInt(s));
            }
        }

        boolean b = courseService.removeByIds(idList);
        if (!b){
            model.addAttribute("error","批量删除课程失败");
        }
        return "redirect:/course/listCourse";
    }

    /**
     * 新增方法：根据专业查询课程
     * @param pageNum
     * @param pageSize
     * @param model
     * @param course
     * @return
     */
    @RequestMapping("/listCourseByMajor")
    public String listCourseByMajor(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                    Model model, Course course){
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (course.getMajor() != null){
            queryWrapper.eq("major",course.getMajor());
        }
        PageInfo pageInfo = new PageInfo(courseService.list(queryWrapper));
        model.addAttribute("pageInfo", pageInfo);
        return "admin-course-list";
    }

    /**
     * 新增方法：根据教师查询课程
     * @param pageNum
     * @param pageSize
     * @param model
     * @param course
     * @return
     */
    @RequestMapping("/listCourseByTeacher")
    public String listCourseByTeacher(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                      Model model, Course course){
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (course.getTeacher() != null){
            queryWrapper.eq("teacher",course.getTeacher());
        }
        PageInfo pageInfo = new PageInfo(courseService.list(queryWrapper));
        model.addAttribute("pageInfo", pageInfo);
        return "admin-course-list";
    }

}
