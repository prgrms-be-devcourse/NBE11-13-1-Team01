document.querySelectorAll(".delivery-status-select").forEach((select) => {
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
        } catch (error) {
            select.value = previousStatus;
            alert(error.message);
        } finally {
            select.disabled = false;
        }
    });
});
