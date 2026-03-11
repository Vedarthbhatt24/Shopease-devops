package com.vedu.shop.repository;

import com.vedu.shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(String category);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
