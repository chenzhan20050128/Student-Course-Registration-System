package com.scrs.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.Student;

public interface StudentService extends IService<Student> {
    Boolean login(String username, String password);
}
