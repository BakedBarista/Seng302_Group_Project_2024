const Actions = {
    ACCEPT: 'accept',
    DECLINE: 'decline',
}

let acceptButton = document.getElementById('heartButton');
let declineButton = document.getElementById('declineButton');

if (acceptButton) {
    acceptButton.addEventListener('click', function (event) {
        event.preventDefault();
        //TODO: This user ID needs to be updated in the new task
        handlePost(Actions.ACCEPT, document.getElementById("suggestedIdAccept").value);
    });
}

if (declineButton) {
    declineButton.addEventListener('click', function (event) {
        event.preventDefault();
        //TODO: This user ID needs to be updated in the new task
        handlePost(Actions.DECLINE, document.getElementById("suggestedIdDecline").value);
    });
}


/**
 * Handles the Post API request to the server for both accept and decline
 * @param action either 'accept' or 'decline'
 * @param suggestedUserId
 */
function handlePost(action, suggestedUserId) {
    let toastDivs = document.getElementById('acceptToast');
    let toast = new bootstrap.Toast(toastDivs);

    let formData = new FormData();
    formData.append('action', action);
    formData.append('suggestedId', suggestedUserId);

    fetch(`${baseUrl}`, {
        method: 'POST',
        headers: {
            [csrfHeader]: csrf,
        },
        body: formData
    })
    .then(response => {
        console.log('Raw response:', response);
        if (response.ok) {
            return response.json();
        } else {
            return response.json();
            throw new Error('Network response was not ok.');
        }
    })
    .then(data => {
        console.log('Response data:', data);
        if (data.success) {
            toast.show();
        }
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
    });
}