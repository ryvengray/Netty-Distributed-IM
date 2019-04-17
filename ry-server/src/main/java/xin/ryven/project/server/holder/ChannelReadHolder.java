package xin.ryven.project.server.holder;

import xin.ryven.project.common.enums.MsgType;
import xin.ryven.project.common.spring.SpringBeanUtils;
import xin.ryven.project.server.service.ChannelReadService;
import xin.ryven.project.server.service.impl.LoginChannelReadServiceImpl;
import xin.ryven.project.server.service.impl.PingChannelReadServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理读取消息的分发处理实现类
 *
 * @author gray
 */
public class ChannelReadHolder {

    private static Map<MsgType, ChannelReadService> channelReadServiceMap =
            new HashMap<MsgType, ChannelReadService>() {
                {
                    put(MsgType.LOGIN, SpringBeanUtils.getBean(LoginChannelReadServiceImpl.class));
                }
            };

    public static ChannelReadService readService(MsgType type) {
        return channelReadServiceMap.get(type);
    }

}
