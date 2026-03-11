package com.vedu.shop;

import com.vedu.shop.model.Product;
import com.vedu.shop.repository.ProductRepository;
import com.vedu.shop.service.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L).name("Test Headphones")
                .description("Great sound").price(1999.0)
                .stock(50).category("Electronics").active(true)
                .build();
    }

    @Test
    @DisplayName("Should return all active products")
    void testGetAllActiveProducts() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of(sampleProduct));
        List<Product> result = productService.getAllActiveProducts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Headphones");
        verify(productRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Should find product by ID")
    void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        Optional<Product> result = productService.getProductById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualTo(1999.0);
    }

    @Test
    @DisplayName("Should return empty when product not found")
    void testGetProductByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Product> result = productService.getProductById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should save product successfully")
    void testSaveProduct() {
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);
        Product saved = productService.saveProduct(sampleProduct);
        assertThat(saved.getName()).isEqualTo("Test Headphones");
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    @DisplayName("Should check stock availability - sufficient stock")
    void testIsInStockTrue() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        assertThat(productService.isInStock(1L, 10)).isTrue();
    }

    @Test
    @DisplayName("Should check stock availability - insufficient stock")
    void testIsInStockFalse() {
        sampleProduct.setStock(3);
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        assertThat(productService.isInStock(1L, 10)).isFalse();
    }

    @Test
    @DisplayName("Should soft-delete product (set active=false)")
    void testDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
        productService.deleteProduct(1L);
        verify(productRepository).save(argThat(p -> !p.isActive()));
    }

    @Test
    @DisplayName("Should filter products by category")
    void testGetProductsByCategory() {
        when(productRepository.findByCategoryAndActiveTrue("Electronics"))
                .thenReturn(List.of(sampleProduct));
        List<Product> result = productService.getProductsByCategory("Electronics");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Should search products by name")
    void testSearchProducts() {
        when(productRepository.findByNameContainingIgnoreCaseAndActiveTrue("head"))
                .thenReturn(List.of(sampleProduct));
        List<Product> result = productService.searchProducts("head");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should return all products including inactive for admin")
    void testGetAllProducts() {
        Product inactive = Product.builder().id(2L).name("Old Product").active(false).build();
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct, inactive));
        List<Product> result = productService.getAllProducts();
        assertThat(result).hasSize(2);
    }
}
