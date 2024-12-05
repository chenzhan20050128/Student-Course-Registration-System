package com.scrs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scrs.pojo.Student;
import com.scrs.pojo.Teacher;
import com.scrs.pojo.User;
import com.scrs.service.StudentService;
import com.scrs.service.TeacherService;
import com.scrs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * 该控制器负责处理账户相关的请求，特别是用户登录操作。
 * 根据用户角色的不同，分别处理管理员、老师和学生的登录，并将相应信息存入会话中。
 */
@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    /**
     * 处理用户登录请求。根据前端提交的用户名、密码和角色信息对用户进行验证。
     * 验证通过后，会将相关的用户信息存储在会话（session）中。
     *
     * @param username 用户名，用于登录
     * @param password 密码，用于登录
     * @param role     用户角色（1: 管理员, 2: 老师, 3: 学生）
     * @param model    用于传递信息到前端视图的模型
     * @param session  HttpSession对象，用于保存用户会话信息
     * @return 根据不同的角色返回不同的首页视图或失败时返回登录页
     */
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
        } else if (role == 2) {
            // 老师
            Boolean b = teacherService.login(username, password);
            if (b != null && b) {
                QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("tname", username); // teacher类的字段名称是tname
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
        } else if (role == 3) {
            // 学生
            Boolean b = studentService.login(username, password);
            if (b != null && b) {
                QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sname", username); // Student类的字段名称是sname

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
        } else {
            model.addAttribute("msg", "请选择登录角色");
            return "index";
        }
    }
}