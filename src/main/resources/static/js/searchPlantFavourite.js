function showSearchResults() {
    console.log("Button Pressed")

    document.getElementById('searchForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const searchTerm = document.getElementById('searchField').value;
        const searchResultsContainer = document.getElementById('searchResults');
        console.log("Searching for:", searchTerm);

        fetch(`edit-public-profile/search?search=`+ encodeURIComponent(searchTerm), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrf,
            },
            body: JSON.stringify({searchTerm: searchTerm}),
        }).then(response => response.json())
            .then( response => {
                console.log("data {}", response);
                const plantList = document.getElementById('searchResults');

                if (response.length > 0) {
                    searchResultsContainer.innerHTML = '<h2>Results</h2>'

                    const plantSelection = document.createElement('select');
                    //plantSelection.innerHTML = '';
                    plantSelection.classList.add('form-select');
                    plantSelection.size = 10;
                    plantList.appendChild(plantSelection);

                    response.forEach(plant => {
                        console.log(plant.name);
                        console.log(plant.gardenName);

                        const option = document.createElement('option');

                        const plantOption = document.createElement('div');
                        const optionName = document.createElement('span');
                        optionName.innerHTML = `${plant.name}` + ` (${plant.gardenName})`;
                        plantOption.appendChild(optionName);

                        option.appendChild(plantOption);

                        // Styling for option list
                        option.style.padding = '10px';
                        option.style.borderRadius = '5px';
                        option.style.border = '1px solid #ccc';
                        option.style.cursor = 'pointer';

                        option.addEventListener('mouseover', () => {
                            option.style.backgroundColor = '#d3d3d3';
                        })

                        option.addEventListener('mouseover', () => {
                            option.style.backgroundColor = '';
                        })

                        plantSelection.appendChild(option);

                    })
                } else {
                        searchResultsContainer.innerHTML = '<h2>No results found</h2>'
                }
            }).catch(error => {
                console.log('Error: ', error);
        })
    })


}