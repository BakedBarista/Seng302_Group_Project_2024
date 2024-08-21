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

            fetch(`${apiBaseUrl}/`, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        toast.show();
                    } else {
                        console.error('Network response was not ok.');
                    }
                })
                .catch(error => {
                    console.error('There was a problem with the fetch operation:', error);
                });
        });
    }
});