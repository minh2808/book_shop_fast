package com.example.BookShopFast.service;

import com.example.BookShopFast.entity.Book;
import com.example.BookShopFast.entity.Cart;
import com.example.BookShopFast.entity.CartItem;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.repository.BookRepository;
import com.example.BookShopFast.repository.CartItemRepository;
import com.example.BookShopFast.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    public Cart getOrCreateCart(User user) {
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setGrandTotal(BigDecimal.ZERO);
            return cartRepository.save(cart);
        }
    }
    
    @Transactional
    public CartItem addBookToCart(User user, Long bookId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        
        if (bookOptional.isEmpty()) {
            throw new RuntimeException("Sách không tồn tại");
        }
        
        Book book = bookOptional.get();
        
        if (!book.isActive()) {
            throw new RuntimeException("Sách không còn bán");
        }
        
        if (book.getInStockNumber() < quantity) {
            throw new RuntimeException("Số lượng sách không đủ");
        }
        
        // Kiểm tra xem sách đã có trong giỏ hàng chưa
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndBook(cart, book);
        
        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Nếu đã có, cập nhật số lượng
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (book.getInStockNumber() < newQuantity) {
                throw new RuntimeException("Số lượng sách không đủ");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            // Nếu chưa có, tạo mới
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(quantity);
        }
        
        // Tính subtotal
        BigDecimal subtotal = BigDecimal.valueOf(book.getOurPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        cartItem.setSubtotal(subtotal);
        
        cartItem = cartItemRepository.save(cartItem);
        
        // Cập nhật GrandTotal
        updateCartTotal(cart);
        
        return cartItem;
    }
    
    @Transactional
    public void removeCartItem(Long cartItemId) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            Cart cart = cartItem.getCart();
            cart.getCartItems().remove(cartItem);
            updateCartTotal(cart);

        }
    }
    
    @Transactional
    public void updateCartItemQuantity(Long cartItemId, int quantity) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            Book book = cartItem.getBook();
            
            if (quantity <= 0) {
                removeCartItem(cartItemId);
                return;
            }
            
            if (book.getInStockNumber() < quantity) {
                throw new RuntimeException("Số lượng sách không đủ");
            }
            
            cartItem.setQuantity(quantity);
            BigDecimal subtotal = BigDecimal.valueOf(book.getOurPrice()).multiply(BigDecimal.valueOf(quantity));
            cartItem.setSubtotal(subtotal);
            cartItemRepository.save(cartItem);
            
            updateCartTotal(cartItem.getCart());
        }
    }
    
    private void updateCartTotal(Cart cart) {
        List<CartItem> cartItems = cart.getCartItems();
        BigDecimal grandTotal = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setGrandTotal(grandTotal);
        cartRepository.save(cart);
    }
    
    public Cart getCartByUser(User user) {
        return getOrCreateCart(user);
    }
    
    @Transactional
    public void clearCart(User user) {
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            cart.getCartItems().clear();
            cart.setGrandTotal(BigDecimal.ZERO);
            cartRepository.save(cart);
        }
    }
}

