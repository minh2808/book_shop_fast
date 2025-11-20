package com.example.BookShopFast.service;

import com.example.BookShopFast.entity.Book;
import com.example.BookShopFast.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Book> findAllActiveBooks() {
        return bookRepository.findByActiveTrue();
    }
    
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    public Book save(Book book) {
        return bookRepository.save(book);
    }
    
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}

