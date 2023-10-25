class ChatViewConnected extends HTMLElement {
    constructor() {
        super()
        this.shadow = this.attachShadow({ mode: 'open' })
    }

    async connectedCallback() {
        // Carrega els estils CSS
        const style = document.createElement('style')
        style.textContent = await fetch('/shadows/chat_view_connected.css').then(r => r.text())
        this.shadow.appendChild(style)
    
        // Carrega els elements HTML
        const htmlContent = await fetch('/shadows/chat_view_connected.html').then(r => r.text())

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
        document.querySelector('chat-ws').showView('viewDisconnecting')
        await new Promise(resolve => setTimeout(resolve, 1500))
        document.querySelector('chat-ws').showView('viewDisconnected')
    }
}

// Defineix l'element personalitzat
customElements.define('chat-view-connected', ChatViewConnected)