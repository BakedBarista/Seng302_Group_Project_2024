document.addEventListener('DOMContentLoaded', function() {
    const declineButton = document.getElementById('declineButton');
    const acceptButton = document.getElementById('heartIcon');
    const toastDivs = document.getElementById('acceptToast');
    const toast = new bootstrap.Toast(toastDivs);
    
    const card = document.getElementById('card');
    const cardName = document.getElementById('cardName');
    const cardCompatibility = document.getElementById('cardCompatibility');
    const cardDescription = document.getElementById('cardDescription');
    const cardImage = document.getElementById('cardImage');
    const birthFlower = document.getElementById('birthFlower');
    const favouriteGarden = document.getElementById('favouriteGarden');
    const favouritePlants = document.getElementById('favouritePlants');
    
    const userListJson = getMeta('_userList') || '[]';
    let userList = JSON.parse(userListJson);
    let userIndex = 0;
    function currentUser() {
        return userList[userIndex];
    }

    function getProfilePictureUrl(user) {
        if (user.hasProfilePicture) {
            return `${baseUrl}users/${user.id}/profile-picture`;
        } else {
            return `${baseUrl}img/default-profile.svg`;
        }
    }

    setTimeout(() => {
        // Preload all of the images after 0.5s
        for (const user of userList) {
            const link = document.createElement('link');
            link.rel = 'preload';
            link.as = 'image';
            link.href = getProfilePictureUrl(user);
            document.head.appendChild(link);
        }
    }, 500);

    function showCurrentUserCard() {
        if (userIndex >= userList.length) {
            const card = document.getElementById('card');

            card.outerHTML = '<p class="darkText">No users left to connect with</p>';
            return;
        }
        
        const user = currentUser();
        console.log(user);

        cardName.textContent = user.fullName;
        cardCompatibility.textContent = user.compatibility;
        cardDescription.textContent = user.description;
        birthFlower.innerHTML = user.birthFlowerHtml;
        favouriteGarden.innerHTML = user.favouriteGardenHtml;
        favouritePlants.innerHTML = user.favouritePlantsHtml;
        cardImage.src = getProfilePictureUrl(user);

        // Update the progress bar used as the compatibility measure
        const progressBar = document.querySelector('.progress-bar');
        progressBar.style.width = `${user.compatibility}%`;

        if (user.compatibility <= 30) {
            progressBar.style.background = '#d47b7b';
        } else if (user.compatibility <= 60) {
            progressBar.style.background = '#cb9f03';
        } else {
            progressBar.style.background = '#93d77b';
        }

    }
    showCurrentUserCard();

    async function nextUser(swipeDirection) {
        userIndex++;

        card.classList.add(swipeDirection);
        await delay(500);
        showCurrentUserCard();
        await delay(500);
        card.classList.remove(swipeDirection);
    }

    function delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    if (declineButton) {
        declineButton.addEventListener('click', function(event) {
            event.preventDefault();
            decline();
        });
    }
    if (acceptButton) {
        acceptButton.addEventListener('click', function(event) {
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

        nextUser('swipe-right');

        sendPost(formData);
    }

    function decline() {
        const formData = new FormData();
        formData.append('action', 'decline');
        formData.append('id', currentUser()?.id); // this needs to be the id of user.

        nextUser('swipe-left');

        sendPost(formData);
    }

    function sendPost(formData) {
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
});
