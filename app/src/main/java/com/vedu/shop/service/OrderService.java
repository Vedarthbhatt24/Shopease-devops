package com.vedu.shop.service;

import com.vedu.shop.model.*;
import com.vedu.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order placeOrder(String name, String email, String address,
                            Map<Long, Integer> cartItems) {
        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        Order order = Order.builder()
                .customerName(name)
                .customerEmail(email)
                .shippingAddress(address)
                .status(Order.OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + entry.getKey()));

            if (product.getStock() < entry.getValue()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            product.setStock(product.getStock() - entry.getValue());
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(entry.getValue())
                    .unitPrice(product.getPrice())
                    .build();
            items.add(item);
            total += item.getSubtotal();
        }

        order.setItems(items);
        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmailOrderByOrderDateDesc(email);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
