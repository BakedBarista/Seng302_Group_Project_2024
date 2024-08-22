function showSearchResults() {
    console.log("Button Pressed")

    document.getElementById('searchForm').addEventListener('submit', function(event) {
        console.log("reached");
        event.preventDefault();
        const searchTerm = document.getElementById('searchField').value;
        const searchResultsContainer = document.getElementById('searchResults');
        console.log("Searching for:", searchTerm);

        fetch(`${apiBaseUrl}/users/edit-public-profile/search?search=`+ (searchTerm), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({searchTerm: this.searchTerm}),
        }).then( data => {

            console.log(data);
            const searchModal = document.getElementById('plantSelectorModal');
            const modal = bootstrap.Modal.getInstance(searchModal);

            modal.show();

        })
    })


}