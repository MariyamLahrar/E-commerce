package com.commerce.Online.repository;

import com.commerce.Online.entity.Order;
import com.commerce.Online.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // =========================
    // USER
    // =========================
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // =========================
    // ADMIN - LIST ALL
    // =========================
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // =========================
    // ADMIN - FILTER STATUS
    // =========================
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    // =========================
    // ADMIN - SEARCH USERNAME
    // =========================
    Page<Order> findByUser_UsernameContainingIgnoreCaseOrderByCreatedAtDesc(
            String username,
            Pageable pageable
    );

    // =========================
    // ADMIN - STATUS + SEARCH
    // =========================
    Page<Order> findByStatusAndUser_UsernameContainingIgnoreCaseOrderByCreatedAtDesc(
            OrderStatus status,
            String username,
            Pageable pageable
    );

    // =========================
    // REVENUE GLOBAL
    // =========================
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'VALIDEE'")
    BigDecimal calculateTotalRevenue();

    // =========================
    // REVENUE USER
    // =========================
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'VALIDEE' AND o.user.id = :userId")
    BigDecimal calculateRevenueByUser(@Param("userId") Long userId);

    // =========================
    // COUNT STATUS
    // =========================
    long countByStatus(OrderStatus status);
}