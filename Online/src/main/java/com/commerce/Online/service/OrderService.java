package com.commerce.Online.service;

import com.commerce.Online.converter.OrderConverter;
import com.commerce.Online.dto.OrderDTO;
import com.commerce.Online.entity.*;
import com.commerce.Online.exception.ResourceNotFoundException;
import com.commerce.Online.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CartService cartService;
    private final OrderConverter orderConverter;

    // Utile pour le contrôleur afin d'éviter les erreurs de type Page dans Thymeleaf
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersListByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .map(orderConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findByUsername(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(orderConverter::toDTO);
    }

    public OrderDTO placeOrder(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Cart cart = cartService.getCartEntityByUsername(username);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Le panier est vide.");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cart.getItems()) {
            Article article = ci.getArticle();
            if (article.getStock() < ci.getQuantity()) {
                throw new IllegalStateException("Stock insuffisant pour : " + article.getName());
            }

            article.setStock(article.getStock() - ci.getQuantity());
            articleRepository.save(article);

            OrderItem oi = OrderItem.builder()
                    .article(article)
                    .quantity(ci.getQuantity())
                    .unitPrice(article.getPrice())
                    .build();
            orderItems.add(oi);

            total = total.add(article.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.EN_ATTENTE)
                .totalAmount(total)
                .items(new ArrayList<>())
                .build();

        Order saved = orderRepository.save(order);
        for (OrderItem oi : orderItems) {
            oi.setOrder(saved);
            saved.getItems().add(oi);
        }

        orderRepository.save(saved);
        cartService.clearCart(cart);
        return orderConverter.toDTO(saved);
    }

    // =========================
    // ADMIN - LIST (FIXED)
    // =========================
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAllAdmin(String search, OrderStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders;

        if (search != null && !search.isBlank() && status != null) {

            orders = orderRepository
                    .findByStatusAndUser_UsernameContainingIgnoreCaseOrderByCreatedAtDesc(
                            status, search, pageable
                    );

        } else if (status != null) {

            orders = orderRepository
                    .findByStatusOrderByCreatedAtDesc(status, pageable);

        } else if (search != null && !search.isBlank()) {

            orders = orderRepository
                    .findByUser_UsernameContainingIgnoreCaseOrderByCreatedAtDesc(
                            search, pageable
                    );

        } else {

            orders = orderRepository
                    .findAllByOrderByCreatedAtDesc(pageable);
        }

        return orders.map(orderConverter::toDTO);
    }

    // =========================
    // FIND BY ID
    // =========================
    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        return orderConverter.toDTO(
                orderRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"))
        );
    }

    // =========================
    // CANCEL ORDER
    // =========================
    public OrderDTO cancelOrder(Long orderId, String username) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));

        if (!order.getUser().getUsername().equals(username)) {
            throw new SecurityException("Accès refusé");
        }

        if (order.getStatus() != OrderStatus.EN_ATTENTE) {
            throw new IllegalStateException("Impossible d'annuler cette commande");
        }

        for (OrderItem item : order.getItems()) {
            Article article = item.getArticle();
            article.setStock(article.getStock() + item.getQuantity());
            articleRepository.save(article);
        }

        order.setStatus(OrderStatus.ANNULEE);

        return orderConverter.toDTO(orderRepository.save(order));
    }

    // =========================
    // UPDATE STATUS (ADMIN)
    // =========================
    public OrderDTO updateStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));

        order.setStatus(status);

        return orderConverter.toDTO(orderRepository.save(order));
    }

    // =========================
    // STATS
    // =========================
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return orderRepository.calculateTotalRevenue();
    }

    @Transactional(readOnly = true)
    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return orderRepository.count();
    }
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAllSafe(OrderStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders;

        if (status == null) {
            orders = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        }

        return orders.map(orderConverter::toDTO);
    }

    // =========================
    // DELETE (ADMIN)
    // =========================
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));

        // Optionnel : rendre le stock si on supprime une commande en attente
        if (order.getStatus() == OrderStatus.EN_ATTENTE) {
            for (OrderItem item : order.getItems()) {
                Article article = item.getArticle();
                article.setStock(article.getStock() + item.getQuantity());
                articleRepository.save(article);
            }
        }

        orderRepository.delete(order);
    }

    // =========================
    // FIND ALL SAFE (FIXED)
    // =========================
    // Cette méthode était vide et renvoyait null, d'où l'erreur Thymeleaf
    public Page<OrderDTO> findAllSafe(Pageable pageable) {
        // On récupère toutes les commandes triées par date (le tri est géré par le Pageable passé par le controller)
        return orderRepository.findAll(pageable)
                .map(orderConverter::toDTO);
    }
}