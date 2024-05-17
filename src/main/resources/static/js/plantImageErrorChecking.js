
/** @type {HTMLInputElement} */
const plantImageInput = document.getElementById(
    "image"
);
/** @type {HTMLSpanElement} */
const plantImageError = document.getElementById(
    "plantImageError"
);

const VALID_MIME_TYPES = ["image/png", "image/jpeg", "image/svg+xml"];
let hasError = false;


plantImageInput.addEventListener("change", (ev) => {
    const file = plantImageInput.files[0];
    hasError = false;
    if (!file) {
        return;
    }

    plantImageError.textContent = "";
    plantImageError.style.display = "none";


    if (!VALID_MIME_TYPES.includes(file.type)) {
        plantImageError.style.display = "block";
        plantImageError.textContent =
            "Image must be of type png, jpg or svg";
        hasError = true;
        plantImageInput.value = null;
        return;
    }

    if (file.size > 10 * 1024 * 1024) {
        plantImageError.style.display = "block";
        plantImageError.textContent =
            "File size must be less than 10MB";
        hasError = true;
        plantImageInput.value = null;
        return;
    }
    const reader = new FileReader();
    reader.onload = function () {
        const preview = document.getElementById("preview");
        preview.src = reader.result;
        preview.style.display = "block";
    };
    reader.readAsDataURL(file);
});