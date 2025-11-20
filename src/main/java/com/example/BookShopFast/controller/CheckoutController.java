package com.example.BookShopFast.controller;

import com.example.BookShopFast.entity.Cart;
import com.example.BookShopFast.entity.Orders;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.entity.UserPayment;
import com.example.BookShopFast.entity.UserShipping;
import com.example.BookShopFast.repository.UserPaymentRepository;
import com.example.BookShopFast.repository.UserShippingRepository;
import com.example.BookShopFast.service.CartService;
import com.example.BookShopFast.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserShippingRepository userShippingRepository;
    
    @Autowired
    private UserPaymentRepository userPaymentRepository;
    
    @GetMapping
    public String showCheckout(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Cart cart = cartService.getCartByUser(user);
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return "redirect:/cart";
        }
        
        // Lấy danh sách địa chỉ và phương thức thanh toán đã lưu
        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        model.addAttribute("savedShippingAddresses", userShippingRepository.findByUser(user));
        model.addAttribute("savedPaymentMethods", userPaymentRepository.findByUser(user));
        model.addAttribute("userShipping", new UserShipping());
        model.addAttribute("userPayment", new UserPayment());
        
        return "checkout";
    }
    
    @PostMapping
    public String processCheckout(@RequestParam(required = false) Long selectedShippingId,
                                 @RequestParam(required = false) Long selectedPaymentId,
                                 @ModelAttribute UserShipping userShipping,
                                 @ModelAttribute UserPayment userPayment,
                                 @RequestParam String shippingMethod,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Nếu chọn địa chỉ đã lưu
        if (selectedShippingId != null) {
            UserShipping savedShipping = userShippingRepository.findById(selectedShippingId).orElse(null);
            if (savedShipping != null && savedShipping.getUser().getId().equals(user.getId())) {
                userShipping = savedShipping;
            }
        } else {
            // Validation cho địa chỉ mới
            if (userShipping.getName() == null || userShipping.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập tên người nhận");
                return "redirect:/checkout";
            }
            
            if (userShipping.getStreet() == null || userShipping.getStreet().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập địa chỉ");
                return "redirect:/checkout";
            }
        }
        
        // Nếu chọn phương thức thanh toán đã lưu
        if (selectedPaymentId != null) {
            UserPayment savedPayment = userPaymentRepository.findById(selectedPaymentId).orElse(null);
            if (savedPayment != null && savedPayment.getUser().getId().equals(user.getId())) {
                userPayment = savedPayment;
            }
        } else {
            // Validation cho phương thức thanh toán mới
            if (userPayment.getHolderName() == null || userPayment.getHolderName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập tên chủ thẻ");
                return "redirect:/checkout";
            }
            
            if (userPayment.getLast4() == null || userPayment.getLast4().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập số thẻ");
                return "redirect:/checkout";
            }
        }
        
        try {
            Orders order = orderService.createOrder(user, userShipping, userPayment, shippingMethod);
            redirectAttributes.addFlashAttribute("orderId", order.getId());
            redirectAttributes.addFlashAttribute("orderCode", order.getOrderCode());
            return "redirect:/checkout/success";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }
    
    @GetMapping("/success")
    public String checkoutSuccess(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "checkout-success";
    }
}

