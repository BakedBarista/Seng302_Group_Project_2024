document.addEventListener('DOMContentLoaded', function() {
    var buttonClick = document.querySelector('button[type="submit"]');
    var toastDivs = document.getElementById('acceptToast');
    var toast = new bootstrap.Toast(toastDivs);

    if (buttonClick) {
        buttonClick.addEventListener('click', function(event) {
            event.preventDefault();
            var formData = new FormData();
            formData.append('action', 'accept');
            formData.append('id', 1); // this needs to be the id of user.

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
});