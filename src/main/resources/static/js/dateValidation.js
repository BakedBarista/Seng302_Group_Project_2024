function validateDate() {
    let dateInput = document.getElementById('date');
    let computedStyle = window.getComputedStyle(dateInput);
    let borderColor = computedStyle.getPropertyValue('border-color');

    if (borderColor === 'rgb(100, 149, 237)') {
        console.log("I'm here");
        document.getElementById('dateErrorMessage').value = "Date Invalid";
    } else {
        document.getElementById('dateErrorMessage').value = "";
    }
    return true;
}