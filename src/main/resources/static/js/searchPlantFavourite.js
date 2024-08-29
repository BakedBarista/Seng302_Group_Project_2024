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
}

