document.addEventListener('DOMContentLoaded', function() {
    const imageContainer = document.querySelector(".image-container");
    const previewImage = imageContainer.querySelector("#preview-image");
    const imageInput = imageContainer.querySelector("#image");
    const VALID_MIME_TYPES = ["image/png", "image/jpeg", "image/svg+xml"];

    imageInput.addEventListener("change", function(ev) {
        const file = imageInput.files[0];
        if (!file) {
            return;
        }

        if (!VALID_MIME_TYPES.includes(file.type)) {
            alert("Image must be of type png, jpg, or svg");
            return;
        }

        if (file.size > 10 * 1024 * 1024) {
            alert("File size must be less than 10MB");
            return;
        }

        previewImage.src = URL.createObjectURL(file);
    });
});