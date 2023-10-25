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
        let animTime = '500ms';
        let refDisconnected = this.shadow.querySelector('chat-view-disconnected').shadowRoot.querySelector('.root')
        let refConnecting = this.shadow.querySelector('chat-view-connecting').shadowRoot.querySelector('.root')
        let refDisconnecting = this.shadow.querySelector('chat-view-disconnecting').shadowRoot.querySelector('.root')
        let refConnected = this.shadow.querySelector('chat-view-connected').shadowRoot.querySelector('.root')

        // Mostrar la vista seleccionada, amb l'status indicat
        switch (viewName) {
        case 'viewDisconnected':
            if (this.view == 'viewConnecting') {
                this.animateViewChange('out', animTime, refConnecting, refDisconnected)
            }
            if (this.view == 'viewDisconnecting') {
                this.animateViewChange('out', animTime, refDisconnecting, refDisconnected)
            }
            break
        case 'viewConnecting':
            this.animateViewChange('in', animTime, refDisconnected, refConnecting)
            break
        case 'viewDisconnecting':
            this.animateViewChange('out', animTime, refConnected, refDisconnecting)
            break
        case 'viewConnected':
            this.animateViewChange('in', animTime, refConnecting, refConnected)
            break
        }
        this.view = viewName
    }

    async animateViewChange (type, animTime, view0, view1) {
        if (type == 'out') {
            await Promise.all([
                this.animateElement(view0, animTime, "translate3d(0, 0, 0)", "translate3d(100%, 0, 0)"),
                this.animateElement(view1, animTime, "translate3d(-100%, 0, 0)", "translate3d(0%, 0, 0)")
            ])
        } else {
            await Promise.all([
                this.animateElement(view0, animTime, "translate3d(0, 0, 0)", "translate3d(-100%, 0, 0)"),
                this.animateElement(view1, animTime, "translate3d(100%, 0, 0)", "translate3d(0%, 0, 0)")
            ])
        }
    }

    async animateElement(element, animTime, posBegin, posEnd) {
        element.style.display = 'flex';
        element.style.transition = 'none';
    
        element.style.transform = posBegin;
    
        // Add small delay to ensure element is positioned before animating
        setTimeout(() => {
            let transition = 'transform ' + animTime + ' ease 0s';
            element.style.transition = transition;
    
            element.style.transform = posEnd;
        }, 100);
    }
}

// Defineix l'element personalitzat
customElements.define('chat-ws', ChatWS)