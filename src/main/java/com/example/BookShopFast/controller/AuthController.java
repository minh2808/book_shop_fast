package com.example.BookShopFast.controller;

import com.example.BookShopFast.entity.Role;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        // Nếu đã đăng nhập thì redirect về trang chủ
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        try {
            User user = userService.login(username, password);
            
            // Lưu user vào session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userRoles", user.getRoles());
            
            // Thông báo chào mừng
            String fullName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                             (user.getLastName() != null ? " " + user.getLastName() : "").trim();
            if (fullName.isEmpty()) {
                fullName = user.getUsername();
            }
            redirectAttributes.addFlashAttribute("success", "Chào mừng " + fullName + "! Đăng nhập thành công.");
            
            // Kiểm tra role để redirect
            Set<Role> roles = user.getRoles();
            boolean isAdmin = roles.stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                return "redirect:/admin";
            }
            
            return "redirect:/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String showRegisterPage(HttpSession session, Model model) {
        // Nếu đã đăng nhập thì redirect về trang chủ
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            // Validation
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Username không được để trống");
                return "redirect:/register";
            }
            
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password phải có ít nhất 6 ký tự");
                return "redirect:/register";
            }
            
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Password và Confirm Password không khớp");
                return "redirect:/register";
            }
            
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email không được để trống");
                return "redirect:/register";
            }
            
            // Đăng ký user
            userService.register(user);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

