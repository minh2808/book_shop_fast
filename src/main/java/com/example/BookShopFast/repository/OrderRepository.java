package com.example.BookShopFast.repository;

import com.example.BookShopFast.entity.Orders;
import com.example.BookShopFast.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.user u LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.book WHERE u = :user ORDER BY o.orderDate DESC")
    List<Orders> findByUserOrderByOrderDateDesc(User user);
    
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.user u LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.book WHERE o.id = :id AND u = :user")
    Optional<Orders> findByIdAndUserWithOrderItems(Long id, User user);
}

