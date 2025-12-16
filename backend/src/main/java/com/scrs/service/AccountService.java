package com.scrs.service;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 账户服务接口
 */
public interface AccountService {
    
    /**
     * 统一登录处理
     * @param username 用户名
     * @param password 密码
     * @param role 角色（1-管理员，2-教师，3-学生）
     * @param session HTTP会话
     * @return 登录结果
     */
    Map<String, Object> login(String username, String password, Integer role, HttpSession session);
    
    /**
     * 退出登录
     * @param session HTTP会话
     * @return 操作结果
     */
    boolean logout(HttpSession session);
}
