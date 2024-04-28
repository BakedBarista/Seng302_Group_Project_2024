/**
 * Prevent form submission when pressing enter while an input is focused
 */
window.addEventListener("load", () => {
    /**@type {NodeListOf<HTMLFormElement>} */
    const forms = document.querySelectorAll("form");

    for (const form of forms) {
        /**@type {NodeListOf<HTMLInputElement>} */
        const inputs = form.querySelectorAll("form input");
        for (const input of Array.from(inputs).slice(0, -1)) {
            input.addEventListener("keydown", (ev) => {
                if (ev.key === "Enter") {
                    ev.preventDefault();
                }
            });
        }
    }

});
