package com.example.BookShopFast.controller;

import com.example.BookShopFast.entity.Book;
import com.example.BookShopFast.entity.Cart;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.service.BookService;
import com.example.BookShopFast.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private CartService cartService;
    
    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Book> bookOptional = bookService.findById(id);
        
        if (bookOptional.isEmpty() || !bookOptional.get().isActive()) {
            model.addAttribute("error", "Sách không tồn tại hoặc đã bị xóa");
            return "error";
        }
        
        Book book = bookOptional.get();
        model.addAttribute("book", book);
        
        // Lấy sách cùng thể loại (trừ sách hiện tại)
        List<Book> relatedBooks = bookService.findByCategory(book.getCategory())
                .stream()
                .filter(b -> !b.getId().equals(book.getId()) && b.isActive())
                .limit(4)
                .toList();
        model.addAttribute("relatedBooks", relatedBooks);
        
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Cart cart = cartService.getCartByUser(user);
            model.addAttribute("cart", cart);
        }
        
        return "book-detail";
    }
}

