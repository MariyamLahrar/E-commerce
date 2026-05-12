package com.commerce.Online.config;

import com.commerce.Online.entity.*;
import com.commerce.Online.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Initialisation des données...");

        // ── Rôles ──────────────────────────────────────────────
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        // ── Admin ──────────────────────────────────────────────
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Super")
                    .lastName("Admin")
                    .phone("0600000000")
                    .enabled(true)
                    .roles(new HashSet<>(Set.of(roleAdmin, roleUser)))
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin créé : admin / admin123");
        }

        // ── User ───────────────────────────────────────────────
        User regularUser = null;
        if (!userRepository.existsByUsername("user")) {
            regularUser = User.builder()
                    .username("user")
                    .email("user@ecommerce.com")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("Jean")
                    .lastName("Dupont")
                    .phone("0611111111")
                    .enabled(true)
                    .roles(new HashSet<>(Set.of(roleUser)))
                    .build();
            regularUser = userRepository.save(regularUser);
            Cart cart = Cart.builder().user(regularUser).items(new ArrayList<>()).build();
            cartRepository.save(cart);
            log.info("✅ User créé : user / user123");
        } else {
            regularUser = userRepository.findByUsername("user").orElse(null);
        }

        // Deuxième utilisateur de démo
        User user2 = null;
        if (!userRepository.existsByUsername("marie")) {
            user2 = User.builder()
                    .username("marie")
                    .email("marie@ecommerce.com")
                    .password(passwordEncoder.encode("marie123"))
                    .firstName("Marie")
                    .lastName("Martin")
                    .phone("0622222222")
                    .enabled(true)
                    .roles(new HashSet<>(Set.of(roleUser)))
                    .build();
            user2 = userRepository.save(user2);
            Cart cart2 = Cart.builder().user(user2).items(new ArrayList<>()).build();
            cartRepository.save(cart2);
        }

        // ── Articles ───────────────────────────────────────────
        if (articleRepository.count() == 0) {
            List<Article> articles = buildArticles();
            articleRepository.saveAll(articles);
            log.info("✅ {} articles créés", articles.size());

            // ── Commandes de démonstration ─────────────────────
            if (regularUser != null && articles.size() >= 4) {
                // Commande validée
                Order order1 = Order.builder()
                        .user(regularUser)
                        .status(OrderStatus.VALIDEE)
                        .totalAmount(new BigDecimal("1698.00"))
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .shippingAddress("12 Rue de la Paix, Paris")
                        .items(new ArrayList<>())
                        .build();
                Order savedOrder1 = orderRepository.save(order1);
                OrderItem oi1 = OrderItem.builder().order(savedOrder1).article(articles.get(0))
                        .quantity(1).unitPrice(articles.get(0).getPrice()).build();
                OrderItem oi2 = OrderItem.builder().order(savedOrder1).article(articles.get(2))
                        .quantity(2).unitPrice(articles.get(2).getPrice()).build();
                savedOrder1.getItems().addAll(List.of(oi1, oi2));
                orderRepository.save(savedOrder1);

                // Commande en attente
                Order order2 = Order.builder()
                        .user(regularUser)
                        .status(OrderStatus.EN_ATTENTE)
                        .totalAmount(new BigDecimal("299.99"))
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .shippingAddress("12 Rue de la Paix, Paris")
                        .items(new ArrayList<>())
                        .build();
                Order savedOrder2 = orderRepository.save(order2);
                OrderItem oi3 = OrderItem.builder().order(savedOrder2).article(articles.get(4))
                        .quantity(1).unitPrice(articles.get(4).getPrice()).build();
                savedOrder2.getItems().add(oi3);
                orderRepository.save(savedOrder2);

                // Commande annulée
                Order order3 = Order.builder()
                        .user(regularUser)
                        .status(OrderStatus.ANNULEE)
                        .totalAmount(new BigDecimal("89.99"))
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .shippingAddress("12 Rue de la Paix, Paris")
                        .items(new ArrayList<>())
                        .build();
                Order savedOrder3 = orderRepository.save(order3);
                OrderItem oi4 = OrderItem.builder().order(savedOrder3).article(articles.get(6))
                        .quantity(1).unitPrice(articles.get(6).getPrice()).build();
                savedOrder3.getItems().add(oi4);
                orderRepository.save(savedOrder3);

                log.info("✅ Commandes de démonstration créées");
            }
        }

        log.info("🎉 Données initialisées avec succès !");
    }

    private List<Article> buildArticles() {
        return List.of(
                Article.builder().name("MacBook Pro 14\"").description("Puce Apple M3 Pro, 18Go RAM, 512Go SSD. Performance exceptionnelle pour les professionnels.")
                        .price(new BigDecimal("2499.00")).stock(15).category("Informatique").brand("Apple")
                        .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400").active(true).build(),

                Article.builder().name("iPhone 15 Pro").description("Titane naturel, puce A17 Pro, appareil photo 48MP, Dynamic Island. Le smartphone ultime.")
                        .price(new BigDecimal("1299.00")).stock(30).category("Téléphonie").brand("Apple")
                        .imageUrl("https://images.unsplash.com/photo-1696446701796-da61229f9097?w=400").active(true).build(),

                Article.builder().name("Samsung Galaxy S24 Ultra").description("200MP, S Pen intégré, écran 6.8\" QHD+, batterie 5000mAh. Le roi des Android.")
                        .price(new BigDecimal("1399.00")).stock(25).category("Téléphonie").brand("Samsung")
                        .imageUrl("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400").active(true).build(),

                Article.builder().name("Sony WH-1000XM5").description("Casque à réduction de bruit leader du marché. 30h d'autonomie, son Hi-Res Audio.")
                        .price(new BigDecimal("349.00")).stock(50).category("Audio").brand("Sony")
                        .imageUrl("https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=400").active(true).build(),

                Article.builder().name("AirPods Pro 2").description("Réduction de bruit adaptative, audio spatial personnalisé, boîtier USB-C.")
                        .price(new BigDecimal("279.00")).stock(60).category("Audio").brand("Apple")
                        .imageUrl("https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=400").active(true).build(),

                Article.builder().name("Dell XPS 15").description("Intel Core i9, RTX 4070, 32Go RAM, écran OLED 3.5K. Puissance et élégance.")
                        .price(new BigDecimal("2199.00")).stock(10).category("Informatique").brand("Dell")
                        .imageUrl("https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400").active(true).build(),

                Article.builder().name("Logitech MX Master 3S").description("Souris sans fil ergonomique, scroll ultra-rapide, 8000 DPI. Productivité maximale.")
                        .price(new BigDecimal("109.00")).stock(80).category("Accessoires").brand("Logitech")
                        .imageUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400").active(true).build(),

                Article.builder().name("Samsung 4K Monitor 27\"").description("Dalle IPS 4K UHD, 144Hz, HDR600, USB-C 65W. Idéal créatifs et gamers.")
                        .price(new BigDecimal("699.00")).stock(20).category("Informatique").brand("Samsung")
                        .imageUrl("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400").active(true).build(),

                Article.builder().name("iPad Pro 12.9\"").description("Puce M2, écran Liquid Retina XDR, compatible Apple Pencil 2. La tablette pro.")
                        .price(new BigDecimal("1199.00")).stock(18).category("Tablettes").brand("Apple")
                        .imageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400").active(true).build(),

                Article.builder().name("Bose QuietComfort 45").description("Confort légendaire Bose, 24h d'autonomie, son équilibré. Pour voyager sereinement.")
                        .price(new BigDecimal("329.00")).stock(35).category("Audio").brand("Bose")
                        .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400").active(true).build(),

                Article.builder().name("OnePlus 12").description("Snapdragon 8 Gen 3, charge 100W, caméra Hasselblad, 50W sans fil.")
                        .price(new BigDecimal("899.00")).stock(40).category("Téléphonie").brand("OnePlus")
                        .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400").active(true).build(),

                Article.builder().name("Keychron K2 Pro").description("Clavier mécanique sans fil, switches Gateron, rétroéclairage RGB, compatible Mac/Win.")
                        .price(new BigDecimal("129.00")).stock(45).category("Accessoires").brand("Keychron")
                        .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400").active(true).build(),

                Article.builder().name("Anker 735 GaNPrime").description("Chargeur 65W 3 ports GaN, ultra-compact, compatible PD 3.0 et Quick Charge.")
                        .price(new BigDecimal("59.99")).stock(100).category("Accessoires").brand("Anker")
                        .imageUrl("https://images.unsplash.com/photo-1609592806596-b994c8d7f96d?w=400").active(true).build(),

                Article.builder().name("DJI Mini 4 Pro").description("Drone 4K HDR, omnidirectionnel, 34min de vol, transmission 20km. Pour les aventuriers.")
                        .price(new BigDecimal("799.00")).stock(12).category("Photo & Vidéo").brand("DJI")
                        .imageUrl("https://images.unsplash.com/photo-1473968512647-3e447244af8f?w=400").active(true).build(),

                Article.builder().name("Samsung T7 Shield SSD").description("SSD externe 2To, IP65 résistant eau/poussière, USB 3.2 Gen 2, 1050 Mo/s.")
                        .price(new BigDecimal("189.00")).stock(55).category("Stockage").brand("Samsung")
                        .imageUrl("https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400").active(true).build(),

                Article.builder().name("Xiaomi 14 Ultra").description("Leica Summilux, Snapdragon 8 Gen 3, 5000mAh, charge 90W. Photo mobile ultime.")
                        .price(new BigDecimal("1099.00")).stock(22).category("Téléphonie").brand("Xiaomi")
                        .imageUrl("https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400").active(true).build()
        );
    }
}