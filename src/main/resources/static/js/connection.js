document.addEventListener('DOMContentLoaded', function () {
    let acceptButton = document.getElementById('heartButton');
    let declineButton = document.getElementById('declineButton');
    let toastDivs = document.getElementById('acceptToast');
    let toast = new bootstrap.Toast(toastDivs);

    if (acceptButton) {
        acceptButton.addEventListener('click', function (event) {
            event.preventDefault();
            let formData = new FormData();
            formData.append('action', 'accept');
            formData.append('id', 7); // this needs to be the id of user.

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
        });
    }
    if (declineButton) {
        declineButton.addEventListener('click', function (event) {
            event.preventDefault();
            let formData = new FormData();
            formData.append('action', 'decline');
            formData.append('id', 7); // this needs to be the id of user.

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
                .catch(error => {
                    console.error('There was a problem with the fetch operation:', error);
                });
        });
    }
});