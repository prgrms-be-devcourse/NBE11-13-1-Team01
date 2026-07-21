const cart = new Map();
const cartList = document.querySelector("#cart-list");
const cartEmpty = document.querySelector("#cart-empty");
const itemInputs = document.querySelector("#order-item-inputs");
const orderTotal = document.querySelector("#order-total");
const checkoutButton = document.querySelector("#checkout-button");
const orderForm = document.querySelector("#order-form");
const orderMessage = document.querySelector("#order-message");

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

orderForm.addEventListener("submit", event => {
    if (cart.size === 0) {
        event.preventDefault();
        orderMessage.textContent = "주문할 상품을 한 개 이상 추가해 주세요.";
    }
});
