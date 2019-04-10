var loginUrl = '/route/login'
new Vue({
    el: '#app',
    data: {
        username: ''
    },
    methods: {
        login: function () {
            var u = this.username.trim()
            if (!u) {
                alert('请输入昵称')
                return
            }
            axios.post(loginUrl, {username: u})
                .then(function (res) {
                    if (res.status === 200) {
                        var data = res.data
                        if (data.code === 0) {
                            localStorage.setItem('chat:user', JSON.stringify(data.data))
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
        }
    }
})