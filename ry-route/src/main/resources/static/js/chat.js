const TYPE_LOGIN = 1
const TYPE_MSG = 2

let getServerUrl = '/route/serverAddress'
let sendMsgUrl = '/route/msg/send'
let onlineUserUrl = '/route/onlineUsers'

let allMessages = {}

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
        //重新连接
        vue.notify = '连接断开，重新连接中'
        vue.getServerAndConnect()
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
    props: ['username', 'userId', 'active', 'unread'],
    template: '<div class="user-card" v-bind:class="{active:active}">{{ username }}<span class="unread" v-if="unread > 0">{{ unread }}</span></div>'
})

let vue = new Vue({
    el: '#chat',
    data: {
        // users: [{username: '展示', userId: 1}, {username: 'Gray', userId: 1, active: true}],
        users: [],
        messages: [],
        sendMsg: '',
        notify: '',
        user: {},
        chatUser: {}
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
                document.title = this.user.username + '-Chat'
                this.getServerAndConnect()
                //获取用户列表
                this.getOnlineUsers();
            }
        },
        send() {
            let msg = this.sendMsg.trim()
            if (msg) {
                let chatUser = this.chatUser
                if (!chatUser.userId) {
                    console.warn("没有active用户")
                    return
                }
                let msgData = {
                    userId: this.user.userId,
                    username: this.user.username,
                    toUserId: chatUser.userId,
                    toUsername: chatUser.username,
                    content: msg,
                    type: TYPE_MSG
                }
                console.log(msgData)
                axios.post(sendMsgUrl, msgData)
                    .then(function (res) {
                        if (res.status === 200) {
                            var data = res.data
                            if (data.code === 0) {
                                //socket 连接
                                let messages = vue.messages
                                let saveMsg = {'username': vue.user.username, 'msg': msg, 'me': true}
                                messages.push(saveMsg)
                                vue.messages = messages
                                vue.sendMsg = ''
                                saveMessage(vue.chatUser.userId, saveMsg)
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
        received(data) {
            //收到消息作出处理
            switch (data.type) {
                case TYPE_LOGIN:
                    if (data.content === 'OK') {
                        this.notify = '连接成功'
                    }
                    break
                case TYPE_MSG:
                    let fromUser = data.userId
                    //判断是否是当前用户
                    let msg = {'username': data.username, 'msg': data.content}
                    if (vue.chatUser.userId === fromUser) {
                        let messages = vue.messages
                        messages.push(msg)
                        vue.messages = messages
                        //全局
                        saveMessage(fromUser, msg)
                    } else {
                        saveMessage(fromUser, msg)
                        //未读数
                        let users = vue.users
                        for (let i in users) {
                            if (users[i].userId === fromUser) {
                                users[i].unread = users[i].unread + 1
                                break
                            }
                        }
                        vue.users = users
                    }
                default:
            }
        },
        getOnlineUsers() {
            axios.get(onlineUserUrl)
                .then(function (res) {
                    if (res.status === 200) {
                        var data = res.data
                        if (data.code === 0) {
                            let users = data.data
                            var setActive = false
                            let me = vue.user
                            let displayUsers = []
                            for (let i in users) {
                                let u = users[i]
                                if (me.userId === u.userId) {
                                    continue
                                }
                                if (!setActive) {
                                    u.active = true
                                    vue.chatUser = u
                                    setActive = true
                                }
                                u['unread'] = 0
                                displayUsers.push(u)
                            }
                            vue.users = displayUsers
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
        userClick(ele) {
            let users = vue.users
            var change = false
            for (let i in users) {
                let user = users[i]
                if (user.userId === ele) {
                    if (!user.active) {
                        user.active = true
                        user.unread = 0
                        //重载消息
                        vue.messages = allMessages[ele] || []
                        console.log( allMessages[ele])
                        change = true
                        vue.chatUser = user
                    }
                } else {
                    user.active = false
                }
            }
            if (change) {
                vue.users = users
                vue.$forceUpdate()
            }
        }
    }
})

function saveMessage(userId, msg) {
    if (allMessages[userId]) {
        allMessages[userId].push(msg)
    } else {
        allMessages[userId] = [msg]
    }
}

