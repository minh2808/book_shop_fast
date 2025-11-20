package com.example.BookShopFast.service;

import com.example.BookShopFast.entity.Role;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.repository.RoleRepository;
import com.example.BookShopFast.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    public User register(User user) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }
        
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }
        
        // Gán role mặc định là ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("Role ROLE_USER không tồn tại");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        
        user.setEnabled(true);
        
        return userRepository.save(user);
    }
    
    public User login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Username hoặc password không đúng");
        }
        
        User user = userOptional.get();
        
        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }
        
        // So sánh password trực tiếp (không mã hóa)
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Username hoặc password không đúng");
        }
        
        return user;
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

