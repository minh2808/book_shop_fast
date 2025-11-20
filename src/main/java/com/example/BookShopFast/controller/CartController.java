package com.example.BookShopFast.controller;

import com.example.BookShopFast.entity.Cart;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        return "cart";
    }
    
    @PostMapping("/add")
    public String addToCart(@RequestParam Long bookId,
                           @RequestParam(defaultValue = "1") int quantity,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để thêm sách vào giỏ hàng");
            return "redirect:/login";
        }
        
        try {
            cartService.addBookToCart(user, bookId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sách vào giỏ hàng!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/book/" + bookId;
    }
    
    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long cartItemId,
                                @RequestParam int quantity,
                                RedirectAttributes redirectAttributes) {
        try {
            cartService.updateCartItemQuantity(cartItemId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật giỏ hàng");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }
    
    @PostMapping("/remove")
    public String removeCartItem(@RequestParam Long cartItemId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập");
            return "redirect:/login";
        }

        
        try {
            cartService.removeCartItem(cartItemId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sách khỏi giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        cartService.clearCart(user);
        redirectAttributes.addFlashAttribute("success", "Đã xóa tất cả sách khỏi giỏ hàng");
        return "redirect:/cart";
    }
}

