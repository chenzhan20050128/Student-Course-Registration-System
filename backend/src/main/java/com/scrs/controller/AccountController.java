package com.scrs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scrs.common.R;
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
     *{
     *         "username":"admin",
     *         "password":"admin123",
     *         "role":1
     *     }
     * @param loginRequest 包含用户名、密码和角色的请求对象
     * @return 返回登录结果和角色信息
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        // 先从session中获取用户名、密码和角色信息
        result.put("getFromLoginRequest", "false");
        String username = (String) session.getAttribute("currentUsername");
        String password = (String) session.getAttribute("password");
        Integer role = (Integer) session.getAttribute("role");

        if (username == null || password == null || role == null) {
            result.put("getFromLoginRequest", "true");

            username = loginRequest.getUsername();
            password = loginRequest.getPassword();
            role = loginRequest.getRole();
        }
        //session.setAttribute("role", role);
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
                // session.setAttribute("email", user.getEmail());
                // session.setAttribute("image", user.getImage());
                session.setAttribute("role", 1);
                result.put("role", 1);
                result.put("id", user.getId());
                return R.success(result);
            } else {
                return R.error("用户名或密码错误");
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
                // session.setAttribute("email", teacher.getEmail());
                // session.setAttribute("image", teacher.getTimage());
                session.setAttribute("role", 2);
                result.put("role", 2);
                result.put("id", teacher.getId());
                return R.success(result);
            } else {
                return R.error("用户名或密码错误");
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
                // session.setAttribute("image", student.getSimage());
                session.setAttribute("role", 3);
                result.put("role", 3);
                result.put("id", student.getId());
                return R.success(result);
            } else {
                return R.error("用户名或密码错误");
            }
        } else {
            // 角色不正确
            return R.error("角色不正确");
        }
    }

    @GetMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.invalidate();
        return R.success("退出登录成功");
    }

    @GetMapping("/getUserName")
    public R<User> getUserName(HttpSession session) {
        String username = (String) session.getAttribute("currentUsername");
        // String image = (String) session.getAttribute("image");
        User user = new User();
        user.setUsername(username);
        // user.setImage(image);
        return R.success(user);
    }

    /*
     * 从session获取角色信息，包括管理员，老师，学生
     * inDatabase：0：只在session中查询id，name，role等基本信息返回
     *           1：在数据库中查询完整信息返回
     */
    @GetMapping("/getRoleMessage")
    public R<Object> getRoleMessage(HttpSession session,@RequestParam(defaultValue = "0",required = false) Integer inDatabase){
        Integer role = (Integer) session.getAttribute("role");
        System.out.println("NAme " + session.getAttribute("currentUsername"));
        System.out.println("role:"+role);
        System.out.println("inDatabase:"+inDatabase);
        if (role == null){
            return R.error("未登录，不能获取session数据");
        }
        if (role == 1){
            if (inDatabase == 0){
                User user = new User();
                user.setId((Integer) session.getAttribute("userId"));
                user.setUsername((String) session.getAttribute("currentUsername"));
                return R.success(user);
            }
            else if (inDatabase == 1){
                User user = userService.getById((Integer) session.getAttribute("userId"));
                return R.success(user);
            }
            else {
                return R.error("未给出inDatabase");
            }
        }
        else if (role == 2){
            if (inDatabase == 0){
                Teacher teacher = new Teacher();
                teacher.setId((Integer) session.getAttribute("userId"));
                teacher.setTname((String) session.getAttribute("currentUsername"));
                return R.success(teacher);
            }
            else if (inDatabase == 1){
                Teacher teacher = teacherService.getById((Integer) session.getAttribute("userId"));
                return R.success(teacher);
            }
            else {
                return R.error("未给出inDatabase");
            }
        }
        else if (role == 3){
            if (inDatabase == 0){
                Student student = new Student();
                student.setId((Integer) session.getAttribute("userId"));
                student.setSname((String) session.getAttribute("currentUsername"));
                return R.success(student);
            }
            else if (inDatabase == 1){
                Student student = studentService.getById((Integer) session.getAttribute("userId"));
                return R.success(student);
            }
            else {
                return R.error("未给出inDatabase");
            }
        }
        else {
            return R.error("未知角色");
        }
    }


    

    /**
     * 个人中心和注册
     * 前端注意，这里的user不确定他的类型是管理员老师还是学生，需要判断！
     */
    @GetMapping("/profile")
    public R profile(HttpSession session) {
        Integer role = (Integer) session.getAttribute("role");
        String currentUsername = (String) session.getAttribute("currentUsername");
        String password = (String) session.getAttribute("password");
        Integer userId = (Integer) session.getAttribute("userId");

        if (role == 1) {
            User user = userService.getById(userId);
            user.setPassword(password);
            return R.success(user);
        } else if (role == 2) {
            Teacher teacher = teacherService.getById(userId);
            teacher.setPassword(password);
            return R.success(teacher);
        } else if (role == 3) {
            Student student = studentService.getById(userId);
            student.setPassword(password);
            return R.success(student);
        } else {
            return R.error("未登录");
        }
    }

    @PostMapping("/updateAdminProfile")
    public R<String> updateAdminProfile(@RequestBody User user, MultipartFile file) {
        if (!file.isEmpty()) {
            transfileAdmin(user, file);
        }
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(passwordAfterMD5);
        boolean b = userService.updateById(user);
        if (!b) {
            return R.error("更新失败");
        }

        return R.success("更新成功");
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
        File file2 = new File(file1, path);
        try {
            file.transferTo(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setImage(path);
    }

    @PostMapping("/updateTeacherProfile")
    public R<String> updateTeacherProfile(@RequestBody Teacher teacher, MultipartFile file) {
        if (!file.isEmpty()) {
            transfileTeacher(teacher, file);
        }
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(teacher.getPassword().getBytes());
        teacher.setPassword(passwordAfterMD5);
        boolean b = teacherService.updateById(teacher);
        if (!b) {
            return R.error("更新失败");
        }
        return R.success("更新成功");
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

    @PostMapping("/updateStudentProfile")
    public R<String> updateStudentProfile(@RequestBody Student student, MultipartFile file) {
        if (!file.isEmpty()) {
            transfileStudent(student, file);
        }
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(student.getPassword().getBytes());
        student.setPassword(passwordAfterMD5);
        boolean b = studentService.updateById(student);
        if (!b) {
            return R.error("更新失败");
        }
        return R.success("更新成功");
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
     * 这个方法不需要了，前端直接跳转到注册页面
     */
    @GetMapping("/toRegister")
    public String toRegister() {

        return "register";
    }

    /*
     * 跳转到登录页面
     * 这个方法不需要了，前端直接跳转到登录页面
     */
    @GetMapping("/toLogin")
    public String toLogin() {
        return "index";
    }

    /*
     * 注册
     */
    @GetMapping("/register")
    public R<String> register(@RequestParam Integer role, @RequestParam String userName, @RequestParam String userPwd,
            @RequestParam String confirmPwd) {
        if (role == null) {
            return R.error("请选择注册角色");
        }
        if (!userPwd.equals(confirmPwd)) {
            return R.error("两次密码不一致");
        }

        if (role == 1) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", userName);
            User one = userService.getOne(queryWrapper);
            if (one != null) {
                return R.error("用户名已存在");
            }
            User user = new User();
            user.setUsername(userName);
            user.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
            boolean save = userService.save(user);
            if (!save) {
                return R.error("注册失败");
            }
            return R.success("注册成功");
        } else if (role == 2) {
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tname", userName);
            Teacher one = teacherService.getOne(queryWrapper);
            if (one != null) {
                return R.error("用户名已存在");
            }
            Teacher teacher = new Teacher();
            teacher.setTname(userName);
            teacher.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
            boolean save = teacherService.save(teacher);
            if (!save) {
                return R.error("注册失败");
            }
            return R.success("注册成功");
        } else if (role == 3) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sname", userName);
            Student one = studentService.getOne(queryWrapper);
            if (one != null) {
                return R.error("用户名已存在");
            }
            Student student = new Student();
            student.setSname(userName);
            student.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
            boolean save = studentService.save(student);
            if (!save) {
                return R.error("注册失败");
            }
            return R.success("注册成功");
        } else {
            return R.error("请选择正确的注册角色");
        }

    }
}