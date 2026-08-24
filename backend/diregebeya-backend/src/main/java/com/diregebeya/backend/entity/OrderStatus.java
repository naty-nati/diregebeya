package com.diregebeya.backend.entity;

/**
 * No enforced transition graph yet (e.g. blocking DELIVERED -> PENDING) -
 * OrderServiceImpl.updateStatus accepts any value here. A production system
 * would likely model this as an explicit state machine; deferred until a
 * concrete need for it shows up rather than building it speculatively now.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
