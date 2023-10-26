let socket;
let socketConnected = false;
let socketId = ""
let clients = []
let selectedClient = ""

function connect(protocol, ip, port) {

    socket = new WebSocket(`${protocol}://${ip}:${port}`)

    socket.onopen = function(e) {
        console.log("Socket connected")
        socketConnected = true
    }
    
    socket.onmessage = function(event) {
        let data = JSON.parse(event.data)
        let message = ""
        console.log(data)
        switch (data.type) {
            case 'list':
                clients = data.list.map(e => e.toString());
                clients.splice(clients.indexOf(socketId), 1);
                message = `List of clients: ${data.list}\n`;
                break;
            case 'id':
                socketId = data.value;
                message = `Id received: ${data.value}\n`;
                break;
            case 'connected':
                clients.push(data.id);
                clients.splice(clients.indexOf(socketId), 1);
                message = `Connected client: ${data.id}\n`;
                break;
            case 'disconnected':
                if (selectedClient === data.id) {
                    selectedClient = "";
                }
                clients.splice(clients.indexOf(data.id), 1);
                message = `Disconnected client: ${data.id}\n`;
                break;
            case 'private':
                message = `Private message from '${data.from}': ${data.value}\n`;
                break;
            default:
                message = `Message from '${data.from}': ${data.value}\n`;
                break;
        }

        if (message != "") {
            document.querySelector('chat-ws').getViewShadow('chat-view-connected').addMessage(message)
        }
    }
}

function disconnect() {
    console.log("Socket disconnected")
    socket.close();
    socketConnected = false;
}

// Funcions per a enviar missatges, desconnectar, etc.
function sendMessage(message) {
    socket.send(JSON.stringify(message))
}