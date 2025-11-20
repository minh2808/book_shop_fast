package com.example.BookShopFast.repository;

import com.example.BookShopFast.entity.OrderItem;
import com.example.BookShopFast.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Orders order);
}

