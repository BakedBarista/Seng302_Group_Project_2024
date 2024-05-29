"use strict";

let locationMatch = false;
/**
 * Initializes an address autocomplete feature on the specified container element.
 *
 * @param {HTMLElement} containerElement - The container element where the autocomplete feature is added.
 * @param {(result: object | string) => void} callback - The callback function to be called when an option is selected. Called with the option object if a match was found, otherwise called with the input value.
 * @param {{ apiUrl: string, notFoundMessageHtml: string, placeholder: string, acceptButton: boolean, appendUserInput: boolean, allowNonExistent?: boolean }} options - Additional named options. All of these are required.
 * @returns An object with methods that can be used to interact with the autocomplete feature.
 */
function autocomplete(containerElement, callback, options) {

    const MIN_ADDRESS_LENGTH = 3;
    const DEBOUNCE_DELAY = 300;
    let debounceTimer;

    // create container for input element
    const inputContainerElement = document.createElement("div");
    inputContainerElement.className = "input-container";
    containerElement.appendChild(inputContainerElement);

    // create input element
    const inputElement = document.createElement("input");
    inputElement.className = 'form-control';
    inputElement.type = "text";
    inputElement.placeholder = options.placeholder;
    inputElement.className = "form-control";
    inputContainerElement.appendChild(inputElement);

    // add input field clear button
    const acceptButton = document.createElement("div");
    acceptButton.className = "accept-button";
    addAcceptIcon(acceptButton);
    acceptButton.addEventListener("click", (e) => {
        e.stopPropagation();
        acceptFocusedItem();
    });
    if (options.acceptButton) {
        inputContainerElement.appendChild(acceptButton);
    }

    // add input field clear button
    const clearButton = document.createElement("div");
    clearButton.className = "clear-button";
    addIcon(clearButton);
    clearButton.addEventListener("click", (e) => {
        e.stopPropagation();
        clear();
    });
    inputContainerElement.appendChild(clearButton);

    /* We will call the API with a timeout to prevent unnecessary API activity.*/
    let currentTimeout;

    /* Save the current request promise reject function. To be able to cancel the promise when a new request comes */
    let currentPromiseReject;

    /* Save the current items from the API response */
    let currentItems = [];

    /* Focused item in the autocomplete list. This variable is used to navigate with buttons */
    let focusedItemIndex;

    /* Process a user input: */
    inputElement.addEventListener("input", function(input) {
        const currentValue = this.value;

        /* Close any already open dropdown list */
        closeDropDownList();

        clearTimeout(debounceTimer);


        // Cancel previous timeout
        if (currentTimeout) {
            clearTimeout(currentTimeout);
        }

        // Cancel previous request promise
        if (currentPromiseReject) {
            currentPromiseReject({
                cancelled: true
            });
        }

        if (currentValue) {
            acceptButton.classList.add("visible");
            clearButton.classList.add("visible");
        } else {
            acceptButton.classList.remove("visible");
            clearButton.classList.remove("visible");
        }


        // Skip empty or short address strings
        if (!currentValue || currentValue.length < MIN_ADDRESS_LENGTH) {
            return false;
        }


        /* Create a new promise and send geocoding request */
        const promise = new Promise((resolve, reject) => {
            currentPromiseReject = reject;

            debounceTimer = setTimeout(() => {
                fetch(`${options.apiUrl}?currentValue=${currentValue}`)
                    .then(response => {
                        currentPromiseReject = null;

                        // check if the call was successful
                        if (response.ok) {
                            response.json().then(data => resolve(data));
                        } else {
                            response.json().then(data => reject(data));
                        }
                    });
            }, DEBOUNCE_DELAY);
        });

        promise.then((data) => {
            // here we get address suggestions
            currentItems = data.results;

            // handles no location match
            const noLocationMatch = document.createElement("div");
            noLocationMatch.innerHTML = options.notFoundMessageHtml + (options.appendUserInput ? '"' + currentValue + '"': "");

            // create a DIV element that will contain the items (values)
            const autocompleteItemsElement = document.createElement("div");
            autocompleteItemsElement.className = "autocomplete-items";
            inputContainerElement.appendChild(autocompleteItemsElement);

            const resultsExist = data.results.length !== 0;
            if (resultsExist) {
                locationMatch = true;
                /* For each item in the results */
                data.results.forEach((result, index) => {
                    /* Create a DIV element for each element: */
                    const itemElement = document.createElement("div");
                    /* Set formatted address as item value */
                    itemElement.innerHTML = result.formatted;
                    autocompleteItemsElement.appendChild(itemElement);

                    /* Set the value for the autocomplete text field and notify: */
                    itemElement.addEventListener("click", () => {
                        inputElement.value = currentItems[index].formatted;
                        callback(currentItems[index]);
                        /* Close the list of autocompleted values: */
                        closeDropDownList();
                    });
                });
            } else {
                autocompleteItemsElement.appendChild(noLocationMatch);
                const itemElement = document.createElement("div");

                /* Set the value for the autocomplete text field and notify: */
                noLocationMatch.addEventListener("click", () => {
                    currentItems = inputElement.value;

                    callback(inputElement.value);
                    /* Close the list of autocompleted values: */
                    closeDropDownList();
                });
            }
        }, (err) => {
            if (!err.cancelled) {
                console.log(err);
            }
        });
    });

    /**
     * Add support for keyboard navigation
     * */
    inputElement.addEventListener("keydown", function(e) {
        const autocompleteItemsElement = containerElement.querySelector(".autocomplete-items");
        if (autocompleteItemsElement) {
            const itemElements = autocompleteItemsElement.getElementsByTagName("div");
            if (e.keyCode === 40) { // Down arrow
                e.preventDefault();
                /*If the arrow DOWN key is pressed, increase the focusedItemIndex variable:*/
                focusedItemIndex = focusedItemIndex !== itemElements.length - 1 ? focusedItemIndex + 1 : 0;
                /*and make the current item more visible:*/
                setActive(itemElements, focusedItemIndex);
            } else if (e.keyCode === 38) { // Up arrow
                e.preventDefault();

                /*If the arrow UP key is pressed, decrease the focusedItemIndex variable:*/
                focusedItemIndex = focusedItemIndex !== 0 ? focusedItemIndex - 1 : focusedItemIndex = (itemElements.length - 1);
                /*and make the current item more visible:*/
                setActive(itemElements, focusedItemIndex);
            } else if (e.keyCode === 13) { // Enter key
                /* If the ENTER key is pressed and value is selected, accept the selection and close the list*/
                e.preventDefault();
                if (focusedItemIndex > -1 || options.acceptButton) {
                    acceptFocusedItem();
                }
            }
        } else {
            if (e.keyCode === 40) { // Down arrow
                /* Open dropdown list again */
                const event = document.createEvent('Event');
                event.initEvent('input', true, true);
                inputElement.dispatchEvent(event);
            }
        }
    });

    /**
     * Setting the item in the autocomplete dropdown list
     *
     * @param items - An array of items div elements in the dropdown
     * @param {number} index -  The index of the item
     * @returns {boolean} - Returns false if there are no items, otherwise true
     */
    function setActive(items, index) {
        if (!items || !items.length) return false;

        for (let i = 0; i < items.length; i++) {
            items[i].classList.remove("autocomplete-active");
        }

        /* Add class "autocomplete-active" to the active element*/
        items[index].classList.add("autocomplete-active");
    }

    /**
     * Accepts the focused item in the autocomplete dropdown list
     */
    function acceptFocusedItem() {
        const currentItem = currentItems[focusedItemIndex];
        if (currentItem) {
            inputElement.value = currentItem.formatted;
            callback(currentItem);
        } else {
            callback(inputElement.value);
        }
        closeDropDownList();
    }

    /**
     * Clears the input field and closes the dropdown list
     */
    function clear() {
        inputElement.value = '';
        acceptButton.classList.remove("visible");
        clearButton.classList.remove("visible");
        closeDropDownList();
    }

    /**
     * Sets focus on an element and closes the dropdown list
     */
    function focus() {
        inputElement.focus();
        closeDropDownList();
    }

    // Close the dropdown list
    function closeDropDownList() {
        const autocompleteItemsElement = inputContainerElement.querySelector(".autocomplete-items");
        if (autocompleteItemsElement) {
            inputContainerElement.removeChild(autocompleteItemsElement);
        }

        focusedItemIndex = -1;
    }

    // Add an icon to the button
    function addIcon(buttonElement) {
        const svgElement = document.createElementNS("http://www.w3.org/2000/svg", 'svg');
        svgElement.setAttribute('viewBox', "0 0 24 24");
        svgElement.setAttribute('height', "24");

        const iconElement = document.createElementNS("http://www.w3.org/2000/svg", 'path');
        iconElement.setAttribute("d", "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z");
        iconElement.setAttribute('fill', 'currentColor');
        svgElement.appendChild(iconElement);
        buttonElement.appendChild(svgElement);
    }

    /**
     * Add an accept icon to the button
     */
    function addAcceptIcon(buttonElement) {
        const svgElement = document.createElementNS("http://www.w3.org/2000/svg", 'svg');
        svgElement.setAttribute('viewBox', "0 0 24 24");
        svgElement.setAttribute('height', "24");

        const iconElement = document.createElementNS("http://www.w3.org/2000/svg", 'path');
        iconElement.setAttribute("d", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        iconElement.setAttribute('fill', 'currentColor');
        svgElement.appendChild(iconElement);
        buttonElement.appendChild(svgElement);
    }

    /* Close the autocomplete dropdown when the document is clicked.
      Skip, when a user clicks on the input field */
    document.addEventListener("click", function(e) {
        if (e.target !== inputElement) {
            closeDropDownList();
        } else if (!containerElement.querySelector(".autocomplete-items")) {
            // open dropdown list again
            const event = document.createEvent('Event');
            event.initEvent('input', true, true);
            inputElement.dispatchEvent(event);
        }
    });

    // Return an object with the desired methods
    return { clear, focus, inputElement };
}
