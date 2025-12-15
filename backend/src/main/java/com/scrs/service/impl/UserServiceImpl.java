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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean registerUser(String userName, String userPwd) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            return false; // 用户名已存在
        }

        User user = new User();
        user.setUsername(userName);
        user.setPassword(DigestUtils.md5DigestAsHex(userPwd.getBytes()));
        return this.save(user);
    }

    @Override
    public Boolean updateUserProfile(User user, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String imagePath = saveImage(file, "D:/scrs/user/");
            user.setImage(imagePath);
        }
        
        // 只有当密码不为空时才加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String passwordAfterMD5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            user.setPassword(passwordAfterMD5);
        }
        return this.updateById(user);
    }

    @Override
    public String saveImage(MultipartFile file, String location) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename != null ? originalFilename.lastIndexOf(".") : -1;
        String suffix = index > 0 ? originalFilename.substring(index) : "";
        String newFileName = System.nanoTime() + suffix;
        
        File dest = new File(location + newFileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        
        try {
            file.transferTo(dest);
            return location + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
