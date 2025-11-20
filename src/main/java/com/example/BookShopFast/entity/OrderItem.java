package com.example.BookShopFast.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int quantity;
    private BigDecimal subtotal;
    private BigDecimal bookPrice; // Giá sách tại thời điểm mua
    
    // OrderItem - Book: N-1 (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
    
    // OrderItem - Order: N-1 (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getBookPrice() {
        return bookPrice;
    }
    
    public void setBookPrice(BigDecimal bookPrice) {
        this.bookPrice = bookPrice;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public Orders getOrder() {
        return order;
    }
    
    public void setOrder(Orders order) {
        this.order = order;
    }
}

