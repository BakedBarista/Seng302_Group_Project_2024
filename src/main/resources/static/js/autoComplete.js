"use strict";

let locationMatch = false;
/**
 * Initializes an address autocomplete feature on the specified container element.
 *
 * @param {HTMLElement} containerElement - The container element where the autocomplete feature is added.
 * @param {Function} callback - The callback function to be called when an address is selected.
 * @param {string} apiUrl - The URL of the API to call for suggestions.
 */
function autocomplete(containerElement, callback, apiUrl) {

    const MIN_ADDRESS_LENGTH = 3;
    const DEBOUNCE_DELAY = 300;
    let debounceTimer;

    // create container for input element
    const inputContainerElement = document.createElement("div");
    inputContainerElement.className = "input-container";
    containerElement.appendChild(inputContainerElement);

    // create input element
    const inputElement = document.createElement("input");
    inputElement.type = "text";
    inputElement.placeholder = "Start typing address here or fill manually";
    inputContainerElement.appendChild(inputElement);

    // add input field clear button
    const clearButton = document.createElement("div");
    clearButton.className = "clear-button";
    addIcon(clearButton);
    clearButton.addEventListener("click", (e) => {
        e.stopPropagation();
        inputElement.value = '';
        callback(null);
        clearButton.classList.remove("visible");
        closeDropDownList();
    });
    inputContainerElement.appendChild(clearButton);

    /* We will call the API with a timeout to prevent unnecessary API activity.*/
    let currentTimeout;

    /* Save the current request promise reject function. To be able to cancel the promise when a new request comes */
    let currentPromiseReject;

    /* Focused item in the autocomplete list. This variable is used to navigate with buttons */
    let focusedItemIndex;

    /* Process a user input: */
    inputElement.addEventListener("input", function(e) {
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
            clearButton.classList.add("visible");
        } else {
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
                fetch(`${apiUrl}?currentValue=${currentValue}`)
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
            let currentItems = data.results;

            // handles no location match
            const noLocationMatch = document.createElement("div");
            noLocationMatch.innerHTML = "No matching location found, location-based services may not work (<u class='text-primary'>Use location</u>)";

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


                    let addressString = inputElement.value.split(/\s|,/ );
                    callback(addressString);
                    populateAddress(addressString);
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
            if (e.keyCode === 40) {
                e.preventDefault();
                /*If the arrow DOWN key is pressed, increase the focusedItemIndex variable:*/
                focusedItemIndex = focusedItemIndex !== itemElements.length - 1 ? focusedItemIndex + 1 : 0;
                /*and make the current item more visible:*/
                setActive(itemElements, focusedItemIndex);
            } else if (e.keyCode === 38) {
                e.preventDefault();

                /*If the arrow UP key is pressed, decrease the focusedItemIndex variable:*/
                focusedItemIndex = focusedItemIndex !== 0 ? focusedItemIndex - 1 : focusedItemIndex = (itemElements.length - 1);
                /*and make the current item more visible:*/
                setActive(itemElements, focusedItemIndex);
            } else if (e.keyCode === 13) {
                /* If the ENTER key is pressed and value as selected, close the list*/
                e.preventDefault();
                if (focusedItemIndex > -1) {
                    closeDropDownList();
                }
            }
        } else {
            if (e.keyCode === 40) {
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
}

/**
 * Populates address fields with properties from the selected address in the autocomplete container
 *
 * @param {Object} selectedAddress - The selected address with the address information
 */
function populateAddressFields(selectedAddress) {
    // Populate address fields with properties from the selectedAddress address
    document.getElementById("streetNumber").value = selectedAddress.housenumber || null;
    document.getElementById("streetName").value = selectedAddress.street || null;
    document.getElementById("suburb").value = selectedAddress.suburb || null;
    document.getElementById("city").value = selectedAddress.city || null;
    document.getElementById("country").value = selectedAddress.country || null;
    document.getElementById("postCode").value = selectedAddress.postcode || null;
    document.getElementById("lat").value = selectedAddress.lat || null;
    document.getElementById("lon").value = selectedAddress.lon || null;
}

/**
 * Populates address fields with item in the input with no location match
 *
 * @param {Object} currentItem - The current address input
 */
function populateAddress(currentItem) {
    document.getElementById("streetNumber").value = currentItem[0] ? currentItem[0] : null;
    document.getElementById("streetName").value = (currentItem[1] ? currentItem[1] : null)
        + (currentItem[2] ? " " + currentItem[2] : null);
    document.getElementById("suburb").value = currentItem[3] ? currentItem[3] : null;
    document.getElementById("city").value = currentItem[4] ? currentItem[4] : null;
    document.getElementById("country").value = currentItem[5] ? currentItem[5] : null;
    document.getElementById("postCode").value = currentItem[6] ? currentItem[6] : null;
}


const locationAutocompleteContainer = document.getElementById(
  'location-autocomplete-container'
);
if (locationAutocompleteContainer) {
  autocomplete(
    locationAutocompleteContainer,
    populateAddressFields,
    '/api/location-autocomplete'
  );
}

function addTag(tag) {
    // TODO
    console.log(tag);
}

const tagAutocompleteContainer = document.getElementById(
  'tag-autocomplete-container'
);
if (tagAutocompleteContainer) {
  autocomplete(
    tagAutocompleteContainer,
    addTag,
    '/api/tag-autocomplete'
  );
}
