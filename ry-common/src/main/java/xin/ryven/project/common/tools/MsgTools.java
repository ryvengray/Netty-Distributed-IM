package xin.ryven.project.common.tools;

import com.alibaba.fastjson.JSON;
import xin.ryven.project.common.enums.MsgType;
import xin.ryven.project.common.vo.MsgVo;

/**
 * socket 通信的消息的工具类
 *
 * @author gray
 */
public class MsgTools {

    /**
     * 快捷返回login成功或者失败的message
     *
     * @param message 消息
     * @return json
     */
    public static String loginMessage(String message) {
        MsgVo msgVo = new MsgVo();
        msgVo.setUserId(-1);
        msgVo.setType(MsgType.LOGIN.getType());
        msgVo.setContent(message);
        return JSON.toJSONString(msgVo);
    }

}
