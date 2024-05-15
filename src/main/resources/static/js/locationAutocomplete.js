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
            apiUrl: '/api/location-autocomplete',
            notFoundMessageHtml: "No matching location found, location-based services may not work (<u class='text-primary'>Use location</u>)",
            placeholder: "Start typing address here or fill manually",
        }
    );
}
