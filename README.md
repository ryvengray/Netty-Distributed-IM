### 基于 Netty 的聊天系统后台

#### 项目将会使用到
- SpringBoot
- SpringCloud
- Netty
- WebSocket
- Vue.js
- Zookeeper
- Redis

##### 访问流程
![avatar](resource/process.jpg)

> 1、前端访问route路由登录，登录成功返回在线用户列表，同时返回该用户需要连接的服务器地址

> 2、连接 Netty 服务器

> 3、消息通过route发送，route根据负载均衡算法找到服务器发送


#### 启动项目
> 本地需要启动 zookeeper、redis，配置好地址密码

> 1、启动 ry-eureka 服务

> 2、启动配置中心 ry-config-server 服务

> 3、启动 ry-server 服务, ry-user 服务, ry-route 服务 （gateway 可以忽略）

也可以直接使用脚本 all-start.sh ，注意几个项目用到的端口不要被占用,
如果出现异常不能访问，到logs下查看日志文件

****
- 添加IM服务器  `./server-add.sh <im-port> <http-port>` 如 `./server-add.sh 9001 10020`
- 添加服务  `./jar-start.sh <server-name> <http-port>` 如 `./jar-start.sh ry-user 9999`