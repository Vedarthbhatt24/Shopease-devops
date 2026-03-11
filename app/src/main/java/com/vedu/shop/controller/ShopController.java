package com.vedu.shop.controller;

import com.vedu.shop.model.*;
import com.vedu.shop.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;
    private final OrderService orderService;

    // HOME - Product listing
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String search,
                       HttpSession session) {
        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("search", search);
        } else if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            products = productService.getAllActiveProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("cartCount", getCartCount(session));
        return "shop/home";
    }

    // PRODUCT DETAIL
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        productService.getProductById(id).ifPresent(p -> model.addAttribute("product", p));
        model.addAttribute("cartCount", getCartCount(session));
        return "shop/product-detail";
    }

    // ADD TO CART
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes ra) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();
        cart.merge(productId, quantity, Integer::sum);
        session.setAttribute("cart", cart);
        ra.addFlashAttribute("message", "Item added to cart!");
        return "redirect:/";
    }

    // VIEW CART
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();

        List<Map<String, Object>> cartItems = new ArrayList<>();
        double total = 0;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            productService.getProductById(entry.getKey()).ifPresent(p -> {
                Map<String, Object> item = new HashMap<>();
                item.put("product", p);
                item.put("quantity", entry.getValue());
                item.put("subtotal", p.getPrice() * entry.getValue());
                cartItems.add(item);
            });
            total += productService.getProductById(entry.getKey())
                    .map(p -> p.getPrice() * entry.getValue()).orElse(0.0);
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("cartCount", cart.size());
        return "shop/cart";
    }

    // REMOVE FROM CART
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) cart.remove(productId);
        return "redirect:/cart";
    }

    // CHECKOUT PAGE
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";
        model.addAttribute("cartCount", cart.size());
        return "shop/checkout";
    }

    // PLACE ORDER
    @PostMapping("/checkout/place")
    public String placeOrder(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String address,
                             HttpSession session,
                             RedirectAttributes ra) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";
        try {
            Order order = orderService.placeOrder(name, email, address, cart);
            session.removeAttribute("cart");
            ra.addFlashAttribute("orderId", order.getId());
            ra.addFlashAttribute("orderTotal", order.getTotalAmount());
            return "redirect:/order-success";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    // ORDER SUCCESS
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "shop/order-success";
    }

    // TRACK ORDER
    @GetMapping("/track-order")
    public String trackOrder(@RequestParam(required = false) String email, Model model) {
        if (email != null && !email.isEmpty()) {
            model.addAttribute("orders", orderService.getOrdersByEmail(email));
            model.addAttribute("email", email);
        }
        return "shop/track-order";
    }

    private int getCartCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        return cart == null ? 0 : cart.size();
    }
}
