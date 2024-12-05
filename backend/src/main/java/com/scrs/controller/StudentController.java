package com.scrs.controller;/*
                            * @date 12/05 12:12
                            */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.pojo.College;
import com.scrs.pojo.Major;
import com.scrs.pojo.Student;
import com.scrs.service.CollegeService;
import com.scrs.service.MajorService;
import com.scrs.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private MajorService majorService;

    @Value("${file.location}") // 获取配置文件中的文件上传路径
    private String location;

    @RequestMapping("/listStudent")
    public String listStudent(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            Model model, Student student) {
        if (pageNum == null|| pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null|| pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (student.getSname() != null) {
            queryWrapper.like("sname", student.getSname());
        }
        List<Student> students = studentService.list(queryWrapper);
        PageInfo<Student> pageInfo = new PageInfo<>(students);
        model.addAttribute("pageInfo", pageInfo);
        return "admin-student-list";
    }

    @RequestMapping("/preSaveStudent")
    public String preSaveStudent(Model model) {
        List<College> listCollege = collegeService.list(null);
        List<Major> listMajor = majorService.list(null);
        model.addAttribute("listCollege", listCollege);
        model.addAttribute("listMajor", listMajor);
        return "admin-student-save";
    }

    @RequestMapping("/saveStudent")
    public String saveStudent(Student student, MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            transfile(student, file);
        }
        studentService.save(student);

        return "redirect:/student/listStudent";// 重定向到listStudent请求
    }

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

    @RequestMapping("/preUpdateStudent")
    public String preUpdateStudent(@PathVariable Integer id,Model model){
        //需要在前端回显数据
        Student student = studentService.getById(id);
        model.addAttribute("student",student);
        List<College> listCollege = collegeService.list(null);
        List<Major> listMajor = majorService.list(null);
        model.addAttribute("listCollege", listCollege);
        model.addAttribute("listMajor", listMajor);
        return "admin-student-update";
    }

    @RequestMapping("/updateStudent")
    public String updateStudent(Student student,MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            transfile(student, file);
        }
        studentService.updateById(student);
        return "redirect:/student/listStudent";
    }
    @RequestMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable Integer id){
        studentService.removeById(id);
        return "redirect:/student/listStudent";
    }

    @RequestMapping("/deleteBatchStudent")
    public String deleteBatchStudent(@RequestBody String idList,Model model){
        String[] split = idList.split(",");
        List<Integer> list = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                list.add(Integer.parseInt(s));
            }
        }
        boolean isRemoved = studentService.removeById(idList);
        if (!isRemoved) {
            model.addAttribute("error", "Error deleting student.");
        }
        return "redirect:/student/listStudent";
    }
}
