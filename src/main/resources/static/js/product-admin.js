const productForm = document.querySelector("#product-form");
const productFormTitle = document.querySelector("#product-form-title");
const productSubmit = document.querySelector("#product-submit");
const initialStockField = document.querySelector("#initial-stock-field");
const productStock = document.querySelector("#product-stock");
const productStockLabel = document.querySelector("#product-stock-label");
const productImageFile = document.querySelector("#product-image-file");

const currentImageArea = document.querySelector("#current-image-area");
const currentProductImage = document.querySelector("#current-product-image");
const deleteProductImage = document.querySelector("#delete-product-image");

let editingProductId = null;

function resetCurrentImage() {
    productImageFile.value = "";
    deleteProductImage.checked = false;
    currentProductImage.src = "";
    currentImageArea.hidden = true;
}

function resetProductForm() {
    productForm.reset();
    resetCurrentImage();
    editingProductId = null;

    productForm.action = "/admin/products";
    productFormTitle.textContent = "상품 등록";
    productSubmit.textContent = "등록하기";
    initialStockField.hidden = false;
    productStock.required = true;
    productStockLabel.textContent = "초기 재고 *";
}

document.querySelectorAll(".edit-product-button").forEach(button => {
    button.addEventListener("click", () => {
        resetCurrentImage();
        editingProductId = Number(button.dataset.id);

        document.querySelector("#product-name").value =
            button.dataset.name || "";
        document.querySelector("#product-description").value =
            button.dataset.description || "";
        document.querySelector("#product-price").value =
            button.dataset.price;
        productStock.value = button.dataset.stock;

        if (button.dataset.imageUrl) {
            currentProductImage.src = button.dataset.imageUrl;
            currentImageArea.hidden = false;
        }

        productForm.action = `/admin/products/${editingProductId}`;
        productFormTitle.textContent = "상품 수정";
        productSubmit.textContent = "수정하기";
        initialStockField.hidden = false;
        productStock.required = true;
        productStockLabel.textContent = "재고 수정 *";

        productForm.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    });
});

productImageFile.addEventListener("change", () => {
    if (productImageFile.files.length > 0) {
        deleteProductImage.checked = false;
    }
});

deleteProductImage.addEventListener("change", () => {
    if (deleteProductImage.checked) {
        productImageFile.value = "";
    }
});

productForm.addEventListener("submit", async event => {
    if (editingProductId === null) {
        return;
    }

    event.preventDefault();
    productSubmit.disabled = true;

    try {
        const response = await fetch(
            `/api/products/${editingProductId}/stock`,
            {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    stockQuantity: Number(productStock.value)
                })
            }
        );

        if (!response.ok) {
            throw new Error("상품 재고 수정에 실패했습니다.");
        }

        productForm.submit();
    } catch (error) {
        alert(error.message);
        productSubmit.disabled = false;
    }
});

document.querySelector("#product-form-reset")
    .addEventListener("click", resetProductForm);
