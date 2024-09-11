const origin = location.origin.replace('http', 'ws');
const url = `${origin}${apiBaseUrl}/messages`;
const otherUserId = activeChatId;

/**@type {HTMLFormElement} */
const sendMessageForm = document.getElementById('sendMessageForm');
const label = sendMessageForm.querySelector('label');
const textArea = sendMessageForm.querySelector('textarea');
const messagesContainer = document.getElementById('scrollbar');
const invalidFeedback = document.getElementById('invalidFeedback');

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
    if (data.type === 'pong') {
        return;
    }
    console.log(data);
    switch (data.type) {
        case 'updateMessages':
            console.log('updateMessages');
            updateMessages();
            break;
        case 'error':
            invalidFeedback.textContent = data.error;
            label.classList.add('is-invalid');
            textArea.value = data.message;
            break;
    }
});

sendMessageForm.addEventListener('submit', (ev) => {
    ev.preventDefault();

    sendMessage();
});

function sendMessage() {
    const message = textArea.value;
    ws.send(
        JSON.stringify({ type: 'sendMessage', receiver: otherUserId, message })
    );

    textArea.value = '';
    button.disabled = true;
    label.classList.remove('is-invalid');
}

async function updateMessages() {
    const res = await fetch(`${apiBaseUrl}/messages/${otherUserId}`);
    const html = await res.text();
    messagesContainer.innerHTML = html;

    scrollToBottom(messagesContainer);
}
