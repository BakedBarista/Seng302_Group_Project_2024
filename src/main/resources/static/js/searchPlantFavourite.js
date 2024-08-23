function showSearchResults() {
    console.log("Button Pressed")

    document.getElementById('searchForm').addEventListener('submit', function(event) {
        console.log("reached");
        event.preventDefault();
        const searchTerm = document.getElementById('searchField').value;
        const searchResultsContainer = document.getElementById('searchResults');
        console.log("Searching for:", searchTerm);

        fetch(`edit-public-profile/search?search=`+ (searchTerm), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrf,
            },
/*
            body: JSON.stringify({searchTerm}),
*/
        }).then(response => response.json())
            .then( response => {
            console.log("data {}", response);
            const plantList = document.getElementById('plantList');
            console.log('reached this part');
/*
            plantList.innerHTML = '';
*/

            if (response.length > 0) {
                console.log(response.length);
                console.log('reached');
                response.forEach(plant => {
                    console.log(plant.name);
                    const listItem = document.createElement('li');
                    listItem.classList.add('list-group-item');
                    listItem.textContent = plant.name;
                    plantList.appendChild(listItem);
                })
            }

            console.log(response);
      /*      const searchModal = document.getElementById('plantSelectorModal');
            const modal = bootstrap.Modal.getInstance(searchModal);

            modal.show();*/

        })
    })


}