package com.scrs.service.impl;

import com.scrs.pojo.Student;
import com.scrs.pojo.Teacher;
import com.scrs.pojo.User;
import com.scrs.service.AccountService;
import com.scrs.service.StudentService;
import com.scrs.service.TeacherService;
import com.scrs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 账户服务实现类
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> login(String username, String password, Integer role, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        if (role == 1) {
            // 管理员登录
            Map<String, Object> loginResult = userService.loginWithCache(username, password);
            if ((Boolean) loginResult.get("success")) {
                User user = (User) loginResult.get("user");
                String token = (String) loginResult.get("token");

                // 设置Session
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", user.getId());
                session.setAttribute("role", 1);
                session.setAttribute("token", token);

                // 返回结果
                result.put("success", true);
                result.put("role", 1);
                result.put("id", user.getId());
                result.put("token", token);
                return result;
            }
        } else if (role == 2) {
            // 教师登录
            Map<String, Object> loginResult = teacherService.loginWithCache(username, password);
            if ((Boolean) loginResult.get("success")) {
                Teacher teacher = (Teacher) loginResult.get("teacher");
                String token = (String) loginResult.get("token");

                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", teacher.getId());
                session.setAttribute("role", 2);
                session.setAttribute("token", token);

                result.put("success", true);
                result.put("role", 2);
                result.put("id", teacher.getId());
                result.put("token", token);
                return result;
            }
        } else if (role == 3) {
            // 学生登录
            Map<String, Object> loginResult = studentService.loginWithCache(username, password);
            if ((Boolean) loginResult.get("success")) {
                Student student = (Student) loginResult.get("student");
                String token = (String) loginResult.get("token");

                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", student.getId());
                session.setAttribute("sex", student.getSex());
                session.setAttribute("age", student.getAge());
                session.setAttribute("major", student.getMajor());
                session.setAttribute("college", student.getCollege());
                session.setAttribute("role", 3);
                session.setAttribute("token", token);

                result.put("success", true);
                result.put("role", 3);
                result.put("id", student.getId());
                result.put("token", token);
                return result;
            }
        } else {
            result.put("success", false);
            result.put("message", "角色不正确");
            return result;
        }

        result.put("success", false);
        result.put("message", "用户名或密码错误");
        return result;
    }

    @Override
    public boolean logout(HttpSession session) {
        try {
            // 从session中获取token并删除Redis中的缓存
            String token = (String) session.getAttribute("token");
            Integer role = (Integer) session.getAttribute("role");

            if (token != null && role != null) {
                String tokenKey = "";
                if (role == 1) {
                    tokenKey = "user:token:" + token;
                } else if (role == 2) {
                    tokenKey = "teacher:token:" + token;
                } else if (role == 3) {
                    tokenKey = "student:token:" + token;
                }
                stringRedisTemplate.delete(tokenKey);
                System.out.println("退出登录 - 已删除Redis token: " + tokenKey);
            }

            session.invalidate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
