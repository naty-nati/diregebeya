package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.admin.AdminStatsResponse;
import com.diregebeya.backend.entity.OrderStatus;
import com.diregebeya.backend.repository.CategoryRepository;
import com.diregebeya.backend.repository.OrderRepository;
import com.diregebeya.backend.repository.ProductRepository;
import com.diregebeya.backend.repository.UserRepository;
import com.diregebeya.backend.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsServiceImpl implements AdminStatsService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    @Override
    public AdminStatsResponse getStats() {
        // Five small count(*) queries, one per OrderStatus, rather than a
        // single GROUP BY - there are only 5 possible statuses, so this
        // stays readable; a GROUP BY projection would earn its complexity
        // once the breakdown needed grouping by something with real
        // cardinality (e.g. per-day).
        Map<OrderStatus, Long> ordersByStatus = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status, orderRepository.countByStatus(status));
        }

        return AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalProducts(productRepository.count())
                .totalCategories(categoryRepository.count())
                .totalOrders(orderRepository.count())
                .totalRevenue(orderRepository.sumTotalAmountByStatusNot(OrderStatus.CANCELLED))
                .ordersByStatus(ordersByStatus)
                .build();
    }
}
