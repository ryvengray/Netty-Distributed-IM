package xin.ryven.project.route.service;

import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;

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
     * 发送消息
     *
     * @param msgVo 消息实体
     * @return 消息发送结果
     */
    void sendMessage(MsgVo msgVo);
}
