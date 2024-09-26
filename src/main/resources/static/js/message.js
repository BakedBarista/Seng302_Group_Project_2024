const origin = location.origin.replace('http', 'ws');
const url = `${origin}${apiBaseUrl}/messages`;
const otherUserId = activeChatId;

/**@type {HTMLFormElement} */
const sendMessageForm = document.getElementById('sendMessageForm');
const label = sendMessageForm?.querySelector('label');
const textArea = sendMessageForm?.querySelector('textarea');
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

function markMessagesAsRead() {
    ws.send(JSON.stringify({
        type: 'readMessage',
        receiver: otherUserId
    }));
}

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
    await updateRecentChat();
}

async function updateRecentChat() {
    const res = await fetch(`${apiBaseUrl}/messages/recent-chats`);
    const data = await res.json();
    console.log(data);

    for (const [chatId, chat] of Object.entries(data)) {
        console.log("ID: ", chatId);  // Access the chat ID
        console.log("CONTENT: ", chat.messageContent);  // Access message content
        updateMessagePreview(chatId, chat.messageContent);
    }
}

function updateMessagePreview(userId, newMessageContent) {
    // Determine the correct element ID based on whether the logged-in user is the sender or receiver
    const previewId = `messagePreview-${userId}`;

    const previewIdImage = `messagePreviewImage-${userId}`;

    console.log("PREVIEW ID:", previewId);
    console.log("IMAGE ID:", previewIdImage);

    // If an image
    if (newMessageContent == null) {
        console.log("DOING AN IMAGE");
        // Find the element by its ID
        const messagePreviewElement = document.getElementById(previewIdImage);

        if (messagePreviewElement) {
            messagePreviewElement.textContent = `Image 📷`;
            messagePreviewElement.title = newMessageContent;
            // messagePreviewElementImage.className = "d-block";
        }
    } else {
        const messagePreviewElement = document.getElementById(previewId);

        if (messagePreviewElement) {
            messagePreviewElement.textContent = newMessageContent;
            messagePreviewElement.title = newMessageContent;
        }

        // const messagePreviewElementImage = document.getElementById(previewIdImage);
        //
        // if (messagePreviewElementImage) {
        //     messagePreviewElementImage.className = "d-none";
        // }
    }

}

function isScrolledToBottom(container) {
    return container.scrollHeight - container.scrollTop === container.clientHeight;
}

