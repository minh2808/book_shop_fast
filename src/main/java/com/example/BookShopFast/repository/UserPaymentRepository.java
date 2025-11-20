package com.example.BookShopFast.repository;

import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.entity.UserPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, Long> {
    List<UserPayment> findByUser(User user);
}

