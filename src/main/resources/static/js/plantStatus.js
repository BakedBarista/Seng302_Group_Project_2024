function saveHarvestedDate() {
    const plantId = document.getElementById('currentPlantId').value;
    const harvestedDate = document.getElementById('harvestedDateInput').value;
    const errorContainer = document.getElementById('errorContainer');

    fetch(`${apiBaseUrl}/plants/${plantId}/harvest-date`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrf,
        },
        body: JSON.stringify({ harvestedDate: harvestedDate }),
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                updateStatus(plantId, 'HARVESTED');
                const harvestedDateModal = document.getElementById('harvestedDateModal');
                const modal = bootstrap.Modal.getInstance(harvestedDateModal);
                modal.hide();

                const toastElement = document.querySelector('.toast');
                const toast = new bootstrap.Toast(toastElement);
                toast.show();
            } else if (data.status === 'error') {
                errorContainer.textContent = data.errors.join(', ');
            } else {
                throw new Error('Unexpected response status');
            }
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

$(document).ready(function() {
    $('#harvestedDateModal').on('show.bs.modal', function (event) {
        const today = new Date().toISOString().split('T')[0];
        $('#harvestedDateInput').val(today);
        $('#errorContainer').text('');
    });
});

function setPlantId(plantId) {
    document.getElementById('currentPlantId').value = plantId;
}


function updateStatus(plantId, newStatus) {
    fetch(`${apiBaseUrl}/plants/${plantId}/status?status=${newStatus}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrf,
        },
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Network response was not ok.');
        })
        .then(data => {
            const largeBadge = document.querySelector(`#dropdownMenuButton${plantId}`);
            const smallBadge = document.querySelector(`#dropdownMenuButtonSmall${plantId}`);

            if (largeBadge || smallBadge) {
                if (newStatus === 'NOT_GROWING') {
                    if (largeBadge) {
                        largeBadge.textContent = 'Not Growing';
                        largeBadge.className = 'btn button-not-planted dropdown-toggle';
                    }
                    if (smallBadge) {
                        smallBadge.textContent = 'Not Growing';
                        smallBadge.className = 'btn button-not-planted dropdown-toggle';
                    }
                } else if (newStatus === 'CURRENTLY_GROWING') {
                    if (largeBadge) {
                        largeBadge.textContent = 'Growing';
                        largeBadge.className = 'btn button-growing dropdown-toggle';
                    }
                    if (smallBadge) {
                        smallBadge.textContent = 'Growing';
                        smallBadge.className = 'btn button-growing dropdown-toggle';
                    }
                } else if (newStatus === 'HARVESTED') {
                    if (largeBadge) {
                        largeBadge.textContent = 'Harvested';
                        largeBadge.className = 'btn button-harvest dropdown-toggle';
                    }
                    if (smallBadge) {
                        smallBadge.textContent = 'Harvested';
                        smallBadge.className = 'btn button-harvest dropdown-toggle';
                    }
                }
            }
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}