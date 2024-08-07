const autocompleteContainer = document.getElementById(
    'plant-search-autocomplete-container'
);
if (autocompleteContainer) {
    autocomplete(
        autocompleteContainer,
        plantSelected,
        {
            apiUrl: `${apiBaseUrl}/search-plant-autocomplete`,
            notFoundMessageHtml: "Cannot find the plant you entered",
            placeholder: "Search plant repository",
        }
    );
}

const queryInput = autocompleteContainer.querySelector('input');
queryInput.name = "q";

const queryParams = new URLSearchParams(location.search);
queryInput.value = queryParams.get('q');

function plantSelected(plant) {
    if (plant.label) {
        queryInput.value = plant.label;
    }
}
