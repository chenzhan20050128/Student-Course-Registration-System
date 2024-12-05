package com.scrs.controller;/*
 * @date 12/05 14:42
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.pojo.Course;
import com.scrs.pojo.Major;
import com.scrs.pojo.Teacher;
import com.scrs.service.MajorService;
import com.scrs.service.TeacherService;
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
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private MajorService majorService;

    @Value("${file.location}") // 获取配置文件中的文件上传路径
    private String location;

    @RequestMapping("/listTeacher")
    public String listTeacher(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            Model model, Teacher teacher)
    {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        if (teacher.getTname() != null) {
            queryWrapper.like("tname", teacher.getTname());
        }
        List<Teacher> teacherList = teacherService.list(queryWrapper);
        PageInfo pageInfo = new PageInfo(teacherList);
        model.addAttribute("pageInfo", pageInfo);
        return "admin-teacher-list";
    }

    @RequestMapping("/preSaveTeacher")
    public String preSaveTeacher(Model model) {
        List<Major> majorList = majorService.list(null);
        model.addAttribute("majorList", majorList);

        return "admin-teacher-save";
    }

    @RequestMapping("/saveTeacher")
    public String saveTeacher(Teacher teacher, MultipartFile file) {
        if (!file.isEmpty()){
            try{
                transfileTeacher(teacher, file);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        teacherService.save(teacher);
        return "redirect:/teacher/listTeacher";
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

    @RequestMapping("/preUpdateTeacher/{id}")
    public String preUpdateTeacher(@PathVariable Integer id, Model model) {
        List<Major> majorList = majorService.list(null);
        model.addAttribute("majorList", majorList);
        Teacher teacher = teacherService.getById(id);
        model.addAttribute("teacher", teacher);
        return "admin-teacher-update";
    }

    @RequestMapping("/updateTeacher")
    public String updateTeacher(Teacher teacher, MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                transfileTeacher(teacher, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        teacherService.updateById(teacher);
        return "redirect:/teacher/listTeacher";
    }

    @RequestMapping("/deleteTeacher/{id}")
    public String deleteTeacher(@PathVariable Integer id,Model model) {
        boolean b = teacherService.removeById(id);
        if (!b) {
            model.addAttribute("error", "删除失败");
        }

        return "redirect:/teacher/listTeacher";
    }

    @RequestMapping("/deleteBatchTeacher")
    public String deleteBatchTeacher(@RequestBody String ids, Model model) {
        String[] split = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split){
            if (!s.isEmpty()) {
                idList.add(Integer.parseInt(s));
            }
        }
        teacherService.removeByIds(idList);
        boolean b = teacherService.removeByIds(idList);
        if (!b){
            model.addAttribute("error", "批量删除失败");
        }
        return "redirect:/teacher/listTeacher";
    }
}