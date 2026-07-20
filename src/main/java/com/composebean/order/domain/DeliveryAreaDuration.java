package com.composebean.order.domain;


import java.util.Arrays;

public enum DeliveryAreaDuration {
    GYEONGGI(1, "경기"),
    CHUNGBUK(2, "충북"),
    CHUNGNAM(2, "충남"),
    GYEONGBUK(3, "경북"),
    GYEONGNAM(4, "경남"),
    JEONBUK(3, "전북"),
    JEONNAM(4, "전남"),
    GANGWON(2, "강원");

    private final int deliveryDays;
    private final String keyword;

    DeliveryAreaDuration(int deliveryDays, String keyword) {
        this.deliveryDays = deliveryDays;
        this.keyword = keyword;
    }

    // 주소 문자열을 받아서 이 Enum에 해당하는지 확인하는 로직
    public static int getDeliveryDaysByAddress(String address) {
        return Arrays.stream(values())
                .filter(region -> address.contains(region.keyword))
                .findFirst()
                .map(region -> region.deliveryDays)
                .orElse(3); // 매칭되는 게 없으면 기본 3일 나중에 오류 던지기 로 바꾸기
    }
}