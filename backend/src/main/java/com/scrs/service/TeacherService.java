package com.scrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.Teacher;

import java.util.Map;

public interface TeacherService extends IService<Teacher> {
    Boolean login(String username, String password);

    /**
     * 教师登录（带Redis缓存）
     */
    Map<String, Object> loginWithCache(String username, String password);
}
