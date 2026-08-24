package com.diregebeya.backend.repository;

import com.diregebeya.backend.entity.Order;
import com.diregebeya.backend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Overrides the inherited findAll(Pageable) to eager-fetch the
     * customer alongside each order - the admin listing always needs
     * user.fullName/email, so this avoids an N+1 lazy load per row.
     */
    @EntityGraph(attributePaths = "user")
    @Override
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    /**
     * Derived query method names can express count/exists but not SUM -
     * that requires an explicit JPQL aggregate. COALESCE guards against a
     * null result (SUM over zero rows) so callers get 0, not a NullPointerException.
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status <> :excludedStatus")
    BigDecimal sumTotalAmountByStatusNot(@Param("excludedStatus") OrderStatus excludedStatus);
}
