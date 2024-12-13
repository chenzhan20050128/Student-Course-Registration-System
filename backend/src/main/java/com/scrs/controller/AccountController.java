package com.scrs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scrs.dto.LoginRequest; // 导入 LoginRequest 类
import com.scrs.pojo.Student;
import com.scrs.pojo.Teacher;
import com.scrs.pojo.User;
import com.scrs.service.StudentService;
import com.scrs.service.TeacherService;
import com.scrs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*; // 注意修改导入，使用 RestController
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 该控制器负责处理账户相关的请求，特别是用户登录操作。
 * 根据用户角色的不同，分别处理管理员、老师和学生的登录，并将相应信息存入会话中。
 */
@RestController // 将 @Controller 改为 @RestController
@CrossOrigin(origins = "http://localhost:5173") // 允许的前端地址
public class AccountController {

    @Value("${file.location}") // 获取配置文件中的文件上传路径
    private String location;

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
     * @param loginRequest 包含用户名、密码和角色的请求对象
     * @param session      HttpSession对象，用于保存用户会话信息
     * @return 返回登录结果和角色信息
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        Integer role = loginRequest.getRole();

        if (username == null || password == null || role == null) {
            result.put("success", false);
            result.put("message", "用户名、密码和角色不能为空");
            return result;
        }

        if (role == 1) {
            // 管理员登录逻辑
            Boolean b = userService.login(username, password);
            if (b != null && b) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("username", username);
                User user = userService.getOne(queryWrapper);
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", user.getId());
                //session.setAttribute("email", user.getEmail());
                //session.setAttribute("image", user.getImage());
                session.setAttribute("role", 1);
                result.put("success", true);
                result.put("role", 1);
                return result;
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return result;
            }
        } else if (role == 2) {
            // 老师登录逻辑
            Boolean b = teacherService.login(username, password);
            if (b != null && b) {
                QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("tname", username); // Teacher 类的字段名称是 tname
                Teacher teacher = teacherService.getOne(queryWrapper);
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", teacher.getId());
                //session.setAttribute("email", teacher.getEmail());
                //session.setAttribute("image", teacher.getTimage());
                session.setAttribute("role", 2);
                result.put("success", true);
                result.put("role", 2);
                return result;
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return result;
            }
        } else if (role == 3) {
            // 学生登录逻辑
            Boolean b = studentService.login(username, password);
            if (b != null && b) {
                QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sname", username); // Student 类的字段名称是 sname
                Student student = studentService.getOne(queryWrapper);
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", student.getId());
                session.setAttribute("sex", student.getSex());
                session.setAttribute("age", student.getAge());
                session.setAttribute("major", student.getMajor());
                session.setAttribute("college", student.getCollege());
                //session.setAttribute("image", student.getSimage());
                session.setAttribute("role", 3);
                result.put("success", true);
                result.put("role", 3);
                return result;
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return result;
            }
        } else {
            // 角色不正确
            result.put("success", false);
            result.put("message", "请选择正确的登录角色");
            return result;
        }
    }


    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    @RequestMapping("/getUserName")
    @ResponseBody
    public User getUserName(HttpSession session) {
        String username = (String) session.getAttribute("currentUsername");
        String image = (String) session.getAttribute("image");
        User user = new User();
        user.setUsername(username);
        user.setImage(image);

        return user;
    }

    /**
     * 个人中心和注册
     */
    @RequestMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Integer role = (Integer) session.getAttribute("role");
        String currentUsername = (String) session.getAttribute("currentUsername");
        String password = (String) session.getAttribute("password");
        Integer userId = (Integer) session.getAttribute("userId");

        if (role == 1) {
            User user = userService.getById(userId);
            user.setPassword(password);
            model.addAttribute("user", user);
            return "admin-profile";
        } else if (role == 2) {
            Teacher teacher = teacherService.getById(userId);
            teacher.setPassword(password);
            model.addAttribute("user", teacher);
            return "teacher-profile";
        } else if (role == 3) {
            Student student = studentService.getById(userId);
            student.setPassword(password);
            model.addAttribute("user", student);
            return "student-profile";
        } else {
            return "index";
        }
    }

    @RequestMapping("/updateAdminProfile")
    public String updateAdminProfile(User user, MultipartFile file) {
        if (!file.isEmpty()) {
            transfileAdmin(user,file);
        }
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(passwordAfterMD5);
        boolean b = userService.updateById(user);
        return "redirect:/profile";
    }

    private void transfileAdmin(User user, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf("/");
        String suffix = originalFilename.substring(index);
        String prefix = System.nanoTime() + "";
        String path = prefix + suffix;
        File file1 = new File(location);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file2 = new File(file1,path);
        try {
            file.transferTo(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setImage(path);
    }

    @RequestMapping("/updateTeacherProfile")
    public String updateTeacherProfile(Teacher teacher, MultipartFile file) {
        if (!file.isEmpty()) {
            transfileTeacher(teacher, file);
        }
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(teacher.getPassword().getBytes());
        teacher.setPassword(passwordAfterMD5);
        teacherService.updateById(teacher);
        return "redirect:/profile";
    }

    private void transfileTeacher(Teacher teacher, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int i = 0;
        if (originalFilename != null) {
            i = originalFilename.lastIndexOf(".");
        }
        String suffix = originalFilename.substring(i);
        String prefix = System.nanoTime() + "";
        String filename = prefix + suffix;

        File file1 = new File(location);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file2 = new File(file1, filename);
        try {
            file.transferTo(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        teacher.setTimage(filename);
    }

    @RequestMapping("/updateStudentProfile")
    public String updateStudentProfile(Student student, MultipartFile file) {
        if (!file.isEmpty()) {
            transfileStudent(student, file);
        }
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(student.getPassword().getBytes());
        student.setPassword(passwordAfterMD5);
        studentService.updateById(student);
        return "redirect:/profile";
    }

    private void transfileStudent(Student student, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int i = 0;
        if (originalFilename != null) {
            i = originalFilename.lastIndexOf(".");
        }
        String suffix = originalFilename.substring(i);
        String prefix = System.nanoTime() + "";
        String filename = prefix + suffix;

        File file1 = new File(location);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file2 = new File(file1, filename);
        try {
            file.transferTo(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 跳转到注册页面
     */
    @RequestMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    /*
     * 跳转到登录页面
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "index";
    }

    @RequestMapping("/register")
    public String register(Integer role,String userName,String userPwd,String confirmPwd,Model model){
        if (role == null) {
            model.addAttribute("msg", "请选择注册角色");
            return "register";
        }
        if (!userPwd.equals(confirmPwd)){
            model.addAttribute("msg","两次密码不一致");
            return "register";
        }

        if (role == 1){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username",userName);
            User one = userService.getOne(queryWrapper);
            if (one != null){
                model.addAttribute("msg","用户名已存在");
                return "register";
            }
            User user = new User();
            user.setUsername(userName);
            user.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
            boolean save = userService.save(user);
            if (!save){
                model.addAttribute("msg","注册失败");
            }
            return "index";
        }
        else if (role == 2){
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tname",userName);
            Teacher one = teacherService.getOne(queryWrapper);
            if (one != null){
                model.addAttribute("msg","用户名已存在");
                return "register";
            }
            Teacher teacher = new Teacher();
            teacher.setTname(userName);
            teacher.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
            boolean save = teacherService.save(teacher);
            if (!save){
                model.addAttribute("msg","注册失败");
            }
            return "index";
        }
        else if (role == 3){
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sname",userName);
            Student one = studentService.getOne(queryWrapper);
            if (one != null){
                model.addAttribute("msg","用户名已存在");
                return "register";
            }
            Student student = new Student();
            student.setSname(userName);
            student.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
            boolean save = studentService.save(student);
            if (!save){
                model.addAttribute("msg","注册失败");
            }
            return "index";
        }
        else {
            model.addAttribute("msg","请选择注册角色");
            return "register";
        }

    }
}