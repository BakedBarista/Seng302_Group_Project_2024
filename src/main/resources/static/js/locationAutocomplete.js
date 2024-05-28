/**
 * Populates address fields with properties from the selected address in the autocomplete container
 *
 * @param {object | string} selectedAddress - The selected address with the address information
 */
function populateAddressFields(selectedAddress) {
    if (typeof selectedAddress === 'string') {
        const currentItem = selectedAddress.split(/\s|,/);
        document.getElementById("streetNumber").value = currentItem[0] ? currentItem[0] : null;
        document.getElementById("streetName").value = (currentItem[1] ? currentItem[1] : null)
            + (currentItem[2] ? " " + currentItem[2] : null);
        document.getElementById("suburb").value = currentItem[3] ? currentItem[3] : null;
        document.getElementById("city").value = currentItem[4] ? currentItem[4] : null;
        document.getElementById("country").value = currentItem[5] ? currentItem[5] : null;
        document.getElementById("postCode").value = currentItem[6] ? currentItem[6] : null;
    } else {
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
}

const locationAutocompleteContainer = document.getElementById(
    'location-autocomplete-container'
);
if (locationAutocompleteContainer) {
    autocomplete(
        locationAutocompleteContainer,
        populateAddressFields,
        {
            apiUrl: `${apiBaseUrl}/location-autocomplete`,
            notFoundMessageHtml: "No matching location found, location-based services may not work (<u class='text-primary'>Use location</u>)",
            placeholder: "Start typing address here or fill manually",
        }
    );
}

function handleManualLocation() {
    var manualAddress = "";
    manualAddress += document.getElementById("streetNumber").value
    manualAddress += " " + document.getElementById("streetName").value;
    manualAddress += " " + document.getElementById("suburb").value;
    manualAddress += " " + document.getElementById("city").value;
    manualAddress += " " + document.getElementById("country").value;
    manualAddress += " " + document.getElementById("postCode").value;

    const MIN_ADDRESS_LENGTH = 3;
    const DEBOUNCE_DELAY = 300;
    let debounceTimer;
    let currentPromiseReject;

    const request = new Promise((resolve, reject) => {
        currentPromiseReject = reject;

        debounceTimer = setTimeout(() => {
            fetch(`${apiBaseUrl}/location-search?location=${manualAddress}`)
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

    request.then((data) => {
        // here we get address suggestions
        currentItems = data.results;
        console.log(data.results);
        console.log(data.results.lat);
        console.log(data.results.lng);

        document.getElementById("lat").value = currentItems.lat;
        document.getElementById("lng").value = currentItems.lng;

    }, (err) => {
        if (!err.cancelled) {
            console.log(err);
        }
    });

}
