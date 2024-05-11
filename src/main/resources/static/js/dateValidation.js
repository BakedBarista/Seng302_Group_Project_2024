function validateForm() {
    let inputDate = document.getElementById("date").value;
    console.log("\nI'm here")
    console.log(inputDate);
    if (!inputDate) {
        alert("Please enter a complete date (DD/MM/YYYY).");
        return false;
    }

    if (inputDate) {
        let parts = inputDate.split("-");
        if (parts.length !== 3 || parts[0].length !== 4 || parts[1].length !== 2 || parts[2].length !== 2) {
            alert("Please enter a valid date");
        }

        let year = parseInt(parts[0], 10);
        let month = parseInt(parts[1], 10);
        let day = parseInt(parts[2], 10);

        if (isNaN(year) || isNaN(month) || isNaN(day)) {
            alert("Please enter a valid date");
            return false;
        }
    }
    return true;
}