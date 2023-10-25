let socket;
let socketConnected = false;

function connect(protocol, ip, port) {

    socket = new WebSocket(`${protocol}://${ip}:${port}`)

    socket.onopen = function(e) {
        console.log("Socket connected")
        socketConnected = true
    }
    
    socket.onmessage = function(event) {
        let data = JSON.parse(event.data)
        console.log(data)
        // Processa les dades rebudes segons el seu tipus
        switch(data.type) {
            case 'list':
                // Actualitza la llista de clients
                break
            // altres casos
        }
    }
}

function disconnect() {
    socket.close();
    socketConnected = false;
}

// Funcions per a enviar missatges, desconnectar, etc.
function sendMessage(message) {
    socket.send(JSON.stringify(message))
}