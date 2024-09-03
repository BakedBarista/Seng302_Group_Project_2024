let selectedCardId;
let selectedPlantId;
let selectedPlantName;

function openPlantSelectorModal(index) {
    const modal = new bootstrap.Modal(document.getElementById("plantSelectorModal"));
    modal.show();
    selectedCardId = 'favouritePlantCard' + index;
}

function showSearchResults() {
    document.getElementById('searchForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const searchTerm = document.getElementById('searchField').value;
        const searchResultsContainer = document.getElementById('searchResults');

        fetch(`${baseUrl}users/edit-public-profile/search?search=` + encodeURIComponent(searchTerm), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrf,
            },
            body: JSON.stringify({searchTerm: searchTerm}),
        }).then(response => response.json())
            .then(response => {
                searchResultsContainer.innerHTML = '';
                if (response.length > 0) {
                    const customSelect = document.createElement('div');
                    customSelect.classList.add('custom-select');

                    const items = document.createElement('div');
                    items.classList.add('select-items');

                    response.forEach(plant => {
                        const option = document.createElement('div');
                        const imageElement = document.createElement('img');
                        imageElement.src = `${baseUrl}plants/${plant.id}/plant-image`;
                        imageElement.alt = plant.name;

                        option.appendChild(imageElement);
                        option.appendChild(document.createTextNode(` ${plant.name} (${plant.gardenName})`));
                        option.dataset.value = plant.id;

                        option.addEventListener('click', function() {
                            selectedPlantId = plant.id;
                            selectedPlantName = plant.name;
                        });

                        items.appendChild(option);
                    });

                    customSelect.appendChild(items);
                    searchResultsContainer.appendChild(customSelect);

                } else {
                    searchResultsContainer.innerHTML = '<h2>No results found</h2>';
                }
            }).catch(error => {
            console.log('Error: ', error);
        });
    });

    document.getElementById('searchForm').addEventListener('submit', function(event) {
        event.preventDefault();
        if (selectedPlantId && selectedPlantName) {
            updateFavouritePlants(selectedPlantId, selectedPlantName);
            modal.close();
        }
    });
}

function updateFavouritePlants(plantId, plantName) {
    const selectedCard = document.getElementById(selectedCardId);

    const imgElement = selectedCard.querySelector('img');
    imgElement.src = `${baseUrl}plants/${plantId}/plant-image`;
    imgElement.alt = plantName;

    const plantNameElement = selectedCard.querySelector('h4');
    plantNameElement.textContent = plantName;

    fetch(`${baseUrl}users/edit-public-profile/favourite-plant`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrf,
        },
        body: JSON.stringify({ id: plantId }),
    }).then(response => {
        if (response.ok) {
            console.log("Favourite plant updated");
        } else {
            console.log("Error updating favourite plant");
            response.json().then(data => console.log(data));
        }
    }).catch(error => {
        console.log(error);
    });
}