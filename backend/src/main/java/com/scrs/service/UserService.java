package com.scrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.User;

public interface UserService extends IService<User>{

    Boolean login(String username, String password);
}
