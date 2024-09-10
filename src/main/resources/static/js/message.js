const origin = location.origin.replace('http', 'ws');
const url = `${origin}${apiBaseUrl}/messages`;
const otherUserId = new URLSearchParams(location.search).get('id');

/**@type {HTMLFormElement} */
const sendMessageForm = document.getElementById('sendMessageForm');
const messagesContainer = document.getElementById('scrollbar');

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
        case 'updateMessages':
            console.log('updateMessages');
            updateMessages();
            break;
    }
});

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

async function updateMessages() {
    const res = await fetch(`${apiBaseUrl}/messages/${otherUserId}`);
    const html = await res.text();
    messagesContainer.innerHTML = html;

    button.disabled = true;
    scrollToBottom(messagesContainer);
}
