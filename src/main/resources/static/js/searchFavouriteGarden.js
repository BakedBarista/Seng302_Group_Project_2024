function showGardenSearchResults() {
    document.getElementById("searchGardenForm").addEventListener('submit', function(e) {
        e.preventDefault();
        const searchTerm = document.getElementById("searchGardenInput").value;
        console.log(searchTerm);
        console.log(JSON.stringify(searchTerm));
        const searchResultsContainer = document.getElementById("searchGardenResults");
        fetch(`edit-public-profile/favourite-garden?search=`+ encodeURIComponent(searchTerm),{
            method: 'POST',
            headers: {
                'Content-Type':'application/json',
                [csrfHeader]: csrf,
            },
            body:JSON.stringify({ searchTerm: searchTerm }),
        }).then(response => response.json())
            .then(response => {
                console.log(response)
                const gardenList = document.getElementById('searchGardenResults');
                if(response.length > 0) {
                    searchResultsContainer.innerHTML = '<h2>Results</h2>';
                    const gardenSelection = document.createElement('select')
                    gardenSelection.classList.add('form-select');
                    gardenSelection.size = 10;
                    gardenList.appendChild(gardenSelection);

                    response.forEach(garden => {
                        const option = document.createElement('option');
                        const gardenOption = document.createElement('div');
                        const optionName = document.createElement('span');
                        optionName.innerHTML = `${garden.name}`;
                        gardenOption.appendChild(optionName);
                        option.appendChild(gardenOption);

                        option.style.padding = '10px';
                        option.style.borderRadius = '5px';
                        option.style.border = '1px solid #ccc';
                        option.style.cursor = 'pointer';

                        gardenSelection.appendChild(option);
                    })
                } else {
                    searchResultsContainer.innerHTML = '<h2>No results found</h2>';
                }
            }).catch(error => {
                console.log(error);
        })

    })
}