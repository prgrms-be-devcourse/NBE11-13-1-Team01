const STATUS_CLASSES = [
    "status-preparing",
    "status-shipping",
    "status-delivered"
];

function updateStatusClass(select) {
    select.classList.remove(...STATUS_CLASSES);

    switch (select.value) {
        case "PREPARING":
            select.classList.add("status-preparing");
            break;
        case "SHIPPING":
            select.classList.add("status-shipping");
            break;
        case "DELIVERED":
            select.classList.add("status-delivered");
            break;
    }
}

document.querySelectorAll(".delivery-status-select").forEach((select) => {
    updateStatusClass(select);

    select.addEventListener("change", async () => {
        const orderId = select.dataset.orderId;
        const previousStatus = Array.from(select.options)
            .find((option) => option.defaultSelected)?.value;

        select.disabled = true;

        try {
            const response = await fetch(`/api/orders/${orderId}/delivery-status`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    deliveryStatus: select.value
                })
            });

            if (!response.ok) {
                throw new Error("배송 상태를 변경하지 못했습니다.");
            }

            Array.from(select.options).forEach((option) => {
                option.defaultSelected = option.value === select.value;
            });

            updateStatusClass(select);
        } catch (error) {
            select.value = previousStatus;
            updateStatusClass(select);
            alert(error.message);
        } finally {
            select.disabled = false;
        }
    });
});