document.addEventListener("DOMContentLoaded", function() {

    document.querySelectorAll('input, textarea').forEach(function(input, index, array) {
        input.addEventListener('keydown', function(event) {
            if (event.key === "Enter") {
                event.preventDefault();
                if (index === array.length - 1) {
                    document.querySelector('form').submit();
                } else {
                    array[index + 1].focus();
                }
            }
        });
    });
});