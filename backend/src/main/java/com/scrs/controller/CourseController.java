package com.scrs.controller;
/*
                            * @date 12/05 13:48
                            */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.common.R;
import com.scrs.pojo.Course;
import com.scrs.pojo.Major;
import com.scrs.service.CourseService;
import com.scrs.service.MajorService;
import com.scrs.utils.CacheClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private CourseService courseService;

    @Autowired
    private MajorService majorService;

    @Value("${file.location}") // 获取配置文件中的文件上传路径
    private String location;

    @GetMapping("/listCourse")
    public R<PageInfo> listCourse(
            @RequestParam(value = "pageNum", defaultValue = "1",required = false) Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6",required = false) Integer pageSize,
            @RequestParam(required = false) String name) {
        log.info("pageNum: {}, pageSize: {}, name: {}", pageNum, pageSize, name);

        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        if (name != null && !name.isEmpty()) {

            PageHelper.startPage(pageNum, pageSize);
            LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(Course::getCname, name);
            PageInfo pageInfo = new PageInfo<>(courseService.list(queryWrapper));
            return R.success(pageInfo);
        }

        String key = "courseList";
        Integer finalPageSize = pageSize;
        Integer finalPageNum = pageNum;
        //log.info("PageInfo pageInfo = cacheClient.queryWithLogicalExpire");
        PageInfo pageInfo = cacheClient.queryWithLogicalExpire(
                key,
                PageInfo.class,
                () -> {
                    log.info("Redis查询失败，直接从数据库中返回数据");
                    PageHelper.startPage(finalPageNum, finalPageSize);
                    LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
                    List<Course> courseList = courseService.list(queryWrapper);
                    return new PageInfo(courseList);
                },
                6000L // 缓存过期时间，单位：秒
        );
        return R.success(pageInfo);
    }

    /**
     * 需要preSave的原因是因为saveCourse前需要先获取majorList，然后有个单选框可以选择major
     */
    @GetMapping("/preSaveCourse")
    public R<List<Major>> preSaveCourse() {
        String key = "majorList";

        // 使用CacheClient查询缓存
        List<Major> majorList = cacheClient.queryWithLogicalExpire(
                key,
                List.class,
                () -> majorService.list(null),
                60L // 缓存过期时间，单位：秒
        );

        R<List<Major>> result = R.success(majorList);
        System.out.println(result.toString());
        return result;
    }

    /**
     * 保存课程，先不保存文件
     */
    @PostMapping("/saveCourse")
    public R<String> saveCourse(@RequestBody Course course,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        // System.out.println(course.toString());
        if (file != null && !file.isEmpty()) {
            transfile(course, file);
        }
        /*
         * if (fileBook != null && !fileBook.isEmpty()) {
         * transfileBook(course, fileBook);
         * }
         */
        courseService.save(course);

        String key = "courseList";
        PageInfo<Course> pageInfo = new PageInfo<>(courseService.list(null));
        cacheClient.setWithLogicalExpire(key, pageInfo, 6000L); // 缓存过期时间，单位：秒

        return R.success("save course successfully");
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

    @GetMapping("/preUpdateCourse/{id}")
    public R<HashMap<String, Object>> preUpdateCourse(@PathVariable Integer id) {
        Course course = courseService.getById(id);
        String key = "majorList";
        List<Major> majorList = cacheClient.queryWithLogicalExpire(
                key,
                List.class,
                () -> majorService.list(null),
                60L // 缓存过期时间，单位：秒
        );
        HashMap<String, Object> map = new HashMap<>();
        map.put("course", course);
        map.put("majorList", majorList);
        R<HashMap<String, Object>> result = R.success(map);

        System.out.println(result.toString());
        return result;
    }

    // 管理员只能修改课程图像，不能修改课程介绍电子书，只能由老师修改
    @PostMapping("/updateCourse")
    public R<String> updateCourse(@RequestBody Course course) {
        // , MultipartFile file
        // if (!file.isEmpty()) {
        // try {
        // transfile(course, file);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        courseService.updateById(course);

        String key = "courseList";
        PageInfo<Course> pageInfo = new PageInfo<>(courseService.list(null));
        cacheClient.setWithLogicalExpire(key, pageInfo, 6000L); // 缓存过期时间，单位：秒

        return R.success("update course successfully");
    }

    @GetMapping("/deleteCourse/{id}")
    public R<String> deleteCourse(@PathVariable Integer id) {
        boolean b = courseService.removeById(id);
        if (!b) {
            return R.error("delete course failed");
        }
        return R.success("delete course successfully");
    }

    @PostMapping("/deleteBatchCourse")
    public R<String> deleteBatchCourse(@RequestBody String ids) {
        // TODO：传入的字符串格式是 "{ids: "1,2,3,4,5"}"，需要处理一下
        String[] split = ids.substring(8, ids.length() - 2).split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                idList.add(Integer.parseInt(s));
            }
        }

        boolean b = courseService.removeByIds(idList);
        if (!b) {
            // model.addAttribute("error", "批量删除课程失败");
            return R.error("delete batch course failed");
        }
        return R.success("delete batch course successfully");
    }

    /**
     * 新增方法：根据专业查询课程
     * 
     * @param pageNum
     * @param pageSize
     * @param model
     * @param course
     * @return
     */
    @RequestMapping("/listCourseByMajor")
    public String listCourseByMajor(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
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
        if (course.getMajor() != null) {
            queryWrapper.eq("major", course.getMajor());
        }
        PageInfo pageInfo = new PageInfo(courseService.list(queryWrapper));
        model.addAttribute("pageInfo", pageInfo);
        return "admin-course-list";
    }

    /**
     * 新增方法：根据教师查询课程
     * 
     * @param pageNum
     * @param pageSize
     * @param model
     * @param course
     * @return
     */
    @RequestMapping("/listCourseByTeacher")
    public String listCourseByTeacher(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
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
        if (course.getTeacher() != null) {
            queryWrapper.eq("teacher", course.getTeacher());
        }
        PageInfo pageInfo = new PageInfo(courseService.list(queryWrapper));
        model.addAttribute("pageInfo", pageInfo);
        return "admin-course-list";
    }

}
