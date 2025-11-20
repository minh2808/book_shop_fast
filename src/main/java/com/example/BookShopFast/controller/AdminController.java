package com.example.BookShopFast.controller;

import com.example.BookShopFast.entity.*;
import com.example.BookShopFast.repository.*;
import com.example.BookShopFast.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    // Helper method để kiểm tra quyền admin
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        Set<Role> roles = user.getRoles();
        return roles.stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }
    
    // Middleware để kiểm tra quyền admin cho tất cả các endpoint
    private String checkAdminAccess(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }
        return null;
    }
    
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        User user = (User) session.getAttribute("user");
        
        // Thống kê
        long totalBooks = bookRepository.count();
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        long activeBooks = bookRepository.findByActiveTrue().size();
        
        // Đơn hàng gần đây
        List<Orders> recentOrders = orderRepository.findAll();
        if (recentOrders.size() > 10) {
            recentOrders = recentOrders.subList(0, 10);
        }
        
        model.addAttribute("user", user);
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeBooks", activeBooks);
        model.addAttribute("recentOrders", recentOrders);
        
        return "admin/dashboard";
    }
    
    // ========== QUẢN LÝ SÁCH ==========
    @GetMapping("/books")
    public String books(HttpSession session, Model model) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        User user = (User) session.getAttribute("user");
        List<Book> books = bookService.findAllBooks();
        
        model.addAttribute("user", user);
        model.addAttribute("books", books);
        
        return "admin/books";
    }
    
    @GetMapping("/books/new")
    public String showBookForm(HttpSession session, Model model) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("book", new Book());
        model.addAttribute("isEdit", false);
        
        return "admin/book-form";
    }
    
    @GetMapping("/books/{id}/edit")
    public String showEditBookForm(@PathVariable Long id, HttpSession session, Model model) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        User user = (User) session.getAttribute("user");
        Book book = bookService.findById(id).orElse(null);
        
        if (book == null) {
            return "redirect:/admin/books";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("book", book);
        model.addAttribute("isEdit", true);
        
        return "admin/book-form";
    }
    
    @PostMapping("/books")
    public String saveBook(@ModelAttribute Book book, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        try {
            if (book.getImageUrl() == null || book.getImageUrl().isEmpty()) {
                book.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
            }
            bookService.save(book);
            redirectAttributes.addFlashAttribute("success", "Lưu sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu sách: " + e.getMessage());
        }
        
        return "redirect:/admin/books";
    }
    
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        try {
            bookService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sách: " + e.getMessage());
        }
        
        return "redirect:/admin/books";
    }
    
    // ========== QUẢN LÝ ĐƠN HÀNG ==========
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        User user = (User) session.getAttribute("user");
        List<Orders> orders = orderRepository.findAll();
        
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        
        return "admin/orders";
    }
    
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        User user = (User) session.getAttribute("user");
        Orders order = orderRepository.findByIdWithDetails(id).orElse(null);
        
        if (order == null) {
            return "redirect:/admin/orders";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        
        return "admin/order-detail";
    }
    
    @PostMapping("/orders/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id, 
                                   @RequestParam String status,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        try {
            Orders order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setOrderStatus(status);
                orderRepository.save(order);
                redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }
    
    // ========== QUẢN LÝ NGƯỜI DÙNG ==========
    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        String redirect = checkAdminAccess(session);
        if (redirect != null) return redirect;
        
        User user = (User) session.getAttribute("user");
        List<User> users = userRepository.findAll();
        
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        
        return "admin/users";
    }
}

