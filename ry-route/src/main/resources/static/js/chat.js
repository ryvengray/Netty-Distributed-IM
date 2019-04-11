const TYPE_LOGIN = 1
const TYPE_MSG = 2

let getServerUrl = '/route/serverAddress'
let sendMsgUrl = '/route/msg/send'

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
        users: [{username: '展示', userId: 1}, {username: 'Gray', userId: 1, active: true}],
        messages: [{username: '站内', msg: '内容'}, {username: '站内', msg: '内容', me: true}],
        sendMsg: '',
        notify: '',
        user: {}
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            let u = sessionStorage.getItem("chat:user")
            if (!u) {
                window.location.href = "login.html"
            } else {
                this.user = JSON.parse(u)
                this.getServerAndConnect()
            }
        },
        getServerAndConnect() {
            axios.get(getServerUrl + '?username=' + this.user.username + '&userId=' + this.user.userId)
                .then(function (res) {
                    if (res.status === 200) {
                        var data = res.data
                        if (data.code === 0) {
                            //socket 连接
                            let server = data.data
                            imWebSocket = new IMWebSocket(server.protocol + "//" + server.host + ":" + server.port, vue.user)
                        } else {
                            alert(data.msg)
                        }
                    } else {
                        alert(res.statusText)
                    }
                })
                .catch(function (reason) {
                    console.log(reason)
                    alert('服务器错误')
                })
        },
        send() {
            let msg = this.sendMsg.trim()
            if (msg) {
                let userList = this.users
                var toUser
                for (let i in userList) {
                    if (userList[i].active) {
                        toUser = userList[i]
                        break
                    }
                }
                if (!toUser) {
                    console.warn("没有active用户")
                    return
                }
                let msgData = {
                    userId: this.user.userId,
                    username: this.user.username,
                    toUserId: toUser.userId,
                    toUsername: toUser.username,
                    content: msg,
                    type: TYPE_MSG
                }
                axios.post(sendMsgUrl, msgData)
                    .then(function (res) {
                        if (res.status === 200) {
                            var data = res.data
                            if (data.code === 0) {
                                //socket 连接
                                console.log('发送成功')
                                vue.sendMsg = ''
                            } else {
                                alert(data.msg)
                            }
                        } else {
                            alert(res.statusText)
                        }
                    })
                    .catch(function (reason) {
                        console.log(reason)
                        alert('服务器错误')
                    })
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
                case TYPE_MSG:
                    console.log('收到消息:', data.content)
                default:
            }
        }
    }
})

