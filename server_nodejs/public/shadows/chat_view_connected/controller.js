class ChatViewConnected extends HTMLElement {
    constructor() {
        super()
        this.shadow = this.attachShadow({ mode: 'open' })
    }

    async connectedCallback() {
        // Carrega els estils CSS
        const style = document.createElement('style')
        style.textContent = await fetch('/shadows/chat_view_connected/style.css').then(r => r.text())
        this.shadow.appendChild(style)
    
        // Carrega els elements HTML
        const htmlContent = await fetch('/shadows/chat_view_connected/view.html').then(r => r.text())

        // Converteix la cadena HTML en nodes utilitzant un DocumentFragment
        const template = document.createElement('template');
        template.innerHTML = htmlContent;
        
        // Clona i afegeix el contingut del template al shadow
        this.shadow.appendChild(template.content.cloneNode(true));

        // Definir els 'eventListeners' dels objectes 
        this.shadow.querySelector('#buttonDisconnect').addEventListener('click', this.actionDisconnect.bind(this))
    } 

    async actionDisconnect () {
        disconnect()
        document.querySelector('chat-ws').showView('chat-view-disconnecting')
        await new Promise(resolve => setTimeout(resolve, 1500))
        document.querySelector('chat-ws').showView('chat-view-disconnected')
    }

    showInfo () {
        this.shadow.querySelector('#connectionInfo').innerHTML = `Connected to <b>${socket.url}</b>, with ID <b>${socketId}</b>`
    }

    addMessage (message) {
        let refMessages = this.shadow.querySelector('#messages')
        refMessages.innerHTML += message + "<br/>"
        refMessages.scrollTop = refMessages.scrollHeight;
    }
}

// Defineix l'element personalitzat
customElements.define('chat-view-connected', ChatViewConnected)