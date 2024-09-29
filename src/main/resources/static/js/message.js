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

/**
 * Updates the recent chat feed in realtime using websockets.
 * @returns {Promise<void>}
 */
async function updateRecentChat() {
    const res = await fetch(`${apiBaseUrl}/messages/recent-chats`);
    const data = await res.json();

    for (const [chatId, chat] of Object.entries(data)) {
        updateMessagePreview(chatId, chat.messageContent, chatId === chat.receiver.toString());
    }
}

/**
 * Internal function used to update the text section of the recent chat feed in realtime.
 * Used by updateRecentChats()
 * @param userId the ID of the other user the chat is with
 * @param newMessageContent the content to display in the chat feed
 * @param isSender whether or not to display 'You: ' as a prefix to the message content.
 */
function updateMessagePreview(userId, newMessageContent, isSender) {
    const previewId = `messagePreview-${userId}`;
    const previewIdImage = `messagePreviewImage-${userId}`;
    const messagePreviewElement = document.getElementById(previewId);
    const messagePreviewImageElement = document.getElementById(previewIdImage);

    const previewIdMobile = `messagePreviewMobile-${userId}`;
    const previewIdImageMobile = `messagePreviewImageMobile-${userId}`;
    const messagePreviewMobileElement = document.getElementById(previewIdMobile);
    const messagePreviewImageMobileElement = document.getElementById(previewIdImageMobile);

    // If an image was last sent
    if (newMessageContent === null || newMessageContent === '') {
        if (messagePreviewImageElement) {
           messagePreviewImageElement.textContent = `${isSender === true ? "You: ": ""}Image 📷`;
           messagePreviewImageElement.title = 'Image';
           messagePreviewElement.className = "d-none";
           messagePreviewImageElement.className = "d-block";
        }

        if (messagePreviewImageMobileElement) {
            messagePreviewImageMobileElement.textContent = `${isSender === true ? "You: ": ""}Image 📷`;
            messagePreviewImageMobileElement.title = 'Image';
            messagePreviewMobileElement.className = "d-none";
            messagePreviewImageMobileElement.className = "d-block";
        }
    } else {
        if (messagePreviewElement) {
            messagePreviewElement.textContent = `${isSender === true ? "You: ": ""} ${newMessageContent}`;
            messagePreviewElement.title = newMessageContent;
            messagePreviewElement.className = "d-block text-truncate m-0";

            if (messagePreviewImageElement) {
                messagePreviewImageElement.className = "d-none"
            }
        }

        if (messagePreviewMobileElement) {
            messagePreviewMobileElement.textContent = `${isSender === true ? "You: ": ""} ${newMessageContent}`;
            messagePreviewMobileElement.title = newMessageContent;
            messagePreviewMobileElement.className = "d-block text-truncate m-0";
            
            if (messagePreviewImageMobileElement) {
                messagePreviewImageMobileElement.className = "d-none"
            }
        }
    }
}

function isScrolledToBottom(container) {
    return container.scrollHeight - container.scrollTop === container.clientHeight;
}

