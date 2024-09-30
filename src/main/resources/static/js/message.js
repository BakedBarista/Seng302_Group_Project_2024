const otherUserId = activeChatId;
const sendMessageForm = document.getElementById('sendMessageForm');
const label = sendMessageForm?.querySelector('label');
const textArea = sendMessageForm?.querySelector('textarea');
const messagesContainer = document.getElementById('scrollbar');
let currentMessageEmojiId = null;

document.body.addEventListener('click', () => {
    if (currentMessageEmojiId != null) {
        const emojiPicker = document.getElementById( "emoji-picker-" + currentMessageEmojiId);
        emojiPicker.className = "d-none";
        currentMessageEmojiId = null;
    }
})

async function updateMessages() {
    const res = await fetch(`${apiBaseUrl}/messages/${otherUserId}`);
    const html = await res.text();
    messagesContainer.innerHTML = html;

    addEventListenersToMessages();

    scrollToBottom(messagesContainer);
    ws.send(JSON.stringify({ type: 'markRead', otherUserId }));
    await updateRecentChat();
}


function addEventListenersToMessages() {
    /**@type {NodeListOf<HTMLElement>} */
    const messages = messagesContainer.querySelectorAll('.message');
    for (const message of messages) {
        const messageId = parseInt(message.getAttribute('data-message-id'));

        message.style.userSelect = 'none';

        /**@type {number | null} */
        let timeout = null;
        function touchStart() {
            timeout = setTimeout(() => {
                longPressHandler(messageId);
            }, 400);
        }
        function touchEnd() {
            if (timeout) {
                clearTimeout(timeout);
                timeout = null;
            }
        }

        /**@type {NodeListOf<HTMLElement>} */
        const cards = message.querySelectorAll('.from-other .card');
        for (const card of cards) {
            card.addEventListener('mousedown', touchStart);
            card.addEventListener('mouseup', touchEnd);
            card.addEventListener('mousemove', touchEnd);
            card.addEventListener('touchstart', touchStart);
            card.addEventListener('touchend', touchEnd);
            card.addEventListener('touchmove', touchEnd);
        }
    }
}

function longPressHandler(messageId) {
    const emojiPicker = document.getElementById( "emoji-picker-" + messageId);
    emojiPicker.className = 'flap-in';

    setTimeout(() => currentMessageEmojiId = messageId, 1000)
}

function addEmoji(event) {
    const emojiBadge = event.target;
    const emoji = emojiBadge.textContent;

    ws.send(
        JSON.stringify({ type: 'addEmoji', messageId: currentMessageEmojiId, emoji })
    );
}

function sendMessage() {
    const message = textArea.value;
    ws.send(JSON.stringify({ type: 'sendMessage', receiver: otherUserId, message }));
    textArea.value = '';
    button.disabled = true;
    label.classList.remove('is-invalid');
}

sendMessageForm.addEventListener('submit', (ev) => {
    ev.preventDefault();

    sendMessage();
});

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
            messagePreviewElement.className = "d-block text-truncate m-0 darkText";

            if (messagePreviewImageElement) {
                messagePreviewImageElement.className = "d-none"
            }
        }

        if (messagePreviewMobileElement) {
            messagePreviewMobileElement.textContent = `${isSender === true ? "You: ": ""} ${newMessageContent}`;
            messagePreviewMobileElement.title = newMessageContent;
            messagePreviewMobileElement.className = "d-block text-truncate m-0 darkText";

            if (messagePreviewImageMobileElement) {
                messagePreviewImageMobileElement.className = "d-none"
            }
        }
    }
}

