package com.vedu.shop.repository;

import com.vedu.shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmailOrderByOrderDateDesc(String email);
    List<Order> findAllByOrderByOrderDateDesc();
}
