package com.xquant.defaultmanager.controller;

import com.xquant.defaultmanager.dto.UserDTO;
import com.xquant.defaultmanager.dto.UserResponseDTO;
import com.xquant.defaultmanager.entity.User;
import com.xquant.defaultmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户相关的CRUD操作和认证管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "获取所有用户", description = "返回系统中所有用户的完整列表")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "通过用户ID查询特定用户的详细信息")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> ResponseEntity.ok(toResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "创建新用户", description = "创建一个新的用户账号，需要提供用户基本信息")
    public UserResponseDTO createUser(@RequestBody UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getEnabled() != null) user.setEnabled(dto.getEnabled());
        User created = userService.createUser(user);
        return toResponse(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "根据用户ID更新现有用户的信息")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        try {
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setPassword(dto.getPassword());
            if (dto.getRole() != null) user.setRole(dto.getRole());
            if (dto.getEnabled() != null) user.setEnabled(dto.getEnabled());
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(toResponse(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据用户ID删除指定的用户账号")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前已认证登录用户的基本信息")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        return userService.getUserByUsername(authentication.getName())
                .map(u -> ResponseEntity.ok(toResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口，检查用户名是否已存在后创建账号")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserDTO dto) {
        if (userService.usernameExists(dto.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(createUser(dto));
    }

    private UserResponseDTO toResponse(User user) {
        UserResponseDTO r = new UserResponseDTO();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setEmail(user.getEmail());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setRole(user.getRole());
        r.setEnabled(user.isEnabled());
        return r;
    }
}
