let selectedCardId;
let selectedPlantId;
let selectedPlantName;

function openPlantSelectorModal(index) {
    const modal = new bootstrap.Modal(document.getElementById("plantSelectorModal"));
    modal.show();
    selectedCardId = 'favouritePlantCard' + index;
    console.log(selectedCardId);
}

function renderPlantCards(favouritePlants) {
    const container = document.getElementById('favouritePlantsContainer');
    container.innerHTML = ''; // Clear existing cards

    // Render cards for favorite plants
    favouritePlants.forEach((plant, index) => {
        const plantCard = document.createElement('div');
        plantCard.className = 'card p-2 me-3 mb-3 border-0 rounded-3 d-flex shadow-sm public-profile-plant-card bg-primary-temp';
        plantCard.id = `favouritePlantCard${index + 1}`;

        const imageElement = document.createElement('img');
        imageElement.src = `${baseUrl}plants/${plant.id}/plant-image`;
        imageElement.alt = plant.name;
        imageElement.className = 'mx-auto d-block pt-1';
        imageElement.style = 'width: 100%; height: 80%; object-fit: cover';

        const plantName = document.createElement('h4');
        plantName.className = 'pt-2 ps-2';
        plantName.textContent = plant.name;

        plantCard.appendChild(imageElement);
        plantCard.appendChild(plantName);
        container.appendChild(plantCard);
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

        emptyCard.appendChild(placeholderImage);
        container.appendChild(emptyCard);
    }
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


function previewFavouritePlants() {
    const plantId = document.getElementById('selectedPlantId').value;

    const selectedOption = document.querySelector("#searchPlantResults select option[data-id='" + plantId + "']");

    if (selectedOption) {
        const plantName = selectedOption.getAttribute('data-name');
        const plantImage = selectedOption.querySelector('img').src;

        const selectedCard = document.getElementById(selectedCardId);
        selectedCard.querySelector()

    }

}

function updateFavouritePlants() {
    const plantId = document.getElementById('selectedPlantId').value;


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