package com.example.BookShopFast.repository;

import com.example.BookShopFast.entity.Book;
import com.example.BookShopFast.entity.Cart;
import com.example.BookShopFast.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);
}

