package com.example.BookShopFast.service;

import com.example.BookShopFast.entity.*;
import com.example.BookShopFast.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserShippingRepository userShippingRepository;
    
    @Autowired
    private UserPaymentRepository userPaymentRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Transactional
    public Orders createOrder(User user, UserShipping userShipping, UserPayment userPayment, String shippingMethod) {
        // Lấy giỏ hàng
        Cart cart = cartService.getCartByUser(user);
        
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }
        
        // Kiểm tra số lượng tồn kho
        for (CartItem item : cart.getCartItems()) {
            Book book = item.getBook();
            if (book.getInStockNumber() < item.getQuantity()) {
                throw new RuntimeException("Sách '" + book.getTitle() + "' không đủ số lượng tồn kho");
            }
        }
        
        // Lưu UserShipping nếu chưa có id (mới tạo)
        if (userShipping.getId() == null) {
            userShipping.setUser(user);
            userShipping = userShippingRepository.save(userShipping);
        }
        
        // Lưu UserPayment nếu chưa có id (mới tạo)
        if (userPayment.getId() == null) {
            userPayment.setUser(user);
            userPayment = userPaymentRepository.save(userPayment);
        }
        
        // Tạo đơn hàng
        Orders order = new Orders();
        order.setUser(user);
        order.setUserShipping(userShipping);
        order.setUserPayment(userPayment);
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setOrderDate(new Date());
        order.setShippingMethod(shippingMethod);
        order.setOrderStatus("Đang xử lý");
        order.setOrderTotal(cart.getGrandTotal());
        
        // Tính ngày giao hàng (3 ngày sau)
        Date shippingDate = new Date();
        shippingDate.setTime(shippingDate.getTime() + (3 * 24 * 60 * 60 * 1000L));
        order.setShippingDate(shippingDate);
        
        order = orderRepository.save(order);
        
        // Lưu danh sách CartItem và Book trước khi xóa
        List<CartItem> cartItems = new java.util.ArrayList<>(cart.getCartItems());
        List<Book> booksToUpdate = new java.util.ArrayList<>();
        List<Integer> quantities = new java.util.ArrayList<>();
        
        // Tạo OrderItem từ CartItem và chuẩn bị cập nhật số lượng tồn kho
        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();
            
            // Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItem.setBookPrice(java.math.BigDecimal.valueOf(book.getOurPrice()));
            orderItemRepository.save(orderItem);
            
            // Lưu thông tin để cập nhật số lượng tồn kho
            booksToUpdate.add(book);
            quantities.add(cartItem.getQuantity());
        }
        
        // Cập nhật số lượng tồn kho
        for (int i = 0; i < booksToUpdate.size(); i++) {
            Book book = booksToUpdate.get(i);
            int quantity = quantities.get(i);
            book.setInStockNumber(book.getInStockNumber() - quantity);
            bookRepository.save(book);
        }

        
        // Xóa giỏ hàng (sau khi đã tạo xong OrderItem)
        cartService.clearCart(user);
        
        return order;
    }
    
    @Transactional(readOnly = true)
    public List<Orders> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
    
    public Orders getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public Orders getOrderByIdAndUser(Long id, User user) {
        return orderRepository.findByIdAndUserWithOrderItems(id, user).orElse(null);
    }
    
    @Transactional
    public Orders cancelOrder(Long orderId, User user) {
        Orders order = getOrderByIdAndUser(orderId, user);
        
        if (order == null) {
            throw new RuntimeException("Đơn hàng không tồn tại hoặc không thuộc về bạn");
        }
        
        // Chỉ cho phép hủy đơn hàng khi đang ở trạng thái "Đang xử lý"
        if (!"Đang xử lý".equals(order.getOrderStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Đang xử lý'");
        }
        
        // Cập nhật lại số lượng tồn kho cho các sách trong đơn hàng
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Book book = orderItem.getBook();
                book.setInStockNumber(book.getInStockNumber() + orderItem.getQuantity());
                bookRepository.save(book);
            }
        }
        
        // Cập nhật trạng thái đơn hàng thành "Đã hủy"
        order.setOrderStatus("Đã hủy");
        order = orderRepository.save(order);
        
        return order;
    }
}

