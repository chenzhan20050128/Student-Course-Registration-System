package com.scrs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scrs.annotation.ApiCount;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*; // 注意修改导入，使用 RestController
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 处理用户登录请求。根据前端提交的用户名、密码和角色信息对用户进行验证。
     * 验证通过后，会将相关的用户信息存储在会话（session）中。
     * {
     * "username":"admin",
     * "password":"admin123",
     * "role":1
     * }
     * 
     * @param loginRequest 包含用户名、密码和角色的请求对象
     * @return 返回登录结果和角色信息
     */
    @PostMapping("/login")
    @ApiCount("用户登录")
    public R<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        //printSessionInfo();//仅用于调试

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

        if (role == 1) {
            // 管理员登录逻辑
            // 先检查Redis缓存
            String cacheKey = "user:login:" + username;
            String cachedPassword = stringRedisTemplate.opsForValue().get(cacheKey);
            
            Boolean b = false;
            if (cachedPassword != null && cachedPassword.equals(password)) {
                // 缓存命中
                b = true;
                System.out.println("管理员登录 - Redis缓存命中: " + username);
            } else {
                // 缓存未命中，查询数据库
                b = userService.login(username, password);
                if (b != null && b) {
                    // 登录成功，将密码缓存到Redis，过期时间30分钟
                    stringRedisTemplate.opsForValue().set(cacheKey, password, 30, TimeUnit.MINUTES);
                    System.out.println("管理员登录 - 数据库验证成功，已缓存到Redis: " + username);
                }
            }
            
            if (b != null && b) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("username", username);
                User user = userService.getOne(queryWrapper);
                
                // 生成唯一token并存储到Redis
                String token = UUID.randomUUID().toString();
                String tokenKey = "user:token:" + token;
                stringRedisTemplate.opsForValue().set(tokenKey, user.getId().toString(), 2, TimeUnit.HOURS);
                
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", user.getId());
                session.setAttribute("role", 1);
                session.setAttribute("token", token);
                
                result.put("role", 1);
                result.put("id", user.getId());
                result.put("token", token);
                return R.success(result);
            } else {
                return R.error("用户名或密码错误");
            }
        } else if (role == 2) {
            // 老师登录逻辑
            String cacheKey = "teacher:login:" + username;
            String cachedPassword = stringRedisTemplate.opsForValue().get(cacheKey);
            
            Boolean b = false;
            if (cachedPassword != null && cachedPassword.equals(password)) {
                b = true;
                System.out.println("教师登录 - Redis缓存命中: " + username);
            } else {
                b = teacherService.login(username, password);
                if (b != null && b) {
                    stringRedisTemplate.opsForValue().set(cacheKey, password, 30, TimeUnit.MINUTES);
                    System.out.println("教师登录 - 数据库验证成功，已缓存到Redis: " + username);
                }
            }
            
            if (b != null && b) {
                QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("tname", username);
                Teacher teacher = teacherService.getOne(queryWrapper);
                
                String token = UUID.randomUUID().toString();
                String tokenKey = "teacher:token:" + token;
                stringRedisTemplate.opsForValue().set(tokenKey, teacher.getId().toString(), 2, TimeUnit.HOURS);
                
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", teacher.getId());
                session.setAttribute("role", 2);
                session.setAttribute("token", token);
                
                result.put("role", 2);
                result.put("id", teacher.getId());
                result.put("token", token);
                return R.success(result);
            } else {
                return R.error("用户名或密码错误");
            }
        } else if (role == 3) {
            // 学生登录逻辑
            String cacheKey = "student:login:" + username;
            String cachedPassword = stringRedisTemplate.opsForValue().get(cacheKey);
            
            Boolean b = false;
            if (cachedPassword != null && cachedPassword.equals(password)) {
                b = true;
                System.out.println("学生登录 - Redis缓存命中: " + username);
            } else {
                b = studentService.login(username, password);
                if (b != null && b) {
                    stringRedisTemplate.opsForValue().set(cacheKey, password, 30, TimeUnit.MINUTES);
                    System.out.println("学生登录 - 数据库验证成功，已缓存到Redis: " + username);
                }
            }
            
            if (b != null && b) {
                QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sname", username);
                Student student = studentService.getOne(queryWrapper);
                
                String token = UUID.randomUUID().toString();
                String tokenKey = "student:token:" + token;
                stringRedisTemplate.opsForValue().set(tokenKey, student.getId().toString(), 2, TimeUnit.HOURS);
                
                session.setAttribute("currentUsername", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", student.getId());
                session.setAttribute("sex", student.getSex());
                session.setAttribute("age", student.getAge());
                session.setAttribute("major", student.getMajor());
                session.setAttribute("college", student.getCollege());
                session.setAttribute("role", 3);
                session.setAttribute("token", token);
                
                result.put("role", 3);
                result.put("id", student.getId());
                result.put("token", token);
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
     * 1：在数据库中查询完整信息返回
     */
    @GetMapping("/getRoleMessage")
    public R getRoleMessage(HttpSession session,
            @RequestParam(defaultValue = "0", required = false) Integer inDatabase) {
        //printSessionInfo();
        Integer role = (Integer) session.getAttribute("role");
        if (role == null) {
            return R.error("未登录，不能获取session数据");
        }
        if (role == 1) {
            if (inDatabase == 0) {
                User user = new User();
                user.setId((Integer) session.getAttribute("userId"));
                user.setUsername((String) session.getAttribute("currentUsername"));
                return R.success(user);
            } else if (inDatabase == 1) {
                User user = userService.getById((Integer) session.getAttribute("userId"));
                return R.success(user);
            } else {
                return R.error("未给出inDatabase");
            }
        } else if (role == 2) {
            if (inDatabase == 0) {
                Teacher teacher = new Teacher();
                teacher.setId((Integer) session.getAttribute("userId"));
                teacher.setTname((String) session.getAttribute("currentUsername"));
                return R.success(teacher);
            } else if (inDatabase == 1) {
                Teacher teacher = teacherService.getById((Integer) session.getAttribute("userId"));
                return R.success(teacher);
            } else {
                return R.error("未给出inDatabase");
            }
        } else if (role == 3) {
            if (inDatabase == 0) {
                Student student = new Student();
                student.setId((Integer) session.getAttribute("userId"));
                student.setSname((String) session.getAttribute("currentUsername"));
                return R.success(student);
            } else if (inDatabase == 1) {
                Student student = studentService.getById((Integer) session.getAttribute("userId"));
                return R.success(student);
            } else {
                return R.error("未给出inDatabase");
            }
        } else {
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


    @PostMapping("/uploadImage")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("文件为空");
        }
        String fileName = file.getOriginalFilename();
        String filePath = location; // 获取项目根目录并拼接上传目录

        File file1 = new File(location);
        // 检查该目录是否存在
        if (!file1.exists()) {
            // 如果目录不存在，则创建该目录（包括必要的父目录）
            file1.mkdirs();
        }
        // 创建一个File对象，表示具体的文件路径，将文件名添加到目录中
        File file2 = new File(file1, fileName + filePath);
        try {
            // 使用传输文件的方法将内容写入到file2的路径中
            file.transferTo(file2);
        } catch (IOException e) {
            // 如果在文件传输过程中出现IO异常，则打印异常栈跟踪以进行调试
            e.printStackTrace();
        }
        return R.success("Success to upload image!");
    }

    /*
        仅用于调试
     */
    public void printSessionInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpSession session = attributes.getRequest().getSession();
            System.out.println("====== Session Info " + new Date() + " ======");
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                System.out.println(attributeName + " : " + session.getAttribute(attributeName));
            }
            System.out.println("Session ID: " + session.getId());
            System.out.println("Creation Time: " + new Date(session.getCreationTime()));
            System.out.println("Last Accessed Time: " + new Date(session.getLastAccessedTime()));
            System.out.println("Max Inactive Interval: " + session.getMaxInactiveInterval() + " seconds");
            System.out.println("=======================================");
        }
    }
}