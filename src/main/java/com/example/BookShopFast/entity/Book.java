package com.example.BookShopFast.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;
    String title;
    String author;
     String publisher;
    String publicationDate;
    String language;
    String category;
    int numberOfPages;
    String format;
    int isbn;
    double shippingWeight;
    double listPrice;
    double ourPrice;
    boolean active=true;

    @Column(columnDefinition="text")
    String description;
    int inStockNumber;
    @Transient
    MultipartFile bookImage;
    String imageUrl; // Đường dẫn ảnh của sách

    // Book - CartItem: 1-N (One-to-Many)
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<CartItem> cartItems = new java.util.ArrayList<>();

    // Getters and Setters for cartItems
    public java.util.List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(java.util.List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
