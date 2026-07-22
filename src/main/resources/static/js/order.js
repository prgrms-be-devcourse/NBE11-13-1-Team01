const cart = new Map();
const cartList = document.querySelector("#cart-list");
const cartEmpty = document.querySelector("#cart-empty");
const itemInputs = document.querySelector("#order-item-inputs");
const orderTotal = document.querySelector("#order-total");
const checkoutButton = document.querySelector("#checkout-button");
const orderForm = document.querySelector("#order-form");
const orderMessage = document.querySelector("#order-message");
const deliveryRegion = document.querySelector("#delivery-region");
const deliveryRegionValue = document.querySelector("#delivery-region-value");
const postalCode = document.querySelector("#postal-code");
const roadAddress = document.querySelector("#road-address");
const addressDetail = document.querySelector("#address-detail");
const combinedAddress = document.querySelector("#combined-address");
const addressSearchButton = document.querySelector("#address-search-button");

let roadAddressWithoutRegion = "";

const deliveryRegionValues = {
    "서울": "서울",
    "서울특별시": "서울",
    "부산": "부산",
    "부산광역시": "부산",
    "대구": "대구",
    "대구광역시": "대구",
    "인천": "인천",
    "인천광역시": "인천",
    "광주": "광주",
    "광주광역시": "광주",
    "대전": "대전",
    "대전광역시": "대전",
    "울산": "울산",
    "울산광역시": "울산",
    "세종": "세종",
    "세종특별자치시": "세종",
    "경기": "경기",
    "경기도": "경기",
    "강원": "강원",
    "강원특별자치도": "강원",
    "충북": "충북",
    "충청북도": "충북",
    "충남": "충남",
    "충청남도": "충남",
    "전북": "전북",
    "전북특별자치도": "전북",
    "전남": "전남",
    "전라남도": "전남",
    "경북": "경북",
    "경상북도": "경북",
    "경남": "경남",
    "경상남도": "경남",
    "제주": "제주",
    "제주특별자치도": "제주"
};

document.querySelectorAll(".add-product-button").forEach(button => {
    button.addEventListener("click", () => {
        const id = Number(button.dataset.id);
        const saved = cart.get(id);

        if (saved && saved.quantity >= saved.stock) {
            orderMessage.textContent = "현재 재고보다 많이 담을 수 없습니다.";
            return;
        }

        cart.set(id, {
            id: id,
            name: button.dataset.name,
            price: Number(button.dataset.price),
            stock: Number(button.dataset.stock),
            quantity: saved ? saved.quantity + 1 : 1
        });

        orderMessage.textContent = "";
        renderCart();
    });
});

function changeQuantity(id, amount) {
    const item = cart.get(id);
    const nextQuantity = item.quantity + amount;

    if (nextQuantity < 1) {
        cart.delete(id);
    } else if (nextQuantity <= item.stock) {
        item.quantity = nextQuantity;
    } else {
        orderMessage.textContent = "현재 재고보다 많이 담을 수 없습니다.";
    }

    renderCart();
}

function renderCart() {
    cartList.innerHTML = "";
    itemInputs.innerHTML = "";
    let total = 0;

    Array.from(cart.values()).forEach((item, index) => {
        total += item.price * item.quantity;

        const row = document.createElement("div");
        row.className = "cart-item";
        row.innerHTML = `
            <strong>${item.name}</strong>
            <div class="cart-controls">
                <button type="button" data-action="minus">−</button>
                <span>${item.quantity}개</span>
                <button type="button" data-action="plus">+</button>
            </div>
            <button type="button" class="cart-remove">삭제</button>
        `;

        row.querySelector('[data-action="minus"]').addEventListener("click", () => changeQuantity(item.id, -1));
        row.querySelector('[data-action="plus"]').addEventListener("click", () => changeQuantity(item.id, 1));
        row.querySelector(".cart-remove").addEventListener("click", () => {
            cart.delete(item.id);
            renderCart();
        });
        cartList.appendChild(row);

        itemInputs.insertAdjacentHTML("beforeend", `
            <input type="hidden" name="items[${index}].productId" value="${item.id}">
            <input type="hidden" name="items[${index}].quantity" value="${item.quantity}">
        `);
    });

    cartEmpty.hidden = cart.size > 0;
    checkoutButton.disabled = cart.size === 0;
    orderTotal.textContent = `${total.toLocaleString("ko-KR")}원`;
}

function updateCombinedAddress() {
    combinedAddress.value = `${roadAddressWithoutRegion} ${addressDetail.value}`.trim();
}

addressSearchButton.addEventListener("click", () => {
    if (typeof kakao === "undefined" || !kakao.Postcode) {
        orderMessage.textContent = "주소 검색 서비스를 불러오지 못했습니다.";
        return;
    }

    new kakao.Postcode({
        oncomplete: data => {
            const selectedAddress = data.roadAddress || data.jibunAddress;
            const regionValue = deliveryRegionValues[data.sido];

            postalCode.value = data.zonecode;
            roadAddress.value = selectedAddress;
            roadAddressWithoutRegion = selectedAddress.replace(/^\S+\s*/, "");
            deliveryRegion.value = "";
            deliveryRegionValue.value = "";

            if (regionValue) {
                deliveryRegion.value = regionValue;
                deliveryRegionValue.value = regionValue;
            } else {
                orderMessage.textContent = "배송 지역을 확인할 수 없는 주소입니다.";
                return;
            }

            updateCombinedAddress();
            orderMessage.textContent = "";
            addressDetail.focus();
        }
    }).open({
        popupKey: "composebean-address"
    });
});

addressDetail.addEventListener("input", updateCombinedAddress);

orderForm.addEventListener("submit", event => {
    if (cart.size === 0) {
        event.preventDefault();
        orderMessage.textContent = "주문할 상품을 한 개 이상 추가해 주세요.";
        return;
    }

    if (!roadAddress.value || !postalCode.value || !deliveryRegionValue.value) {
        event.preventDefault();
        orderMessage.textContent = "주소 찾기를 이용해 배송지를 선택해 주세요.";
        return;
    }

});
