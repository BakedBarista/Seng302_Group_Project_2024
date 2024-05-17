/**
 * Checks if the date field has not been filled out correctly
 * (e.g. catches the case of 10/mm/2001)
 *
 * Changes the value of the dateErrorMessage field so that the
 * controller can recognise if the date has not been filled correctly
 *
 * @returns {boolean} to give permission for thymeleaf to continue once these checks have been done
 */
function validateDate() {
    let dateInput = document.getElementById('plantedDate');

    if (dateInput.validity.badInput) {
        document.getElementById('dateError').value = "dateInvalid";
    } else {
            document.getElementById('dateError').value = "";
    }
    return true;
}