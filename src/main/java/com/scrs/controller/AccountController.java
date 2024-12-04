package com.scrs.controller;/*
                            * @date 12/04 20:28
                            */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scrs.pojo.Student;
import com.scrs.pojo.Teacher;
import com.scrs.pojo.User;
import com.scrs.service.StudentService;
import com.scrs.service.TeacherService;
import com.scrs.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;

    @RequestMapping("/login")
    public String login(String username, String password, Integer role, Model model, HttpSession session) {
        if (role == null) {
            model.addAttribute("msg", "请选择登录角色");
            return "index";
        } else if (role == 1) {
            // 管理员
            Boolean b = userService.login(username, password);
            if (b != null && b) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("username", username);
                User user = userService.getOne(queryWrapper);
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", user.getId());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("image", user.getImage());
                session.setAttribute("role", 1);
                return "admin-home";
            } else {
                model.addAttribute("msg", "用户名或密码错误");
                return "index";
            }
        }
        else if (role == 2) {
            // 老师
            Boolean b = teacherService.login(username, password);
            if (b != null && b) {
                QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("name", username);//teacher类的字段名称是name
                Teacher teacher = teacherService.getOne(queryWrapper);
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", teacher.getId());
                session.setAttribute("email", teacher.getEmail());
                session.setAttribute("image", teacher.getTimage());
                session.setAttribute("role", 2);
                return "teacher-home";
            } else {
                model.addAttribute("msg", "用户名或密码错误");
                return "index";
            }
        }
        else if (role == 3) {
            // 学生
            Boolean b = studentService.login(username, password);
            if (b != null && b) {
                QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sname", username);//Student类的字段名称是sname

                Student student = studentService.getOne(queryWrapper);
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", student.getId());
                session.setAttribute("sex", student.getSex());
                session.setAttribute("age", student.getAge());
                session.setAttribute("major", student.getMajor());
                session.setAttribute("college", student.getCollege());
                session.setAttribute("image", student.getSimage());
                session.setAttribute("role", 3);
                return "student-home";
            } else {
                model.addAttribute("msg", "用户名或密码错误");
                return "index";
            }
        }
        else {
            model.addAttribute("msg", "请选择登录角色");
            return "index";
        }

    }
}
