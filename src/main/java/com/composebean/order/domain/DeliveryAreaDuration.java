package com.composebean.order.domain;


import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;

import java.util.Arrays;

public enum DeliveryAreaDuration {
    SEOUL(1, "서울"),
    INCHEON(1, "인천"),
    GYEONGGI(1, "경기"),
    SEJONG(1, "세종"),

    // 주요 광역시 및 고속 권역
    DAEJEON(1, "대전"),
    GWANGJU(1, "광주"),
    BUSAN(2, "부산"),
    DAEGU(2, "대구"),
    ULSAN(2, "울산"),

    // 충청/강원 권역
    CHUNGBUK(2, "충북"),
    CHUNGNAM(2, "충남"),
    GANGWON(2, "강원"),

    // 전라/경상 권역
    GYEONGBUK(2, "경북"),
    GYEONGNAM(2, "경남"),
    JEONBUK(2, "전북"),
    JEONNAM(2, "전남"),

    // 특수 지역
    JEJU(4, "제주");

    private final int deliveryDays;
    private final String keyword;

    DeliveryAreaDuration(int deliveryDays, String keyword) {
        this.deliveryDays = deliveryDays;
        this.keyword = keyword;
    }

    // 주소 문자열을 받아서 이 Enum에 해당하는지 확인하는 로직
    public static int getDeliveryDaysByAddress(String address) {

        if (address == null || address.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        return Arrays.stream(values())
                .filter(region -> address.contains(region.keyword))
                .findFirst()
                .map(region -> region.deliveryDays)
                .orElseThrow(()->new BusinessException(ErrorCode.INVALID_REQUEST));
    }
}