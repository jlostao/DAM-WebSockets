class ChatWS extends HTMLElement {
    constructor() {
        super()
        this.shadow = this.attachShadow({ mode: 'open' })
        this.view = "chat-view-disconnected"
    }

    async connectedCallback() {
        // Carrega els estils CSS
        const style = document.createElement('style')
        style.textContent = await fetch('/shadows/chat.css').then(r => r.text())
        this.shadow.appendChild(style)
    
        // Carrega els elements HTML
        const htmlContent = await fetch('/shadows/chat.html').then(r => r.text())

        // Converteix la cadena HTML en nodes utilitzant un DocumentFragment
        const template = document.createElement('template');
        template.innerHTML = htmlContent;
        
        // Clona i afegeix el contingut del template al shadow
        this.shadow.appendChild(template.content.cloneNode(true));
/*
        // Definir els 'eventListeners' dels objectes 
        // NO es pot fer des de l'HTML, al ser shadow no funciona
        // Es recomana fer-ho amb '.bind(this, paràmetres ...)' per simplificar les crides a les funcions
        this.shadow.querySelector('#infoBtnLogOut').addEventListener('click', this.actionLogout.bind(this))
        this.shadow.querySelector('#loginForm').addEventListener('submit', this.actionLogin.bind(this))
        this.shadow.querySelector('#loginBtn').addEventListener('click', this.actionLogin.bind(this))
        this.shadow.querySelector('#loginShowSignUpForm').addEventListener('click', this.showView.bind(this, 'viewSignUpForm', 'initial'))
        this.shadow.querySelector('#signUpForm').addEventListener('submit', this.actionLogin.bind(this))
        this.shadow.querySelector('#signUpPassword').addEventListener('input', this.checkSignUpPasswords.bind(this))
        this.shadow.querySelector('#signUpPasswordCheck').addEventListener('input', this.checkSignUpPasswords.bind(this))
        this.shadow.querySelector('#signUpBtn').addEventListener('click', this.actionSignUp.bind(this))
        this.shadow.querySelector('#signUpShowLoginForm').addEventListener('click', this.showView.bind(this, 'viewLoginForm', 'initial'))

        // Automàticament, validar l'usuari per 'token' (si n'hi ha)
        await this.actionCheckUserByToken()*/
    } 

    async showView (viewName) {
        // Amagar totes les vistes
        let animTime = 500;
        let refDisconnected = this.shadow.querySelector('chat-view-disconnected')
        let refConnecting = this.shadow.querySelector('chat-view-connecting')
        let refDisconnecting = this.shadow.querySelector('chat-view-disconnecting')
        let refConnected = this.shadow.querySelector('chat-view-connected')

        // Mostrar la vista seleccionada, amb l'status indicat
        switch (viewName) {
        case 'viewDisconnected':

            break
        case 'viewConnecting':
            this.animateViews('in', animTime, refDisconnected, refConnecting)
            break
        case 'viewDisconnecting':

            break
        case 'viewConnected':

            break
        }
    }

    async animateViews(type, animTime, refView0, refView1) {

        let refRoot0 = refView0.shadowRoot.querySelector(".root")
        let refRoot1 = refView1.shadowRoot.querySelector(".root")

        refRoot1.style.display = "flex"
        refRoot1.style.transition = "unset"
        refRoot1.style.transform = "translate3d(100%, 0, 0)"

        await new Promise(resolve => setTimeout(resolve, 1))

        refRoot0.style.transition = "transform " + animTime + "ms ease-in"

        if (type == 'in') {
            refRoot0.style.transform = "translate3d(-100%, 0, 0)"
            refRoot1.style.transform = "translate3d(0, 0, 0)"
        } else {
            refRoot0.style.transform = "translate3d(0, 0, 0)"
            refRoot1.style.transform = "translate3d(100%, 0, 0)"
        }

        await new Promise(resolve => setTimeout(resolve, 1))

        refRoot0.style.transform = "translate3d(0, 0, 0)"
        refRoot0.style.display = "none"
        refRoot0.style.transition = "unset"
        refRoot1.style.transition = "unset"
    }
}

// Defineix l'element personalitzat
customElements.define('chat-ws', ChatWS)