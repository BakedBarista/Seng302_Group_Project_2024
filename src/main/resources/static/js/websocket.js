// WebSocket connection initialization
const origin = location.origin.replace('http', 'ws');
const url = `${origin}${apiBaseUrl}/messages`;
let ws;

// Function to initialize WebSocket
function initWebSocket() {
    ws = new WebSocket(url);

    ws.addEventListener('open', () => {
        ws.send(JSON.stringify({ type: 'subscribe' }));
        ws.send(JSON.stringify({type:'ping'}));

        // Periodically send ping messages to keep the connection alive
        setInterval(() => {
            ws.send(JSON.stringify({ type: 'ping' }));
        }, 5000);
    });

    ws.addEventListener('message', (ev) => {
        const data = JSON.parse(ev.data);
        console.log('WebSocket data:', data);
        switch (data.type) {
            case 'pong':
                console.log('Received pong');
                updateUnreadCount(data.unreadMessageCount);  // Update unread message count in navbar
                break;
            case 'updateMessages':
                console.log('Received updateMessages');
                updateMessages();
                break;
            case 'error':
                handleWebSocketError(data.error);  // Handle WebSocket errors
                break;
        }
    });

    ws.addEventListener('close', (ev) => {
        console.log('WebSocket closed, attempting to reconnect...');
        reconnectWebSocket();  // Handle reconnections
    });
}

// Function to handle WebSocket reconnections
function reconnectWebSocket() {
    setTimeout(() => {
        initWebSocket();  // Reinitialize the WebSocket
    }, 3000);  // Attempt reconnection after 3 seconds
}

// Function to update unread message count on the navbar
function updateUnreadCount(count) {
    const unreadCountDisplay = document.getElementById('unreadCountDisplay');
    if (unreadCountDisplay) {
        if (count > 99) {
            unreadCountDisplay.innerText = '99+';
        } else {
            unreadCountDisplay.innerText = count;
        }

        if (count > 0) {
            unreadCountDisplay.style.display = 'inline';
        } else {
            unreadCountDisplay.style.display = 'none';
        }
    }
}

function handleWebSocketError(errorMessage) {
    console.error('WebSocket error:', errorMessage);
}

// Call initWebSocket on page load to keep WebSocket connection open
document.addEventListener('DOMContentLoaded', () => {
    initWebSocket();  // Start the WebSocket connection when the page loads
});

