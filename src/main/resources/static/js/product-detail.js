const productDetail = document.querySelector("#product-detail");
const productDetailMessage = document.querySelector("#product-detail-message");
const productDetailImage = document.querySelector("#product-detail-image");
const productDetailPlaceholder = document.querySelector("#product-detail-placeholder");
const productDetailName = document.querySelector("#product-detail-name");
const productDetailDescription = document.querySelector("#product-detail-description");
const productDetailPrice = document.querySelector("#product-detail-price");
const productDetailStock = document.querySelector("#product-detail-stock");

const pathParts = window.location.pathname.split("/").filter(Boolean);
const productId = pathParts[pathParts.length - 1];

async function loadProductDetail() {
    try {
        const response = await fetch(`/api/products/${productId}`);

        if (!response.ok) {
            throw new Error("상품 상세 정보를 불러오지 못했습니다.");
        }

        const product = await response.json();

        productDetailName.textContent = product.name;
        productDetailDescription.textContent =
            product.description || "등록된 상품 설명이 없습니다.";
        productDetailPrice.textContent =
            `${Number(product.price).toLocaleString("ko-KR")}원`;
        productDetailStock.textContent = `${product.stockQuantity}개`;

        if (product.imageUrl) {
            productDetailImage.src = product.imageUrl;
            productDetailImage.alt = product.name;
            productDetailImage.hidden = false;
            productDetailPlaceholder.hidden = true;
        }

        productDetailMessage.hidden = true;
        productDetail.hidden = false;
    } catch (error) {
        productDetailMessage.textContent = error.message;
    }
}

loadProductDetail();
