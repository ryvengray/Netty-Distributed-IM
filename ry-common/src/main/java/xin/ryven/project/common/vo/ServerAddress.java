package xin.ryven.project.common.vo;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * @author gray
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ServerAddress {

    private String protocol;

    private String host;

    private Integer port;

    private Integer httpPort;

    /**
     * 本地的地址，用于route发送http请求使用
     */
    private String localeHost;

    /**
     * 设置host的值
     */
    public void setDynamicHost() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            localeHost = address.getHostAddress();
        } catch (Exception e) {
            log.error("获取IP地址失败", e);
            host = "127.0.0.1";
            localeHost = host;
        }
    }
}
