package com.example.bookmanagementsystem.service;

import com.example.bookmanagementsystem.entity.User;
import com.example.bookmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 添加用户存在性检查方法
    public boolean isUsernameExists(String username, Long userId) {
        boolean exists = userRepository.existsByUsername(username);
        if (userId != null) {
            // 如果是更新操作，检查用户名是否属于当前用户
            User currentUser = userRepository.findById(userId).orElse(null);
            if (currentUser != null && currentUser.getUsername().equals(username)) {
                exists = false; // 用户名属于当前用户，不算重复
            }
        }
        return exists;
    }
}
