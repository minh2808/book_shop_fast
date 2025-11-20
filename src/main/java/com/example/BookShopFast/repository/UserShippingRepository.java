package com.example.BookShopFast.repository;

import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.entity.UserShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserShippingRepository extends JpaRepository<UserShipping, Long> {
    List<UserShipping> findByUser(User user);
}

