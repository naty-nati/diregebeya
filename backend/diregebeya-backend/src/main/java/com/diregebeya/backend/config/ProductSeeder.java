package com.diregebeya.backend.config;

import com.diregebeya.backend.entity.Category;
import com.diregebeya.backend.entity.Product;
import com.diregebeya.backend.entity.ProductImage;
import com.diregebeya.backend.repository.CategoryRepository;
import com.diregebeya.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds a realistic starter catalog so /shop isn't a blank page on a fresh
 * database. Only runs while the products table is empty, so it never
 * touches or duplicates catalog data an admin has already created through
 * the app.
 */
@Component
@RequiredArgsConstructor
@Order(3)
public class ProductSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProductSeeder.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private record SeedProduct(String name, String brand, String description, BigDecimal price, int stock,
                                String imageTag) {
    }

    private static final List<SeedProduct> CLOTHING = List.of(
            new SeedProduct("Classic Oxford Cotton Shirt", "Everlane",
                    "A breathable cotton oxford with a tailored fit, equally at home under a blazer or rolled up at the sleeves.",
                    new BigDecimal("45.99"), 40, "oxford-shirt"),
            new SeedProduct("Slim Fit Denim Jacket", "Levi's",
                    "Mid-weight stonewashed denim with a slim cut and classic button-front styling.",
                    new BigDecimal("89.99"), 25, "denim-jacket"),
            new SeedProduct("Merino Wool Crew Sweater", "Uniqlo",
                    "Fine-gauge merino knit that layers easily and regulates temperature in any season.",
                    new BigDecimal("59.50"), 30, "wool-sweater"),
            new SeedProduct("Relaxed Linen Trousers", "COS",
                    "Lightweight linen with a relaxed leg and drawstring waist, built for warm-weather comfort.",
                    new BigDecimal("68.00"), 20, "linen-trousers"));

    private static final List<SeedProduct> SHOES = List.of(
            new SeedProduct("Classic Leather Chelsea Boots", "Clarks",
                    "Hand-finished leather Chelsea boots with an elastic side panel and stacked heel.",
                    new BigDecimal("129.99"), 18, "chelsea-boots"),
            new SeedProduct("Court Legacy Sneakers", "Nike",
                    "A retro low-top silhouette in smooth leather with classic court-style branding.",
                    new BigDecimal("74.99"), 50, "sneakers"),
            new SeedProduct("Suede Desert Boots", "Clarks",
                    "Original crepe-sole desert boots in soft suede, unlined for a broken-in feel from day one.",
                    new BigDecimal("110.00"), 22, "desert-boots"),
            new SeedProduct("Running Flex Trainers", "Adidas",
                    "Lightweight mesh trainers with responsive cushioning for daily runs or everyday wear.",
                    new BigDecimal("84.50"), 35, "running-shoes"));

    private static final List<SeedProduct> WATCHES = List.of(
            new SeedProduct("Automatic Chronograph Watch", "Seiko",
                    "A self-winding chronograph with a stainless steel case and sapphire-coated crystal.",
                    new BigDecimal("249.00"), 12, "chronograph-watch"),
            new SeedProduct("Minimalist Leather Strap Watch", "MVMT",
                    "A slim-profile watch face on a genuine leather strap, designed for everyday minimal style.",
                    new BigDecimal("118.00"), 28, "leather-watch"),
            new SeedProduct("Stainless Steel Dive Watch", "Citizen",
                    "Water-resistant to 200m with a unidirectional bezel and luminous hands for low-light readability.",
                    new BigDecimal("189.99"), 15, "dive-watch"));

    private static final List<SeedProduct> PERFUMES = List.of(
            new SeedProduct("Bleu Eau de Parfum", "Chanel",
                    "A woody aromatic fragrance with notes of citrus, sandalwood, and cedar.",
                    new BigDecimal("135.00"), 20, "perfume-bottle"),
            new SeedProduct("Sauvage Eau de Toilette", "Dior",
                    "A fresh, spicy composition built around bergamot and Sichuan pepper.",
                    new BigDecimal("98.50"), 30, "cologne"),
            new SeedProduct("Amber & Oud Cologne", "Tom Ford",
                    "A rich, warm blend of amber and oud wood for a distinctive evening signature scent.",
                    new BigDecimal("210.00"), 10, "amber-perfume"));

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Products table already has data - skipping catalog seed");
            return;
        }

        seedCategory("Clothing", "Men's and women's apparel", CLOTHING);
        seedCategory("Shoes", "Sneakers, boots, and formal footwear", SHOES);
        seedCategory("Watches", "Analog and automatic watches", WATCHES);
        seedCategory("Perfumes", "Fragrances for men and women", PERFUMES);

        log.info("Seeded starter product catalog");
    }

    private void seedCategory(String name, String description, List<SeedProduct> products) {
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder().name(name).description(description).build()));

        for (SeedProduct sp : products) {
            Product product = Product.builder()
                    .name(sp.name())
                    .description(sp.description())
                    .brand(sp.brand())
                    .price(sp.price())
                    .stock(sp.stock())
                    .imageUrl(imageUrl(sp.imageTag(), 1))
                    .category(category)
                    .build();

            product.getImages().add(ProductImage.builder()
                    .imageUrl(imageUrl(sp.imageTag(), 1))
                    .displayOrder(0)
                    .product(product)
                    .build());
            product.getImages().add(ProductImage.builder()
                    .imageUrl(imageUrl(sp.imageTag(), 2))
                    .displayOrder(1)
                    .product(product)
                    .build());

            productRepository.save(product);
        }
    }

    /** Deterministic per-product photo from a keyword-tagged free image feed - not a generic placeholder. */
    private String imageUrl(String tag, int variant) {
        return "https://loremflickr.com/800/800/%s?lock=%s".formatted(tag, tag.hashCode() + variant);
    }
}
