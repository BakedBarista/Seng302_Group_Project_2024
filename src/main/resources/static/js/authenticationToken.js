document.addEventListener("DOMContentLoaded", function() {
    console.log('Working...DOM fully loaded and parsed');

    /**
     * Submits form with the full token from concatenating all inputs
     *
     */
    function codeInput() {
        const form = document.getElementById("authenticationForm");
        const inputs = document.querySelectorAll('#token > input'); //Takes all six inputs from different boxes
        let token = document.getElementById("authenticationToken");

        inputs.forEach((input, index) => {
            input.addEventListener('input', (e) => {
                // Stops more than one value per box
                if (input.value.length > input.maxLength) {
                    input.value = input.value.slice(0, 1);
                }

                //Move to the next box if input added
                if (input.value.length >= input.maxLength) {
                    const nextInput = inputs[index + 1];
                    if (nextInput) {
                        nextInput.focus();
                    }
                }
                // Check if all inputs are filled and submits form
                if (inputsFilled()) {
                    token.setAttribute("value", getInputValue());
                    console.log("Token is", token.value)
                    console.log(getInputValue())
                    form.submit();
                }
            });

            //When copy and pasting token code
            //GPT helped
            input.addEventListener('paste', (e) => {
                const copiedTokenString = (e.clipboardData || window.clipboardData).getData('text');
                if (copiedTokenString.length === inputs.length) {
                    copiedTokenString.split('').forEach((char, i) => {
                        inputs[i].value = char;
                    });
                    token.setAttribute("value", getInputValue());
                    form.submit();
                }
                e.preventDefault();
            });

            //Deleting an input
            input.addEventListener('keydown', (e) => {
                if (e.key === "Backspace" && input.value.length === 0) {
                    const previousInput = inputs[index - 1];
                    if (previousInput) {
                        previousInput.focus();
                    }
                }
            });
        });

        /**
         * Checks if all fields are filled
         *
         * @returns {boolean} true if all input fields has input with maximum length
         */
        function inputsFilled() {
            return Array.from(inputs).every(input => input.value.length === input.maxLength);
        }

        /**
         * Concatenates value of all input fields
         *
         * @returns {string} the concatenated value of all input fields
         */
        function getInputValue() {
            return Array.from(inputs).map(input => input.value).join('');
        }
    }
    codeInput();
});