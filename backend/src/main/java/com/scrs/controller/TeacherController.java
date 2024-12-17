package com.scrs.controller;/*
 * @date 12/05 14:42
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.common.R;
import com.scrs.pojo.*;
import com.scrs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentCourseService studentCourseService;

    @Autowired
    private TeacherCourseService teacherCourseService;


    @Value("${file.location}") // 获取配置文件中的文件上传路径
    private String location;
    @Autowired
    private CourseService courseService;

    @GetMapping("/listTeacher")
    public R<PageInfo> listTeacher(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            @RequestParam(required = false) String tname)
    {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        if (tname != null && !tname.isEmpty()) {
            queryWrapper.like(Teacher::getTname, tname);
        }
        List<Teacher> teacherList = teacherService.list(queryWrapper);
        PageInfo pageInfo = new PageInfo(teacherList);
        //model.addAttribute("pageInfo", pageInfo);
        return R.success(pageInfo);
    }

    @GetMapping("/preSaveTeacher")
    public R<List<Major>> preSaveTeacher(Model model) {
        List<Major> majorList = majorService.list(null);
        //model.addAttribute("majorList", majorList);

        return R.success(majorList);
    }

    @PostMapping("/saveTeacher")
    public R<String> saveTeacher(@RequestBody Teacher teacher,
        @RequestParam(value = "file", required = false) MultipartFile file) {
//        if (!file.isEmpty()){
//            try{
//                transfileTeacher(teacher, file);
//            }
//            catch (IOException e){
//                e.printStackTrace();
//            }
//        }
        String defaultPassword = "123456";
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(defaultPassword.getBytes());
        teacher.setPassword(passwordAfterMD5);
        teacherService.save(teacher);
        return R.success("新增教师成功");
    }
    private void transfileTeacher(Teacher teacher, MultipartFile file) throws IOException {
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
        teacher.setTimage(filename);
    }

    @GetMapping("/preUpdateTeacher/{id}")
    public R<HashMap<String,Object>> preUpdateTeacher(@PathVariable Integer id) {
        List<Major> listMajor = majorService.list(null);
        List<College> listCollege = collegeService.list(null);
        //model.addAttribute("majorList", majorList);
        Teacher teacher = teacherService.getById(id);
        //model.addAttribute("teacher", teacher);
        HashMap<String,Object> map = new HashMap<>();
        map.put("teacher",teacher);
        map.put("listCollege",listCollege);
        map.put("listMajor",listMajor);

        return R.success(map);
    }

    @PostMapping("/updateTeacher")
    public R<String> updateTeacher(@RequestBody Teacher teacher, MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                transfileTeacher(teacher, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        teacherService.updateById(teacher);
        return R.success("修改教师成功");
    }

    @GetMapping("/deleteTeacher/{id}")
    public R<String> deleteTeacher(@PathVariable Integer id) {
        boolean b = teacherService.removeById(id);
        if (!b) {
            //model.addAttribute("error", "删除失败");
            return R.error("删除失败");
        }

        return R.success("删除成功");
    }

    @PostMapping("/deleteBatchTeacher")
    public R<String> deleteBatchTeacher(@RequestBody String ids) {
        //TODO：传入的字符串格式是 "{ids: "1,2,3,4,5"}"，需要处理一下
        String[] split = ids.substring(8,ids.length() - 2).split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split){
            if (!s.isEmpty()) {
                idList.add(Integer.parseInt(s));
            }
        }
        teacherService.removeByIds(idList);
        boolean b = teacherService.removeByIds(idList);
        if (!b){
            //model.addAttribute("error", "批量删除失败");
            return R.error("批量删除失败");
        }
        return R.success("批量删除成功");
    }

    /**
     * 注意，前端可以不指定cname，只要设置cname等于空字符串就可以了
     */
    @GetMapping("/listMyCourse")
    public R<PageInfo> listMyCourse(String cname, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<TeacherCourse> teacherCourseList = teacherCourseService.listMyCourse(userId, cname);
        PageInfo<TeacherCourse> pageInfo = new PageInfo<>(teacherCourseList);
        //model.addAttribute("pageInfo", pageInfo);
        return R.success(pageInfo);
    }

    @GetMapping("/check/{id}")
    public R<List<Student>> check(@PathVariable Integer id){
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", id);
        List<StudentCourse> studentCourseList = studentCourseService.list(queryWrapper);
        List<Student> studentList = new ArrayList<>();
        for (StudentCourse sc: studentCourseList){
            Student student = studentService.getById(sc.getSid());
            studentList.add(student);
        }

        //model.addAttribute("studentList", studentList);
        return R.success(studentList);
    }

    @GetMapping("/preUpload/{cid}")
    public R<Course> preUpload(@PathVariable Integer cid) {
        Course course = courseService.getById(cid);
        //model.addAttribute("course", course);
        return R.success(course);
    }

    @PostMapping("/upload")
    public R<String> upload(@RequestBody Course course,MultipartFile file){
        if (!file.isEmpty()){
            transfileBook(course, file);
        }
        courseService.updateById(course);
        return R.success("上传文件成功");
    }

    private void transfileBook(Course course, MultipartFile file) {
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
    }
}
