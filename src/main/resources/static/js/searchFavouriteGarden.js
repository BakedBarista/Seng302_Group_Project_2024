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
                        option.setAttribute('data-id', garden.id);
                        optionName.innerHTML = `${garden.name}`;
                        gardenOption.appendChild(optionName);
                        option.appendChild(gardenOption);

                        option.style.padding = '10px';
                        option.style.borderRadius = '5px';
                        option.style.border = '1px solid #ccc';
                        option.style.cursor = 'pointer';

                        gardenSelection.appendChild(option);

                    })
                    gardenSelection.addEventListener("change", function() {
                        const selectedOption = gardenSelection.options[gardenSelection.selectedIndex];
                        const gardenId = selectedOption.getAttribute('data-id');
                        document.getElementById('selectedGardenId').value = gardenId;
                        console.log(gardenId);
                    })
                } else {
                    searchResultsContainer.innerHTML = '<h2>No results found</h2>';
                }
            }).catch(error => {
                console.log(error);
        })

    })


}

function updateFavouriteGarden() {
    const gardenId = document.getElementById('selectedGardenId').value;
    console.log(gardenId);
    console.log(JSON.stringify({id:gardenId}));

    fetch(`${baseUrl}users/edit-public-profile/favourite-garden`, {
        method: 'PUT',
        headers: {
            'Content-Type':'application/json',
            [csrfHeader]: csrf,
        },
        body:JSON.stringify({id:gardenId}),
    }).then(response => {
        if(response.ok) {
            console.log("Garden updated");
        } else {
            console.log("Error updating garden");
            response.json().then(data => console.log(data));

        }
    }).catch(error => {
        console.log(error);
    });

}