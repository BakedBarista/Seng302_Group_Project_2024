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

/**
 * Gets the content of a meta tag
 *
 * @param {string} name The name of the meta tag
 * @returns The content of the meta tag
 */
function getMeta(name) {
    /**@type {HTMLMetaElement} */
    const metaElement = document.querySelector(`meta[name="${name}"]`);
    return metaElement?.content;
}

const csrf = getMeta('_csrf');
const csrfHeader = getMeta('_csrf_header');
