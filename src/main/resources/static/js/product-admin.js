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

function resetCurrentImage() {
    productImageFile.value = "";
    deleteProductImage.checked = false;
    currentProductImage.src = "";
    currentImageArea.hidden = true;
}

function resetProductForm() {
    productForm.reset();
    resetCurrentImage();

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

        document.querySelector("#product-name").value =
            button.dataset.name || "";

        document.querySelector("#product-description").value =
            button.dataset.description || "";

        document.querySelector("#product-price").value =
            button.dataset.price || "";

        productStock.value =
            button.dataset.stock || "";

        const imageUrl = button.dataset.imageUrl;

        if (imageUrl) {
            currentProductImage.src = imageUrl;
            currentImageArea.hidden = false;
        }

        productForm.action =
            `/admin/products/${button.dataset.id}`;

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

document.querySelector("#product-form-reset")
    .addEventListener("click", resetProductForm);