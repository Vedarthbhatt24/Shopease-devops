package com.vedu.shop.service;

import com.vedu.shop.model.Product;
import com.vedu.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {

            // Electronics
            productRepository.save(Product.builder()
                .name("boAt Rockerz 450 Bluetooth Headphone")
                .description("Over-ear wireless headphone with 15 hours playback, 40mm drivers and soft padded earcups. Compatible with all Bluetooth devices. Comes with aux cable.")
                .price(1299.00).stock(85).category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Redmi 13C 4G Smartphone 128GB")
                .description("6.74 inch HD+ display, 50MP AI triple camera, 5000mAh battery, MediaTek Helio G85 processor. Available in Startrail Black and Startrail Green.")
                .price(9499.00).stock(40).category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Noise ColorFit Pro 4 Smartwatch")
                .description("1.72 inch TFT display, SpO2 and heart rate monitor, 100+ sports modes, IP68 water resistant, 7 day battery life. BT calling enabled.")
                .price(2199.00).stock(60).category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Portronics Toad 23 Wireless Mouse")
                .description("2.4GHz wireless connectivity, 1600 DPI optical sensor, plug and play USB nano receiver, 12 months battery life. Ergonomic design for long usage.")
                .price(349.00).stock(150).category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Zebronics ZEB-K35 USB Keyboard")
                .description("Full size wired USB keyboard with rupee key, spill resistant design, 104 keys, plug and play. Compatible with Windows, Linux and Mac.")
                .price(449.00).stock(120).category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400")
                .active(true).build());

            // Clothing
            productRepository.save(Product.builder()
                .name("Jockey Men's Cotton Round Neck T-Shirt")
                .description("100% super combed cotton, bio-washed fabric for extra softness. Regular fit, ribbed collar with stay-fresh treatment. Available in S, M, L, XL, XXL.")
                .price(399.00).stock(300).category("Clothing")
                .imageUrl("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Peter England Men's Slim Fit Formal Shirt")
                .description("Premium cotton blend formal shirt with wrinkle-free finish. Full sleeves, spread collar, suitable for office and semi-formal occasions.")
                .price(799.00).stock(180).category("Clothing")
                .imageUrl("https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("W Women's Kurta Set with Dupatta")
                .description("Printed straight kurta with palazzo pants and matching dupatta. Machine washable rayon fabric. Festive and casual wear. Sizes XS to 3XL.")
                .price(1199.00).stock(90).category("Clothing")
                .imageUrl("https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?w=400")
                .active(true).build());

            // Footwear
            productRepository.save(Product.builder()
                .name("Bata Men's Casual Sneakers")
                .description("Lightweight EVA sole, mesh upper for breathability, cushioned insole for all day comfort. Lace-up closure. Available in sizes 6 to 11.")
                .price(1499.00).stock(110).category("Footwear")
                .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Relaxo Flite Men's Daily Wear Slippers")
                .description("Lightweight EVA material, anti-skid textured sole, comfortable footbed. Daily wear Hawaii chappals. Available in sizes 6 to 11.")
                .price(249.00).stock(400).category("Footwear")
                .imageUrl("https://images.unsplash.com/photo-1603487742131-4160ec999306?w=400")
                .active(true).build());

            // Kitchen
            productRepository.save(Product.builder()
                .name("Milton Thermosteel Flask 1 Litre")
                .description("Double wall stainless steel vacuum insulated flask. Keeps beverages hot for 24 hours and cold for 24 hours. Leak proof, rust free body.")
                .price(699.00).stock(200).category("Kitchen")
                .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Prestige Aluminium Pressure Cooker 5 Litre")
                .description("ISI certified, weight valve with safety plug, compatible with gas and induction stove. Comes with 5 year warranty.")
                .price(1349.00).stock(70).category("Kitchen")
                .imageUrl("https://images.unsplash.com/photo-1585515320310-259814833e62?w=400")
                .active(true).build());

            // Bags
            productRepository.save(Product.builder()
                .name("Skybags Footloose Laptop Backpack 30L")
                .description("Water resistant polyester, dedicated 15.6 inch laptop compartment, USB charging port, multiple organiser pockets. 2 year brand warranty.")
                .price(1799.00).stock(65).category("Bags")
                .imageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400")
                .active(true).build());

            // Sports
            productRepository.save(Product.builder()
                .name("Nivia Storm Football Size 5")
                .description("32 panel machine stitched PVC football. Size 5 as per FIFA standards. Suitable for practice and recreational play on all surfaces.")
                .price(499.00).stock(95).category("Sports")
                .imageUrl("https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Boldfit Yoga Mat 6mm Anti-Slip")
                .description("TPE eco-friendly material, double sided non-slip texture, moisture resistant. Includes carrying strap. 183cm x 61cm. 1 year warranty.")
                .price(799.00).stock(80).category("Sports")
                .imageUrl("https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400")
                .active(true).build());

            // Stationery
            productRepository.save(Product.builder()
                .name("Classmate 6 Subject Spiral Notebook A4")
                .description("180 pages, single line ruling, micro perforated pages, stiff cardboard back cover. Suitable for college and school students.")
                .price(129.00).stock(500).category("Stationery")
                .imageUrl("https://images.unsplash.com/photo-1531346878377-a5be20888e57?w=400")
                .active(true).build());

            productRepository.save(Product.builder()
                .name("Parker Beta Premium Ball Pen Pack of 5")
                .description("Medium tip 1.0mm, smooth flow blue ink, ergonomic rubber grip, stainless steel tip. Suitable for exams, office and daily writing.")
                .price(199.00).stock(600).category("Stationery")
                .imageUrl("https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?w=400")
                .active(true).build());
        }
    }
}
