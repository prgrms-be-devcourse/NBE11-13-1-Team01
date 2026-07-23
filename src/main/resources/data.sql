-- Compose Bean 초기 데이터
-- 동일한 데이터가 이미 존재하면 다시 삽입하지 않도록 작성한 MySQL용 스크립트다.
-- 실행 순서: products -> orders -> order_items

-- =========================================================
-- 1. 상품 초기 데이터
-- =========================================================

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '브라질 세하 두 카파라오 원두',
    20000,
    '고소한 견과류 향과 부드러운 초콜릿 풍미가 조화로운 브라질 원두',
    '/images/products/brazil_serra_do_caparao_beans.png',
    40,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '브라질 세하 두 카파라오 원두'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '콜롬비아 나리뇨 원두',
    19000,
    '은은한 단맛과 산뜻한 산미가 균형을 이루는 콜롬비아 원두',
    '/images/products/colombia_narino_beans.png',
    55,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '콜롬비아 나리뇨 원두'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '콜롬비아 킨디오 원두',
    18000,
    '캐러멜의 단맛과 깔끔한 바디감이 특징인 콜롬비아 원두',
    '/images/products/colombia_quindio_beans.png',
    60,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '콜롬비아 킨디오 원두'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '에티오피아 시다모 원두',
    17000,
    '화사한 꽃향과 과일 산미가 돋보이는 에티오피아 원두',
    '/images/products/ethiopia_sidamo_beans.png',
    45,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '에티오피아 시다모 원두'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '초콜릿 소스',
    9000,
    '카페모카와 디저트 음료에 활용할 수 있는 진한 초콜릿 소스',
    '/images/products/chocolate_sauce.png',
    80,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '초콜릿 소스'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '우유',
    3500,
    '라떼와 다양한 음료 제조에 사용하는 신선한 우유',
    '/images/products/milk.png',
    100,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '우유'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '종이컵',
    6000,
    '따뜻한 음료 제공에 적합한 테이크아웃용 종이컵 세트',
    '/images/products/paper_cup.png',
    120,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '종이컵'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '플라스틱컵',
    7000,
    '아이스 음료 제공에 적합한 투명 테이크아웃 컵 세트',
    '/images/products/plastic_cup.png',
    110,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '플라스틱컵'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '테이크아웃 컵 리드',
    5000,
    '테이크아웃 컵에 사용할 수 있는 음용형 컵 리드 세트',
    '/images/products/sip_lid.png',
    150,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '테이크아웃 컵 리드'
);

INSERT INTO products (
    name, price, description, image_url, stock_quantity, created_at, updated_at
)
SELECT
    '바닐라 시럽',
    8500,
    '라떼와 디저트 음료에 은은한 단맛을 더하는 바닐라 시럽',
    '/images/products/vanilla_syrup.png',
    75,
    NOW(),
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = '바닐라 시럽'
);

-- =========================================================
-- 2. 주문 초기 데이터
-- 주문의 중복 여부는 이메일 + 주소 + 주문 시각으로 판별한다.
-- =========================================================

-- 같은 이메일·같은 주소·같은 처리 구간 주문 3건
-- 스케줄러 그룹화 및 병합 확인용
INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'composebean@programmers.co.kr',
    '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)',
    '06671',
    28500,
    'PAID',
    'PREPARING',
    '2026-07-24 18:00:00',
    '2026-07-23 09:10:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'composebean@programmers.co.kr'
      AND address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
      AND ordered_at = '2026-07-23 09:10:00'
);

INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'composebean@programmers.co.kr',
    '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)',
    '06671',
    38000,
    'PAID',
    'PREPARING',
    '2026-07-24 18:00:00',
    '2026-07-23 10:20:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'composebean@programmers.co.kr'
      AND address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
      AND ordered_at = '2026-07-23 10:20:00'
);

INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'composebean@programmers.co.kr',
    '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)',
    '06671',
    33000,
    'PAID',
    'PREPARING',
    '2026-07-24 18:00:00',
    '2026-07-23 11:40:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'composebean@programmers.co.kr'
      AND address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
      AND ordered_at = '2026-07-23 11:40:00'
);

-- 이메일은 같지만 주소가 다른 주문
INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'composebean@programmers.co.kr',
    '서울특별시 마포구 양화로 160, 5층',
    '04050',
    17000,
    'PAID',
    'SHIPPING',
    '2026-07-24 18:00:00',
    '2026-07-23 12:15:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'composebean@programmers.co.kr'
      AND address = '서울특별시 마포구 양화로 160, 5층'
      AND ordered_at = '2026-07-23 12:15:00'
);

-- 주소는 같지만 이메일이 다른 주문
INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'mentor@example.com',
    '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)',
    '06671',
    27000,
    'PAID',
    'PREPARING',
    '2026-07-24 18:00:00',
    '2026-07-23 13:00:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'mentor@example.com'
      AND address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
      AND ordered_at = '2026-07-23 13:00:00'
);

-- 부산 지역 주문: 지역별 배송 소요일 확인용
INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'busan.cafe@example.com',
    '부산광역시 해운대구 센텀중앙로 97, 1층',
    '48058',
    28000,
    'PAID',
    'SHIPPING',
    '2026-07-25 18:00:00',
    '2026-07-23 15:10:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'busan.cafe@example.com'
      AND address = '부산광역시 해운대구 센텀중앙로 97, 1층'
      AND ordered_at = '2026-07-23 15:10:00'
);

-- 배송 완료 주문
INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'completed@example.com',
    '대전광역시 유성구 대학로 99, 2층',
    '34134',
    37000,
    'PAID',
    'DELIVERED',
    '2026-07-22 18:00:00',
    '2026-07-21 10:30:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'completed@example.com'
      AND address = '대전광역시 유성구 대학로 99, 2층'
      AND ordered_at = '2026-07-21 10:30:00'
);

-- 배송 중 주문
INSERT INTO orders (
    email, address, postal_code, total_price,
    payment_status, delivery_status,
    delivery_expected_date, ordered_at, deleted_at
)
SELECT
    'shipping@example.com',
    '경기도 성남시 분당구 판교역로 166, 3층',
    '13529',
    36000,
    'PAID',
    'SHIPPING',
    '2026-07-24 18:00:00',
    '2026-07-23 16:20:00',
    NULL
    WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE email = 'shipping@example.com'
      AND address = '경기도 성남시 분당구 판교역로 166, 3층'
      AND ordered_at = '2026-07-23 16:20:00'
);

-- =========================================================
-- 3. 주문 상품 초기 데이터
-- 주문과 상품을 고정 ID가 아닌 조회 조건으로 연결한다.
-- 동일 주문에 동일 상품이 이미 있으면 다시 삽입하지 않는다.
-- =========================================================

-- 주문 1: 브라질 원두 1개 + 바닐라 시럽 1개 = 28,500원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 20000, 20000
FROM orders o
         JOIN products p ON p.name = '브라질 세하 두 카파라오 원두'
WHERE o.email = 'composebean@programmers.co.kr'
  AND o.address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
  AND o.ordered_at = '2026-07-23 09:10:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 8500, 8500
FROM orders o
         JOIN products p ON p.name = '바닐라 시럽'
WHERE o.email = 'composebean@programmers.co.kr'
  AND o.address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
  AND o.ordered_at = '2026-07-23 09:10:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

-- 주문 2: 콜롬비아 나리뇨 원두 2개 = 38,000원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 2, 19000, 38000
FROM orders o
         JOIN products p ON p.name = '콜롬비아 나리뇨 원두'
WHERE o.email = 'composebean@programmers.co.kr'
  AND o.address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
  AND o.ordered_at = '2026-07-23 10:20:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

-- 주문 3: 종이컵 3개 + 컵 리드 3개 = 33,000원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 3, 6000, 18000
FROM orders o
         JOIN products p ON p.name = '종이컵'
WHERE o.email = 'composebean@programmers.co.kr'
  AND o.address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
  AND o.ordered_at = '2026-07-23 11:40:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 3, 5000, 15000
FROM orders o
         JOIN products p ON p.name = '테이크아웃 컵 리드'
WHERE o.email = 'composebean@programmers.co.kr'
  AND o.address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
  AND o.ordered_at = '2026-07-23 11:40:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

-- 주문 4: 에티오피아 시다모 원두 1개 = 17,000원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 17000, 17000
FROM orders o
         JOIN products p ON p.name = '에티오피아 시다모 원두'
WHERE o.email = 'composebean@programmers.co.kr'
  AND o.address = '서울특별시 마포구 양화로 160, 5층'
  AND o.ordered_at = '2026-07-23 12:15:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

-- 주문 5: 콜롬비아 킨디오 원두 1개 + 초콜릿 소스 1개 = 27,000원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 18000, 18000
FROM orders o
         JOIN products p ON p.name = '콜롬비아 킨디오 원두'
WHERE o.email = 'mentor@example.com'
  AND o.address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
  AND o.ordered_at = '2026-07-23 13:00:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 9000, 9000
FROM orders o
         JOIN products p ON p.name = '초콜릿 소스'
WHERE o.email = 'mentor@example.com'
  AND o.address = '서울특별시 서초구 반포대로 45, 4층(서초동, 명정빌딩)'
  AND o.ordered_at = '2026-07-23 13:00:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

-- 주문 6: 우유 4개 + 플라스틱컵 2개 = 28,000원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 4, 3500, 14000
FROM orders o
         JOIN products p ON p.name = '우유'
WHERE o.email = 'busan.cafe@example.com'
  AND o.address = '부산광역시 해운대구 센텀중앙로 97, 1층'
  AND o.ordered_at = '2026-07-23 15:10:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 2, 7000, 14000
FROM orders o
         JOIN products p ON p.name = '플라스틱컵'
WHERE o.email = 'busan.cafe@example.com'
  AND o.address = '부산광역시 해운대구 센텀중앙로 97, 1층'
  AND o.ordered_at = '2026-07-23 15:10:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

-- 주문 7: 브라질 원두 1개 + 에티오피아 원두 1개 = 37,000원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 20000, 20000
FROM orders o
         JOIN products p ON p.name = '브라질 세하 두 카파라오 원두'
WHERE o.email = 'completed@example.com'
  AND o.address = '대전광역시 유성구 대학로 99, 2층'
  AND o.ordered_at = '2026-07-21 10:30:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 17000, 17000
FROM orders o
         JOIN products p ON p.name = '에티오피아 시다모 원두'
WHERE o.email = 'completed@example.com'
  AND o.address = '대전광역시 유성구 대학로 99, 2층'
  AND o.ordered_at = '2026-07-21 10:30:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

-- 주문 8: 콜롬비아 나리뇨 원두 1개 + 바닐라 시럽 2개 = 36,000원
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 1, 19000, 19000
FROM orders o
         JOIN products p ON p.name = '콜롬비아 나리뇨 원두'
WHERE o.email = 'shipping@example.com'
  AND o.address = '경기도 성남시 분당구 판교역로 166, 3층'
  AND o.ordered_at = '2026-07-23 16:20:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 2, 8500, 17000
FROM orders o
         JOIN products p ON p.name = '바닐라 시럽'
WHERE o.email = 'shipping@example.com'
  AND o.address = '경기도 성남시 분당구 판교역로 166, 3층'
  AND o.ordered_at = '2026-07-23 16:20:00'
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    WHERE oi.order_id = o.id
      AND oi.product_id = p.id
);