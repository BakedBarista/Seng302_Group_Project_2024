function countReceivedRequests() {
    const receivedRequestsTableBody = document.querySelector('#requests-tab tbody');

    if (receivedRequestsTableBody) {
        return receivedRequestsTableBody.querySelectorAll('tr').length;
    } else {
        return 0;
    }
}
