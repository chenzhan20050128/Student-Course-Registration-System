package com.scrs.service.impl;/*
                              * @date 12/04 21:12
                              */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.TeacherMapper;
import com.scrs.pojo.Teacher;
import com.scrs.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean login(String username, String password) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tname", username);
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
        queryWrapper.eq("password", passwordAfterMD5);
        Teacher teacher = teacherMapper.selectOne(queryWrapper);
        if (teacher == null) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> loginWithCache(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        // 先检查Redis缓存
        String cacheKey = "teacher:login:" + username;
        String cachedPassword = stringRedisTemplate.opsForValue().get(cacheKey);

        Boolean loginSuccess = false;
        if (cachedPassword != null && cachedPassword.equals(password)) {
            loginSuccess = true;
            System.out.println("教师登录 - Redis缓存命中: " + username);
        } else {
            loginSuccess = this.login(username, password);
            if (loginSuccess) {
                stringRedisTemplate.opsForValue().set(cacheKey, password, 30, TimeUnit.MINUTES);
                System.out.println("教师登录 - 数据库验证成功，已缓存到Redis: " + username);
            }
        }

        if (loginSuccess) {
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tname", username);
            Teacher teacher = teacherMapper.selectOne(queryWrapper);

            String token = UUID.randomUUID().toString();
            String tokenKey = "teacher:token:" + token;
            stringRedisTemplate.opsForValue().set(tokenKey, teacher.getId().toString(), 2, TimeUnit.HOURS);

            result.put("success", true);
            result.put("teacher", teacher);
            result.put("token", token);
        } else {
            result.put("success", false);
        }

        return result;
    }
}
