package xin.ryven.project.server.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.vo.feign.SaveUserServer;

/**
 * 与路由信息相关
 *
 * @author gray
 */
@FeignClient("im-route")
@Component
public interface RouteService {

    /**
     * 下线用户
     *
     * @param user 用户ID
     */
    @PostMapping("route/user/offline")
    void offline(@RequestBody User user);

    /**
     * 用户的路由信息保存
     *
     * @param saveUserServer 包含userId / ServerAddress
     */
    @PostMapping("route/user/saveServer")
    void saveUserChannel(@RequestBody SaveUserServer saveUserServer);
}
