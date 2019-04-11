package xin.ryven.project.server.service;

/**
 * 与路由信息相关
 *
 * @author gray
 */
public interface RouteService {

    /**
     * 下线用户
     *
     * @param userId 用户ID
     */
    void offline(Integer userId);

    /**
     * 用户的路由信息保存
     *
     * @param userId    用户id
     */
    void saveUserChannel(Integer userId);
}
