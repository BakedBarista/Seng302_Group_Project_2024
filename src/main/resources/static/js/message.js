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
    ws.send(
        JSON.stringify({ type: 'sendMessage', receiver: otherUserId, message })
    );

    textArea.value = '';
    button.disabled = true;
    label.classList.remove('is-invalid');
}

sendMessageForm.addEventListener('submit', (ev) => {
    ev.preventDefault();

    sendMessage();
});
