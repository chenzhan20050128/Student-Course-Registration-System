package com.scrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface StudentService extends IService<Student> {
    Boolean login(String username, String password);

    /**
     * 学生登录（带Redis缓存）
     */
    Map<String, Object> loginWithCache(String username, String password);

    Student getStudentByUsername(String username);

    Boolean registerStudent(String userName, String userPwd);

    Boolean updateStudentProfile(Student student, MultipartFile file);

    String saveImage(MultipartFile file, String location);
}
