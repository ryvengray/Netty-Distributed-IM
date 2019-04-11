package xin.ryven.project.common.vo;

import lombok.*;

/**
 * @author gray
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerAddress {

    private String protocol;

    private String host;

    private Integer port;

    private Integer httpPort;
}
