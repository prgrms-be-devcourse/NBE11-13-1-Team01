package com.composebean.order.repository;

import com.composebean.order.domain.Order;
import com.composebean.order.domain.QOrder;
import com.composebean.order.domain.QOrderItem;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderItemResponse;
import com.composebean.order.dto.OrderSummaryResponse;
import com.composebean.order.dto.QOrderSummaryResponse;
import com.composebean.product.domain.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QProduct product = QProduct.product;
    private static final QOrder order = QOrder.order;
    private static final QOrderItem orderItem = QOrderItem.orderItem;

    @Override
    public Page<OrderSummaryResponse> getOrders(String email, Pageable pageable) {
        List<OrderSummaryResponse> results = queryFactory.select(
                new QOrderSummaryResponse(
                        order.id,
                        order.email,
                        order.totalPrice,
                        order.paymentStatus,
                        order.deliveryStatus,
                        order.deliveryExpectedDate,
                        order.orderedAt
                )
        ).from(order).where(eqEmail(email)).orderBy(order.orderedAt.desc()).
                offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        //전체 카운팅 쿼리(유틸에서 내부 변수 값 유추에 필수)
        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .where(
                        eqEmail(email)
                );

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqEmail(String email) {
        if (email == null || email.isBlank()) {
            return null; // where절에서 이 조건이 빠지므로 전체 조회가 됨
        }
        return order.email.eq(email);
    }

    @Override
    public Optional<OrderDetailResponse> getOrderDetail(Long orderId) {
        Order findOrder = queryFactory.selectFrom(order)
                .leftJoin(order.orderItems, orderItem).fetchJoin()
                .leftJoin(orderItem.product, product).fetchJoin()
                .where(order.id.eq(orderId))
                .fetchOne();
        if(findOrder == null) {
            return Optional.empty();
        }

        return Optional.of(OrderDetailResponse.from(findOrder));

        /*List<OrderItemResponse> itemResponses = findOrder.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        OrderDetailResponse response = new OrderDetailResponse(
                findOrder.getId(),
                findOrder.getEmail(),
                findOrder.getAddress(),
                findOrder.getPostalCode(),
                findOrder.getTotalPrice(),
                findOrder.getPaymentStatus().name(),
                findOrder.getDeliveryStatus().name(),
                findOrder.getDeliveryExpectedDate().toLocalDate(),
                findOrder.getOrderedAt(),
                itemResponses
        );

        return Optional.of(response);*/
    }
}
