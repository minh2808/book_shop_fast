package com.example.BookShopFast.controller;

import com.example.BookShopFast.entity.Cart;
import com.example.BookShopFast.entity.Orders;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.service.CartService;
import com.example.BookShopFast.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public String viewOrders(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Orders> orders = orderService.getOrdersByUser(user);
        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        return "orders";
    }
    
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Orders order = orderService.getOrderByIdAndUser(id, user);
        if (order == null) {
            System.out.println("Nhảy vào đây");
            model.addAttribute("error", "Đơn hàng không tồn tại");
            return "error";
        }
        
        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("order", order);
        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        
        return "order-detail";
    }
    
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            orderService.cancelOrder(id, user);
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được hủy thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/orders/" + id;
    }
}

