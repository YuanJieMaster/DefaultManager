package com.xquant.defaultmanager.service.impl;

import com.xquant.defaultmanager.entity.User;
import com.xquant.defaultmanager.repository.UserRepository;
import com.xquant.defaultmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 * 实现用户管理相关的业务操作
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            // 更新基本信息
            user.setEmail(userDetails.getEmail());
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            
            // 如果提供了新密码，则更新密码
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
