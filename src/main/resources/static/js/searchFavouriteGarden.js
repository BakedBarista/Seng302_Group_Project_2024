// Trigger default search with empty search term when modal loads
document.getElementById('gardenSelectorModal').addEventListener('shown.bs.modal', function () {
    showGardenSearchResults(""); // Call the search with empty string on modal load
});

// Set up the form submit event listener
document.getElementById("searchGardenForm").addEventListener('submit', function(e) {
    e.preventDefault(); // Prevent the default form submission
    const searchTerm = document.getElementById("searchGardenInput").value; // Get the search term from input
    showGardenSearchResults(searchTerm); // Call the search with the current input value
});

function showGardenSearchResults(searchTerm = "") {
    const searchResultsContainer = document.getElementById("searchGardenResults");
    fetch(`edit-public-profile/favourite-garden?search=` + encodeURIComponent(searchTerm), {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrf,
        },
        body: JSON.stringify({searchTerm: searchTerm}),
    }).then(response => response.json())
        .then(response => {
            const gardenList = document.getElementById('searchGardenResults');
            gardenList.innerHTML = ''; // Clear previous results

            if (response.length > 0) {
                searchResultsContainer.innerHTML = '<h2>Results</h2>';
                const gardenSelection = document.createElement('select');
                gardenSelection.classList.add('form-select');
                gardenSelection.size = 10;
                gardenList.appendChild(gardenSelection);

                response.forEach(garden => {
                    const option = document.createElement('option');
                    option.setAttribute('data-id', garden.id);
                    option.setAttribute('data-location', garden.location);
                    option.setAttribute('data-description', garden.description);
                    option.setAttribute('data-size', garden.size);
                    option.textContent = `${garden.name}`;

                    option.style.padding = '10px';
                    option.style.borderRadius = '5px';
                    option.style.border = '1px solid #ccc';
                    option.style.cursor = 'pointer';

                    gardenSelection.appendChild(option);
                });

                gardenSelection.addEventListener("change", function () {
                    const selectedOption = gardenSelection.options[gardenSelection.selectedIndex];
                    const gardenId = selectedOption.getAttribute('data-id');
                    document.getElementById('selectedGardenId').value = gardenId;
                });
            } else {
                searchResultsContainer.innerHTML = '<h2>No results found</h2>';
            }
        }).catch(error => {
        console.log(error);
    });


}

function previewFavouriteGarden() {
    const gardenId = document.getElementById('selectedGardenId').value;
    const selectedOption = document.querySelector("#searchGardenResults select option[data-id='" + gardenId + "']");

    if (selectedOption) {
        const gardenName = selectedOption.textContent;
        const gardenLocation = selectedOption.getAttribute('data-location');
        const gardenDescription = selectedOption.getAttribute('data-description');
        const gardenSize = selectedOption.getAttribute('data-size');

        // Update the preview in the UI
        document.getElementById('selectedGardenName').textContent = gardenName;
        document.getElementById('selectedGardenLocation').textContent = gardenLocation;
        document.getElementById('selectedGardenDescription').textContent = gardenDescription;
        document.getElementById('selectedGardenSize').textContent = gardenSize;

        const gardenImageUrl = `${baseUrl}gardens/${gardenId}/garden-image`
        document.getElementById('selectedGardenImage').src = gardenImageUrl;
    } else {
        console.log("No garden selected.");
    }
}


function updateFavouriteGarden() {
    const gardenId = document.getElementById('selectedGardenId').value;

    if(!gardenId) {
        document.getElementById("editPublicProfileForm").submit();
        return;

    }


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
            document.getElementById("editPublicProfileForm").submit();
        } else {
            console.log("Error updating garden");
            response.json().then(data => console.log(data));

        }
    }).catch(error => {
        console.log(error);
    });



}
