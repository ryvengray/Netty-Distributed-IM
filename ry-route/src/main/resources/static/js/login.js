let loginUrl = '/route/login'
let registerUrl = '/route/register'
let vue = new Vue({
    el: '#app',
    data: {
        doLogin: true,
        username: '',
        password: ''
    },
    methods: {
        action() {
            if (this.doLogin) {
                this.login()
            } else {
                this.register()
            }
        },
        login: function () {
            var u = this.username.trim()
            let password = this.password.trim()
            if (!u) {
                alert('请输入昵称')
                return
            }
            axios.post(loginUrl, {username: u, password: hex_md5(password)})
                .then(function (res) {
                    if (res.status === 200) {
                        var data = res.data
                        if (data.code === 0) {
                            sessionStorage.setItem('chat:user', JSON.stringify(data.data))
                            window.location.href = 'index.html'
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
        register: function () {
            var u = this.username.trim()
            var password = this.password.trim()
            if (!u || !password) {
                alert('昵称和密码不能为空')
                return
            }
            axios.post(registerUrl, {username: u, password:  hex_md5(password)})
                .then(function (res) {
                    if (res.status === 200) {
                        var data = res.data
                        if (data.code === 0) {
                            alert('注册成功')
                            vue.doLogin = true
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
        }
    }
})