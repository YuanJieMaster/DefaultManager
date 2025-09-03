package com.xquant.defaultmanager.service;

import com.xquant.defaultmanager.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 * 定义用户管理相关的业务操作方法
 */
public interface UserService {
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息（Optional包装）
     */
    Optional<User> getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息（Optional包装）
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * 创建新用户
     * @param user 用户信息
     * @return 创建成功的用户
     */
    User createUser(User user);
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDetails 更新的用户信息
     * @return 更新后的用户
     */
    User updateUser(Long id, User userDetails);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return true表示存在，false表示不存在
     */
    boolean usernameExists(String username);
    
    /**
     * 检查邮箱是否已存在
     * @param email 邮箱地址
     * @return true表示存在，false表示不存在
     */
    boolean emailExists(String email);
}
