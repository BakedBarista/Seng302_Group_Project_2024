/**
 * Prevent form submission when pressing enter while an input is focused
 */
window.addEventListener("load", () => {
    /**@type {NodeListOf<HTMLInputElement>} */
    const inputs = document.querySelectorAll("form input");

    for (const input of inputs) {
        input.addEventListener("keydown", (ev) => {
            if (ev.key === "Enter") {
                ev.preventDefault();
            }
        });
    }
});
