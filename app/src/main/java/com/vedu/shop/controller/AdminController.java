package com.vedu.shop.controller;

import com.vedu.shop.model.*;
import com.vedu.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        double revenue = orderService.getAllOrders().stream()
                .mapToDouble(Order::getTotalAmount).sum();
        model.addAttribute("totalRevenue", revenue);
        model.addAttribute("recentOrders", orderService.getAllOrders().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // PRODUCTS
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(p -> model.addAttribute("product", p));
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute Product product,
                              BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "admin/product-form";
        productService.saveProduct(product);
        ra.addFlashAttribute("message", "Product saved successfully!");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.deleteProduct(id);
        ra.addFlashAttribute("message", "Product deleted.");
        return "redirect:/admin/products";
    }

    // ORDERS
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        orderService.getOrderById(id).ifPresent(o -> model.addAttribute("order", o));
        return "admin/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam Order.OrderStatus status,
                               RedirectAttributes ra) {
        orderService.updateOrderStatus(id, status);
        ra.addFlashAttribute("message", "Order status updated!");
        return "redirect:/admin/orders/" + id;
    }
}
