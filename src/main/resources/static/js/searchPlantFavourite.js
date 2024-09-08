let selectedCardId;
let selectedPlantId;
let selectedPlantName;
let selectedPlants = [];

//Called when the user pushes a + on the plant card
function openPlantSelectorModal(index) {
    const modalElement = document.getElementById("plantSelectorModal");
    const modal = new bootstrap.Modal(modalElement);
    modal.show();

    selectedCardId = 'favouritePlantCard' + index;  //Used to get the card that was clicked on
    document.getElementById('searchField').value = '';  // Clear the search input field
    document.getElementById('searchPlantResults').innerHTML = '';
    // Remove the previous event listener before adding a new one
    const submitButton = document.getElementById("plantSelectorModalSubmitButton");
    submitButton.removeEventListener('click', handleModalSubmit);  // Remove any previous listeners
    submitButton.addEventListener('click', handleModalSubmit);  // Attach the new listener
}

// New function to handle the modal submit
function handleModalSubmit() {
    previewFavouritePlants();
}

// Functions for search and selecting plants on the modal
function showSearchResults() {
    document.getElementById('searchForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const searchTerm = document.getElementById('searchField').value;
        const searchResultsContainer = document.getElementById('searchPlantResults');
        searchResultsContainer.innerHTML = '';
        fetch(`${baseUrl}users/edit-public-profile/search?search=` + encodeURIComponent(searchTerm), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrf,
            },
            body: JSON.stringify({ searchTerm: searchTerm }),
        }).then(response => response.json())
            .then(response => {
                searchResultsContainer.innerHTML = '';
                if (response.length > 0) {
                    searchResultsContainer.innerHTML = '<h2 style="padding: 10px;">Results</h2>';

                    const customDropdown = document.createElement('div');
                    customDropdown.classList.add('custom-dropdown');
                    searchResultsContainer.appendChild(customDropdown);

                    response.forEach(plant => {
                        const plantOption = document.createElement('div');
                        plantOption.classList.add('plant-option');
                        plantOption.setAttribute('data-id', plant.id);
                        plantOption.setAttribute('data-name', plant.name);

                        const imageElement = document.createElement('img');
                        imageElement.src = `${baseUrl}plants/${plant.id}/plant-image`;
                        imageElement.alt = plant.name;
                        imageElement.style.width = '50px';
                        imageElement.style.height = '50px';
                        imageElement.style.marginRight = '10px';

                        plantOption.style.padding = '10px';
                        plantOption.style.marginBottom = '10px'; // Add margin between results
                        plantOption.style.borderRadius = '5px';
                        plantOption.style.border = '1px solid #ccc';
                        plantOption.style.cursor = 'pointer';
                        plantOption.style.display = 'flex';
                        plantOption.style.alignItems = 'center';

                        plantOption.appendChild(imageElement);
                        plantOption.appendChild(document.createTextNode(` ${plant.name} (${plant.gardenName})`));

                        customDropdown.appendChild(plantOption);

                        //Used to select the plant and highlight it
                        plantOption.addEventListener('click', function() {
                            document.querySelectorAll('.plant-option').forEach(option => {
                                option.classList.remove('highlighted');
                            });

                            plantOption.classList.add('highlighted');

                            selectedPlantId = plant.id;
                            selectedPlantName = plant.name;
                            document.getElementById('selectedPlantId').value = plant.id; //Sets the hidden input field

                            // Hide the error message when a valid plant is selected
                            const errorMessage = document.getElementById('error-message');
                            errorMessage.style.display = 'none';
                        });
                    });

                } else {
                    searchResultsContainer.innerHTML = '<h2 style="padding: 10px;">No results found</h2>';
                }
            }).catch(error => {
            console.log('Error: ', error);
        });
    });
}





// Function to get the favourite plants of the user and preview them after the user has selected one from the modal
function previewFavouritePlants() {
    const plantId = selectedPlantId;
    const selectedOption = document.querySelector("#searchPlantResults .plant-option[data-id='" + plantId + "']");

    if (selectedOption) {
        const selectedCard = document.getElementById(selectedCardId);

        // Check if the plant is already selected and the selected card is not the current one
        if (selectedPlants.includes(plantId) && selectedCard.querySelector('input[type="hidden"]').value != plantId) {
            const errorMessage = document.getElementById('error-message');
            errorMessage.textContent = 'This plant has already been selected. Please choose a different one.';
            errorMessage.style.display = 'block';
            return; // Prevent the modal from closing
        }

        const plantName = selectedOption.getAttribute('data-name');
        const plantImage = selectedOption.querySelector('img').src;

        if (selectedCard) {
            // Remove the old plantId from selectedPlants (in case the user is changing the plant for the same card)
            const previousPlantId = selectedCard.querySelector('input[type="hidden"]').value;
            const previousPlantIndex = selectedPlants.indexOf(previousPlantId);
            if (previousPlantIndex > -1) {
                selectedPlants.splice(previousPlantIndex, 1);
            }

            // Update the card's appearance
            selectedCard.className = 'card p-2 me-3 mb-3 border-0 rounded-3 d-flex shadow-sm public-profile-plant-card bg-primary-temp';

            let imgElement = selectedCard.querySelector('img');
            if (!imgElement) {
                imgElement = document.createElement('img');
                selectedCard.appendChild(imgElement);
            }
            imgElement.src = plantImage;
            imgElement.alt = 'plant image';
            imgElement.className = 'mx-auto d-block pt-1';
            imgElement.style = 'width: 100%; height: 80%; object-fit: cover';

            let plantNameElement = selectedCard.querySelector('h4');
            if (!plantNameElement) {
                plantNameElement = document.createElement('h4');
                selectedCard.appendChild(plantNameElement);
            }
            plantNameElement.className = 'text-center pt-2';
            plantNameElement.textContent = plantName;

            const hiddenInput = selectedCard.querySelector('input[type="hidden"]');
            hiddenInput.value = plantId;

            // Add the new plantId to selectedPlants
            selectedPlants.push(plantId);

            // Hide the error message since plant was successfully added
            const errorMessage = document.getElementById('error-message');
            errorMessage.style.display = 'none';

            // Hide the modal
            const modal = bootstrap.Modal.getInstance(document.getElementById("plantSelectorModal"));
            modal.hide();
        } else {
            console.error(`Element with ID ${selectedCardId} not found.`);
        }
    } else {
        console.error(`Plant option with ID ${plantId} not found.`);
    }
}



// Function to update the favourite plants of the user
function updateFavouritePlants() {
    const plantIds = [
        document.getElementById('selectedPlantId1')?.value,
        document.getElementById('selectedPlantId2')?.value,
        document.getElementById('selectedPlantId3')?.value
    ].filter(id => id);


    fetch(`${baseUrl}users/edit-public-profile/favourite-plant`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrf,
        },
        body: JSON.stringify({ ids: plantIds }),
    }).then(response => {
        if (response.ok) {
            console.log("Favourite plants updated");
        } else {
            console.log("Error updating favourite plants");
            response.json().then(data => console.log(data));
        }
    }).catch(error => {
        console.log(error);
    });
}