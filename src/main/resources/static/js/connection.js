const Actions = {
    ACCEPT: 'accept',
    DECLINE: 'decline',
}

document.addEventListener('DOMContentLoaded', function () {
    let acceptButton = document.getElementById('heartButton');
    let declineButton = document.getElementById('declineButton');

    if (acceptButton) {
        acceptButton.addEventListener('click', function (event) {
            event.preventDefault();
            //TODO: This user ID needs to be updated in the new task
            handlePost(Actions.ACCEPT, 7);
        });
    }

    if (declineButton) {
        declineButton.addEventListener('click', function (event) {
            event.preventDefault();
            //TODO: This user ID needs to be updated in the new task
            handlePost(Actions.DECLINE, 7);
        });
    }
});


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
    formData.append('id', suggestedUserId);

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
            throw new Error('Network response was not ok.');
        }
    })
    .then(data => {
        console.log('Response data:', data);
        if (data.success) {
            toast.show();
        } else {
            console.error('Request failed or no matching request found.');
        }
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
    });
}