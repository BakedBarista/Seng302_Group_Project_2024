document.addEventListener('DOMContentLoaded', function() {
    const buttonClick = document.getElementById('heartIcon');
    const toastDivs = document.getElementById('acceptToast');
    const toast = new bootstrap.Toast(toastDivs);
    
    const cardName = document.getElementById('cardName');
    
    const userListJson = getMeta('_userList');
    const userList = JSON.parse(userListJson);
    
    let userIndex = 0;
    function currentUser() {
        return userList[userIndex];
    }
    function showCurrentUserCard() {
        if (userIndex > userList.length) {
            const card = document.getElementById('card');
            card.outerHTML = '<p>No users left to connect with</p>';
        }
        
        const user = currentUser();
        console.log(user);
        cardName.textContent = user.fullName;
    }
    showCurrentUserCard();

    if (buttonClick) {
        buttonClick.addEventListener('click', function(event) {
            event.preventDefault();
            
            userIndex++;
            showCurrentUserCard();

            const formData = new FormData();
            formData.append('action', 'accept');
            formData.append('id', currentUser().id); // this needs to be the id of user.

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
