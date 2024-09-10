const origin = location.origin.replace('http', 'ws');
const url = `${origin}${apiBaseUrl}/messages`;
const otherUserId = new URLSearchParams(location.search).get('id');

const ws = new WebSocket(url);
ws.addEventListener('open', () => {
    ws.send(JSON.stringify({ type: 'subscribe' }));

    // Periodically send ping messages to keep the connection alive
    setInterval(() => {
        ws.send(JSON.stringify({ type: 'ping' }));
    }, 5000);
});


ws.addEventListener('message', (ev) => {
    const data = JSON.parse(ev.data);
    switch (data.type) {
        case 'value':
            break;
    }
});

/**@type {HTMLFormElement} */
const sendMessageForm = document.getElementById('sendMessageForm');
sendMessageForm.addEventListener('submit', (ev) => {
    ev.preventDefault();

    sendMessage();
});

function sendMessage() {
    const textArea = sendMessageForm.querySelector('textarea');
    const message = textArea.value;
    ws.send(
        JSON.stringify({ type: 'sendMessage', reciever: otherUserId, message })
    );
    textArea.value = '';
}
