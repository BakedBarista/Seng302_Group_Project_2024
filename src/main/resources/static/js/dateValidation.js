// function validateDate() {
//     let dateInput = document.getElementById('date');
//     let computedStyle = window.getComputedStyle(dateInput);
//     let borderColor = computedStyle.getPropertyValue('border-color');
//
//     if (borderColor === 'rgb(255, 0, 0)') {
//         document.getElementById('dateErrorMessage').style.display = 'block';
//         return false;
//     } else {
//         document.getElementById('dateErrorMessage').style.display = 'none';
//         return true;
//     }

    const dateInput = document.getElementById('date'); // Assuming you have an input element with id "myDateInput"

    dateInput.addEventListener('input', function() {
        const inputValue = this.value; // Get the value entered in the input field
        const isValidDate = isValidDateInput(inputValue); // Check if the input value is a valid date

        if (isValidDate) {
            // Input value is valid, do something
            console.log('Valid date input:', inputValue);
        } else {
            document.getElementById('dateErrorMessage').style.display = 'block';
            console.log('Invalid date input:', inputValue);
        }
    });

// }


function isValidDateInput(inputValue) {
    const dateFormatRegex = /^\d{4}-\d{2}-\d{2}$/;

    if (!dateFormatRegex.test(inputValue)) {
        return false;
    }

    const [year, month, day] = inputValue.split('-').map(Number);

    const date = new Date(year, month - 1, day); // Note: month is 0-indexed in JavaScript Dates

    return !isNaN(date) && date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day;
}