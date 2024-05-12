function validateDate() {
    let dateInput = document.getElementById('date');
    let computedStyle = window.getComputedStyle(dateInput);
    let borderColor = computedStyle.getPropertyValue('border-color');

    if (borderColor === 'rgb(255, 0, 0)') {
        document.getElementById('dateErrorMessage').style.display = 'block';
        return false;
    } else {
        document.getElementById('dateErrorMessage').style.display = 'none';
        return true;
    }
}