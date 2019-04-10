## 记录过程中遇到的麻烦

* 使用feign client的时候，一直出现找不到Feign，升级了SpringCloud Finchley的版本就好了
* eureka client 一直没法注册到eureka中，找了很久发现eureka-client的依赖弄错了，错误的配置成了`spring-cloud-netflix-eureka-client`，修改为 `spring-cloud-starter-netflix-eureka-client` 才解决