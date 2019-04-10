package xin.ryven.project.route.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.common.vo.MsgVo;

/**
 * @author gray
 */
@FeignClient(value = "im-server")
@Component
public interface ServerService {

    /**
     * 发送消息
     *
     * @param msgVo 消息实体
     * @return 消息发送结果
     */
    @PostMapping("server/msg/send")
    Resp sendMessage(MsgVo msgVo);
}
