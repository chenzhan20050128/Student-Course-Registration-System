package com.scrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService extends IService<User> {

    Boolean login(String username, String password);

    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);

    /**
     * 注册新用户
     */
    Boolean registerUser(String userName, String userPwd);

    /**
     * 更新用户资料
     */
    Boolean updateUserProfile(User user, MultipartFile file);

    /**
     * 保存上传的图片文件
     */
    String saveImage(MultipartFile file, String location);
}
