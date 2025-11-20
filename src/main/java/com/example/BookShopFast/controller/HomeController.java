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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private CartService cartService;
    
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Book> books = bookService.findAllActiveBooks();
        List<String> categories = bookService.findAllDistinctCategories();
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Cart cart = cartService.getCartByUser(user);
            model.addAttribute("cart", cart);
        }
        return "home";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model, HttpSession session) {
        List<Book> books;
        if (keyword != null && !keyword.isEmpty()) {
            books = bookService.searchByTitle(keyword);
            if (books.isEmpty()) {
                books = bookService.searchByAuthor(keyword);
            }
        } else {
            books = bookService.findAllActiveBooks();
        }
        List<String> categories = bookService.findAllDistinctCategories();
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categories);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Cart cart = cartService.getCartByUser(user);
            model.addAttribute("cart", cart);
        }
        return "home";
    }
    
    @GetMapping("/category")
    public String category(@RequestParam String category, Model model, HttpSession session) {
        List<Book> books = bookService.findByCategory(category);
        List<String> categories = bookService.findAllDistinctCategories();
        model.addAttribute("books", books);
        model.addAttribute("category", category);
        model.addAttribute("categories", categories);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Cart cart = cartService.getCartByUser(user);
            model.addAttribute("cart", cart);
        }
        return "home";
    }
}

