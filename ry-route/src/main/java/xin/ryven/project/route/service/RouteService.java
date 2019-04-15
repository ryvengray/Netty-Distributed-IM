package xin.ryven.project.route.service;

import xin.ryven.project.common.entity.User;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;

import java.util.List;

/**
 * @author gray
 */
public interface RouteService {

    /**
     * 通过一个value获取负载均衡的服务器地址
     *
     * @param value 需要hash的值
     * @return 目标服务器地址
     */
    String routeServer(String value);

    /**
     * 更新服务器地址列表
     *
     * @param servers 服务器地址列表
     */
    void refreshServers(List<String> servers);

    /**
     * 发送消息
     *
     * @param msgVo 消息实体
     */
    void sendMessage(MsgVo msgVo);

    /**
     * 下线用户
     *
     * @param userId 用户ID
     */
    void offline(Integer userId);

    /**
     * 用户的路由信息保存
     *
     * @param userId        用户id
     * @param serverAddress 服务器信息
     */
    void saveUserChannel(Integer userId, ServerAddress serverAddress);

    /**
     * 获取在线用户
     *
     * @return 用户列表
     */
    List<User> onlineUsers();
}
