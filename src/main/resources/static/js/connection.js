document.addEventListener('DOMContentLoaded', function() {
    const buttonClick = document.getElementById('heartIcon');
    const toastDivs = document.getElementById('acceptToast');
    const toast = new bootstrap.Toast(toastDivs);
    
    const card = document.getElementById('card');
    const cardName = document.getElementById('cardName');
    const cardDescription = document.getElementById('cardDescription');
    const cardImage = document.getElementById('cardImage');
    
    const userListJson = getMeta('_userList') || '[]';
    const userList = JSON.parse(userListJson);
    
    let userIndex = 0;
    function currentUser() {
        return userList[userIndex];
    }

    function showCurrentUserCard() {
        if (userIndex >= userList.length) {
            const card = document.getElementById('card');
            card.outerHTML = '<p>No users left to connect with</p>';
            return;
        }
        
        const user = currentUser();
        console.log(user);
        cardName.textContent = user.fullName;
        cardDescription.textContent = user.description;
        cardImage.src = `${baseUrl}users/${user.id}/profile-picture`;
    }
    showCurrentUserCard();

    if (buttonClick) {
        buttonClick.addEventListener('click', function(event) {
            event.preventDefault();
            accept();
        });
    }

    let startX = 0;
    const SWIPE_THRESHOLD = 50;
    card.addEventListener('touchstart', function(event) {
        startX = event.touches[0].clientX;
    });
    card.addEventListener('touchend', function(event) {
        const endX = event.changedTouches[0].clientX;
        if (endX - startX >= SWIPE_THRESHOLD) {
            accept();
        } else if (endX - startX <= -SWIPE_THRESHOLD) {
            decline();
        }
    });

    function accept() {
        const formData = new FormData();
        formData.append('action', 'accept');
        formData.append('id', currentUser()?.id); // this needs to be the id of user.

        userIndex++;
        showCurrentUserCard();

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
                    console.log('Accepted friend request');
                    toast.show();
                } else {
                    console.log('Sent friend request');
                }
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
    }

    function decline() {
        // TODO
        console.log('TODO: decline');
    }
});
