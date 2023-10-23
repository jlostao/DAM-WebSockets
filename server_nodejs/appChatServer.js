// chatServer.js

class Obj {
    constructor(ws) {
        this.ws = ws;
    }
  
    init(httpServer) {
        this.ws.init(httpServer);
        this.ws.onConnection = (socket, id) => this.handleConnection(socket, id);
        this.ws.onMessage = (socket, id, obj) => this.handleMessage(socket, id, obj);
        this.ws.onClose = (socket, id) => this.handleClose(socket, id);
    }
  
    handleConnection(socket, id) {
        console.log("WebSocket client connected: " + id);

        // Saludem personalment al nou client
        socket.send(JSON.stringify({
            type: "private",
            from: "server",
            value: "Welcome to the chat server"
        }));

        // Li enviem el seu identificador
        socket.send(JSON.stringify({
            type: "id",
            from: "server",
            value: id
        }));

        // Enviem al client la llista amb tots els clients connectats
        socket.send(JSON.stringify({
            type: "list",
            from: "server",
            list: this.ws.getClients()
        }));

        // Enviem la direcció URI del nou client a tothom 
        this.ws.broadcast(JSON.stringify({
            type: "connected",
            from: "server",
            id: id
        }));
    }
  
    handleMessage(socket, id, { type, destination, value }) {
        switch (type) {
        case "list":
            socket.send(JSON.stringify({
                type: "list",
                from: "server",
                list: this.ws.getClients()
            }));
            break;
        case "private":
            let destSocket = this.ws.getClientById(destination);
            if (destSocket) {
                destSocket.send(JSON.stringify({
                    type: "private",
                    from: id,
                    value: value
                }));
            } else {
                console.error(`Destination client ${destination} not found.`);
            }
            break;
        case "broadcast":
            this.ws.broadcast(JSON.stringify({
                type: "broadcast",
                from: id,
                value: value
            }));
            break;
        }
    }
  
    handleClose(socket, id) {
        console.log("WebSocket client disconnected: " + id);
        this.ws.broadcast(JSON.stringify({
            type: "disconnected",
            from: "server",
            id: id
        }));
    }
}

module.exports = Obj;