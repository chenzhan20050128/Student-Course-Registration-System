package com.scrs.service.impl;/*
 * @date 12/04 21:24
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.StudentMapper;
import com.scrs.pojo.Student;
import com.scrs.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Boolean login(String username, String password) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
        queryWrapper.eq("password", passwordAfterMD5);
        Student student = studentMapper.selectOne(queryWrapper);
        if (student == null) {
            return false;
        }
        return true;
    }
}
