package com.scrs.service.impl;/*
                              * @date 12/04 20:27
                              */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.UserMapper;
import com.scrs.pojo.User;
import com.scrs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        String passwordAfterMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
        queryWrapper.eq("password", passwordAfterMD5);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return false;
        }
        return true;
    }

}
