const TYPE_LOGIN = 1

var imWebSocket = null

class IMWebSocket {

    constructor(uri, user) {
        this.socket = new WebSocket(uri)
        this.user = user
        this.socket.onclose = IMWebSocket.onClose
        this.socket.onopen = IMWebSocket.onOpen
        this.socket.onmessage = IMWebSocket.onMessage
        this.socket.onerror = IMWebSocket.onError
    }

    send(msg) {
        if (typeof msg !== 'string') {
            msg = JSON.stringify(msg)
        }
        this.socket.send(msg)
    }

    static onMessage(evt) {
        if (evt.type === 'message') {
            let msg = evt.data
            //解析消息
            try {
                let received = JSON.parse(msg)
                vue.received(received)
            } catch (e) {
                console.error(e)
            }
        } else {
            console.log("收到消息", evt)
        }
    }

    static onOpen(evt) {
        let data = imWebSocket.user
        data.type = TYPE_LOGIN
        imWebSocket.send(data)
    }


    static onClose(evt) {
        console.log('Close', evt)
    }

    static onError(evt) {
        console.error('error', evt)
    }
}

/**
 * Vue Component
 */
Vue.component('msg', {
    props: ['username', 'content', 'userId', 'me'],
    template: '<div class="flex msg" v-bind:class="{ right:me }">' +
        '<div class="name">{{ username }} </div><div class="content">{{ content }}</div>' +
        '</div>'
})

Vue.component('user-card', {
    props: ['username', 'userId', 'active'],
    template: '<div class="user-card" v-bind:class="{active:active}">{{ username }}</div>'
})

let vue = new Vue({
    el: '#chat',
    data: {
        users: [{username: '展示', userId: 1}, {username: '展示', userId: 1, active: true}],
        messages: [{username: '站内', msg: '内容'}, {username: '站内', msg: '内容', me: true}],
        sendMsg: '',
        notify: ''
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            let u = localStorage.getItem("chat:user")
            if (!u) {
                window.location.href = "login.html"
            } else {
                imWebSocket = new IMWebSocket("ws://localhost:7689", JSON.parse(u))
            }
        },
        send() {
            if (this.sendMsg.trim()) {
                console.log(this.sendMsg)
            } else {
                this.sendMsg = ''
            }
        },
        received(data) {
            //收到消息作出处理
            switch (data.type) {
                case TYPE_LOGIN:
                    if (data.content === 'OK') {
                        this.notify = '连接成功'
                    }
                    break
                default:
            }
        }
    }
})

