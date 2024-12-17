package com.scrs.controller;

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

/**
 * 该控制器负责处理关于学生管理的请求，包括：
 * - 获取学生列表
 * - 添加新学生
 * - 更新学生信息
 * - 删除学生记录
 * - 上传与学生相关的文件
 *
 * 前端可以通过这些接口与后端通信，可以使用Vue.js开发SPA通过AJAX请求与这些接口互动。
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentCourseService studentCourseService;

    @Autowired
    private TeacherCourseService teacherCourseService;

    @Autowired
    private TeacherService teacherService;

    @Value("${file.location}") // 获取配置文件中的文件上传路径
    private String location;

    /**
     * 列出学生信息，并支持分页。用户可以通过学生姓名过滤列表，并获得分页结果。
     *
     * @param pageNum  要检索的页码，默认为1。
     * @param pageSize 每页的学生数量，默认为6。
     * @return 将要渲染的视图名称，显示学生列表。
     */
    @GetMapping("/listStudent")
    public R<PageInfo> listStudent(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                   @RequestParam(required = false) String sname) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);

        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        if (sname != null && !sname.isEmpty()) {
            queryWrapper.like(Student::getSname, sname);
        }
        List<Student> students = studentService.list(queryWrapper);
        PageInfo<Student> pageInfo = new PageInfo<>(students);

        //System.out.println("PageInfo: " + pageInfo.toString());// 测试

        //model.addAttribute("pageInfo", pageInfo);
        return R.success(pageInfo);
    }

    /**
     * 准备表单以添加新学生信息。
     *
     * @param model 提供表单所需数据的模型，如学院和专业列表。
     * @return 渲染学生保存表单的视图名称。
     */
    @GetMapping("/preSaveStudent")
    public R<HashMap<String,Object>> preSaveStudent(Model model) {
        List<College> listCollege = collegeService.list(null);
        List<Major> listMajor = majorService.list(null);
        //model.addAttribute("listCollege", listCollege);
        //model.addAttribute("listMajor", listMajor);
        HashMap<String,Object> map = new HashMap<>();
        map.put("listCollege",listCollege);
        map.put("listMajor",listMajor);
        // System.out.println("--preSaveStudent------------------cz-------------------");
        // System.out.println("listCollege: " + listCollege.toString());
        // System.out.println("listMajor: " + listMajor.toString());
        // System.out.println("--------------------cz-------------------");
        return R.success(map);
    }

    /**
     * 保存新学生信息。
     *
     * @param student 从表单获取的学生信息。
     * @param file    学生的图片文件。
     * @return 成功保存后重定向到学生列表。
     */
    @PostMapping("/saveStudent")
    public R<String> saveStudent(@RequestBody Student student,
    @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        //TODO: 保存学生的图像文件
        // if (file != null && !file.isEmpty()) {
        //     transfile(student, file);
        // }
        String defaultPassword = "123456";
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(defaultPassword.getBytes());
        student.setPassword(passwordAfterMD5);
        studentService.save(student);

        return R.success("Success to save student!");
    }

    /**
     * 辅助方法用于处理学生的文件上传。
     * 文件存储在特定目录中，生成的文件名将设置在学生对象中。
     *
     * @param student 用于设置图像文件名的学生实体。
     * @param file    上传的文件。
     */
    private void transfile(Student student, MultipartFile file) throws IOException {
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
        // 将文件名设置在student对象的simage属性中，以便保存文件名信息
        student.setSimage(filename);
    }

    /**
     * 准备预填充现有数据的学生更新表单。
     *
     * @param id    要更新的学生ID。
     * @return 用于渲染学生更新表单的视图名称。
     */
    @RequestMapping("/preUpdateStudent/{id}")
    public R<HashMap<String,Object>> preUpdateStudent(@PathVariable Integer id) {
        // 需要在前端回显数据
        Student student = studentService.getById(id);
        //model.addAttribute("student", student);
        List<College> listCollege = collegeService.list(null);
        List<Major> listMajor = majorService.list(null);
        //model.addAttribute("listCollege", listCollege);
        //model.addAttribute("listMajor", listMajor);
        HashMap<String,Object> map = new HashMap<>();
        map.put("student",student);
        map.put("listCollege",listCollege);
        map.put("listMajor",listMajor);
        return R.success(map);
    }

    /**
     * 更新已存在的学生信息。
     *
     * @param student 要更新的学生信息。
     * @param file    学生的新图像文件（如有）。
     * @return 成功更新后重定向到学生列表。
     */
    @PostMapping("/updateStudent")
    public R<String> updateStudent(@RequestBody Student student, MultipartFile file) throws IOException {
//        if (!file.isEmpty()) {
//            System.out.println("11111");
//            transfile(student, file);
//        }
        studentService.updateById(student);
        return R.success("Success to update student!");
    }

    /**
     * 根据学生ID删除学生信息。
     *
     * @param id 要删除的学生ID。
     * @return 成功删除后重定向到学生列表。
     */
    @GetMapping("/deleteStudent/{id}")
    public R<String> deleteStudent(@PathVariable Integer id) {
        boolean b = studentService.removeById(id);
        if (!b) {
            return R.error("Error deleting student.");
        }
        return R.success("Success to delete student!");
    }

    /**
     * 在单次请求中删除多个学生。
     *
     * @param ids 要删除的学生ID的JSON数组
     * @return 重定向到学生列表。
     */
    @PostMapping("/deleteBatchStudent")
    public R<String> deleteBatchStudent(@RequestBody String ids) {
        //TODO：传入的字符串格式是 "{ids: "1,2,3,4,5"}"，需要处理一下
        String[] split = ids.substring(8,ids.length() - 2).split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                idList.add(Integer.parseInt(s));
            }
        }

        boolean b = studentService.removeByIds(idList);
        if (!b) {
            //model.addAttribute("error", "Error deleting student.");
            return R.error("Error deleting student.");
        }
        return R.success("Success to delete student!");
    }


    /*
    现在是学生选课功能，在学生的界面 add by cz at 12.7 15:26
     */

    @GetMapping ("/listCourseByMajorName")
    public R<PageInfo> listCourse(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                  @RequestParam(required = false) String majorName) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }

        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (majorName != null) {
            courseLambdaQueryWrapper.eq(Course::getMajor, majorName);
        }
        List<Course> courses = courseService.list(courseLambdaQueryWrapper);
        PageInfo<Course> pageInfo = new PageInfo<>(courses);
        return R.success(pageInfo);
    }

    /**
     * 选课记录
     */
    @GetMapping("/listMyCourse")
    public String listMyCourse(@RequestParam(required = false) String cname, HttpSession session,Model model){
        Integer userId = (Integer) session.getAttribute("userId");
        List<StudentCourse> studentCourses = studentCourseService.listMyCourse(userId, cname);
        //简化操作，与教师无关
        PageInfo<StudentCourse> pageInfo = new PageInfo<>(studentCourses);
        model.addAttribute("pageInfo", pageInfo);
        return "student-my-course";
    }

    /**
     * 新增改动（12.16 17:02）去掉了session 直接传参
     */
    @GetMapping("/selectCourse")
    public R<String> selectCourse(@RequestParam Integer cid,@RequestParam Integer sid){
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid", sid);
        queryWrapper.eq("cid", cid);
        StudentCourse one = studentCourseService.getOne(queryWrapper);
        if (one != null) {
            return R.error("该学生已经选过该课程");
        }
        Course course = courseService.getById(cid);
        if (course.getNum() >= course.getStock()){
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

    /**
     * 学生退选
     */
    @GetMapping("/deleteMyCourse")
    public R<String> deleteMyCourse(@RequestParam Integer cid, @RequestParam Integer sid){
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sid", sid);
        queryWrapper.eq("cid", cid);
        StudentCourse one = studentCourseService.getOne(queryWrapper);
        if (one == null) {
            return R.error("该学生没有选过该课程");
        }
        one.setStatus(0);//表示退选
        Course course = courseService.getById(cid);
        course.setNum(course.getNum() - 1);
        courseService.updateById(course);
        studentCourseService.updateById(one);
        return R.success("退选成功");
    }
}