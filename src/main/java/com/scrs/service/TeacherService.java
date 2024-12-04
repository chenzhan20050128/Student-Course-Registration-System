package com.scrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.Teacher;

public interface TeacherService extends IService<Teacher> {
    Boolean login(String username, String password);
}
