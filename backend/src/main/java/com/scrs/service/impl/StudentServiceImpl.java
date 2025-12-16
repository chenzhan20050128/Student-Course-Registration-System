package com.scrs.service.impl;/*
                              * @date 12/04 21:24
                              */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.StudentMapper;
import com.scrs.pojo.Student;
import com.scrs.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${file.location:uploads}")
    private String location;

    @Override
    public Boolean login(String username, String password) {
        // log.info("username: {}, password: {}", username, password);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sname", username);
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
        // log.info("passwordAfterMD5: {}", passwordAfterMD5);
        queryWrapper.eq("password", passwordAfterMD5);
        Student student = studentMapper.selectOne(queryWrapper);
        if (student == null) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> loginWithCache(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        // 先检查Redis缓存
        String cacheKey = "student:login:" + username;
        String cachedPassword = stringRedisTemplate.opsForValue().get(cacheKey);

        Boolean loginSuccess = false;
        if (cachedPassword != null && cachedPassword.equals(password)) {
            loginSuccess = true;
            System.out.println("学生登录 - Redis缓存命中: " + username);
        } else {
            loginSuccess = this.login(username, password);
            if (loginSuccess) {
                stringRedisTemplate.opsForValue().set(cacheKey, password, 30, TimeUnit.MINUTES);
                System.out.println("学生登录 - 数据库验证成功，已缓存到Redis: " + username);
            }
        }

        if (loginSuccess) {
            Student student = getStudentByUsername(username);

            String token = UUID.randomUUID().toString();
            String tokenKey = "student:token:" + token;
            stringRedisTemplate.opsForValue().set(tokenKey, student.getId().toString(), 2, TimeUnit.HOURS);

            result.put("success", true);
            result.put("student", student);
            result.put("token", token);
        } else {
            result.put("success", false);
        }

        return result;
    }

    @Override
    public Student getStudentByUsername(String username) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sname", username);
        return studentMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean registerStudent(String userName, String userPwd) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sname", userName);
        Student existingStudent = studentMapper.selectOne(queryWrapper);
        if (existingStudent != null) {
            return false; // 用户名已存在
        }

        Student student = new Student();
        student.setSname(userName);
        student.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
        return this.save(student);
    }

    @Override
    public Boolean updateStudentProfile(Student student, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String imagePath = saveImage(file, location);
            student.setSimage(imagePath);
        }
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(student.getPassword().getBytes());
        student.setPassword(passwordAfterMD5);
        return this.updateById(student);
    }

    @Override
    public String saveImage(MultipartFile file, String location) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename != null ? originalFilename.lastIndexOf(".") : -1;
        String suffix = index > 0 ? originalFilename.substring(index) : "";
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
            return null;
        }
        return filename;
    }
}
