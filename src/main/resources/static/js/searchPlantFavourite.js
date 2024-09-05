let selectedCardId;
let selectedPlantId;
let selectedPlantName;

function openPlantSelectorModal(index) {
    const modal = new bootstrap.Modal(document.getElementById("plantSelectorModal"));
    modal.show();
    selectedCardId = 'favouritePlantCard' + index;
    console.log(selectedCardId);

    document.getElementById("plantSelectorModalSubmitButton").addEventListener('click', function() {
        previewFavouritePlants();
        modal.hide();
    }, { once: true });
}

function showSearchResults() {
    document.getElementById('searchForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const searchTerm = document.getElementById('searchField').value;
        const searchResultsContainer = document.getElementById('searchPlantResults');

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
                    searchResultsContainer.innerHTML = '<h2>Results</h2>';
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

                        plantOption.appendChild(imageElement);
                        plantOption.appendChild(document.createTextNode(` ${plant.name} (${plant.gardenName})`));

                        plantOption.style.padding = '10px';
                        plantOption.style.borderRadius = '5px';
                        plantOption.style.border = '1px solid #ccc';
                        plantOption.style.cursor = 'pointer';
                        plantOption.style.display = 'flex';
                        plantOption.style.alignItems = 'center';

                        customDropdown.appendChild(plantOption);

                        plantOption.addEventListener('click', function() {
                            document.querySelectorAll('.plant-option').forEach(option => {
                                option.classList.remove('highlighted');
                            });

                            plantOption.classList.add('highlighted');

                            selectedPlantId = plant.id;
                            selectedPlantName = plant.name;
                            document.getElementById('selectedPlantId').value = plant.id;
                            console.log(plant.id);
                        });
                    });

                } else {
                    searchResultsContainer.innerHTML = '<h2>No results found</h2>';
                }
            }).catch(error => {
            console.log('Error: ', error);
        });
    });
}







function renderPlantCards(favouritePlants) {
    const container = document.getElementById('favouritePlantsContainer');
    container.innerHTML = ''; // Clear existing cards

    // Render cards for favorite plants
    favouritePlants.forEach((plant, index) => {
        const plantCard = document.createElement('div');
        plantCard.className = 'card p-2 me-3 mb-3 border-0 rounded-3 d-flex flex-column shadow-sm public-profile-plant-card bg-primary-temp';
        plantCard.id = `favouritePlantCard${index + 1}`;

        const imageContainer = document.createElement('div');
        imageContainer.className = 'plant-image-container';
        imageContainer.style = 'height: 50%;'; // Take up the top half

        const imageElement = document.createElement('img');
        imageElement.src = `${baseUrl}plants/${plant.id}/plant-image`;
        imageElement.alt = plant.name;
        imageElement.className = 'w-100 h-100';
        imageElement.style = 'object-fit: cover;';

        imageContainer.appendChild(imageElement);

        const plantName = document.createElement('h4');
        plantName.className = 'pt-2 ps-2 text-center';
        plantName.textContent = plant.name;
        plantName.style = 'flex-grow: 1; display: flex; align-items: center; justify-content: center;'; // Center the text vertically

        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.id = `selectedPlantId${index + 1}`;
        hiddenInput.value = plant.id;

        plantCard.appendChild(imageContainer);
        plantCard.appendChild(plantName);
        plantCard.appendChild(hiddenInput);
        container.appendChild(plantCard);

        console.log(`Card ${plantCard.id} created with plant name: ${plant.name}`);
    });

    // Render empty cards
    const remainingCards = 3 - favouritePlants.length;
    for (let i = 0; i < remainingCards; i++) {
        const emptyCard = document.createElement('div');
        emptyCard.className = 'card p-2 me-3 mb-3 border-0 rounded-3 shadow-sm public-profile-plant-card justify-content-center card-wiggle bg-primary-grey';
        emptyCard.id = `favouritePlantCard${favouritePlants.length + i + 1}`;
        emptyCard.onclick = function() {
            openPlantSelectorModal(favouritePlants.length + i + 1);
        };

        const placeholderImage = document.createElement('img');
        placeholderImage.src = '/icons/create-mustard-grey.svg';
        placeholderImage.alt = 'empty-favourite';
        placeholderImage.width = 50;
        placeholderImage.height = 50;
        placeholderImage.className = 'mx-auto d-block';

        const emptyPlantName = document.createElement('h4');
        emptyPlantName.className = 'pt-2 ps-2 text-center';
        emptyPlantName.textContent = ''; // Empty text content for empty cards

        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.id = `selectedPlantId${favouritePlants.length + i + 1}`;
        hiddenInput.value = '';

        emptyCard.appendChild(placeholderImage);
        emptyCard.appendChild(emptyPlantName);
        emptyCard.appendChild(hiddenInput);
        container.appendChild(emptyCard);

        console.log(`Empty card ${emptyCard.id} created`);
    }
}


function previewFavouritePlants() {
    const plantId = selectedPlantId; // Use the global selectedPlantId
    const selectedOption = document.querySelector("#searchPlantResults .plant-option[data-id='" + plantId + "']");

    if (selectedOption) {
        const plantName = selectedOption.getAttribute('data-name');
        const plantImage = selectedOption.querySelector('img').src;

        const selectedCard = document.getElementById(selectedCardId);
        console.log(selectedCard);
        if (selectedCard) {
            const h4Element = selectedCard.querySelector('h4');
            if (!h4Element) {
                console.error(`H4 element not found in the selected card.`);
                return; // Exit the function early if h4Element is null
            }
            selectedCard.querySelector('h4').textContent = plantName;
            selectedCard.querySelector('img').src = plantImage;

            // Set the value of the hidden input field
            const hiddenInput = selectedCard.querySelector('input[type="hidden"]');
            hiddenInput.value = plantId;
            console.log(`Set hidden input value for ${selectedCardId}: ${plantId}`);
        } else {
            console.error(`Element with ID ${selectedCardId} not found.`);
        }
    } else {
        console.error(`Plant option with ID ${plantId} not found.`);
    }
}


function updateFavouritePlants() {
    const plantIds = [
        document.getElementById('selectedPlantId1').value,
        document.getElementById('selectedPlantId2').value,
        document.getElementById('selectedPlantId3').value
    ].filter(id => id);
    console.log(plantIds);

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