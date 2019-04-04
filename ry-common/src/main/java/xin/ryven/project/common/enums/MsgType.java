package xin.ryven.project.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gray
 */
@AllArgsConstructor
@Getter
public enum MsgType {
    /**
     * 发送的消息类型
     */
    LOGIN(1),
    MESSAGE(2),
    PING(3),
    PONG(4);

    int type;

    /**
     * 通过int类型的type获取类型
     */
    public static MsgType type(int type) {
        for (MsgType m : values()) {
            if (m.type == type) {
                return m;
            }
        }
        return MESSAGE;
    }
}
