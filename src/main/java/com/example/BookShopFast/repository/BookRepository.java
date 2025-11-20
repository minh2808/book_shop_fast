package com.example.BookShopFast.repository;

import com.example.BookShopFast.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByActiveTrue();
    List<Book> findByCategory(String category);
    List<Book> findByTitleContainingIgnoreCase(String keyword);
    List<Book> findByAuthorContainingIgnoreCase(String keyword);
    
    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.active = true AND b.category IS NOT NULL AND b.category != '' ORDER BY b.category")
    List<String> findDistinctCategories();
}

