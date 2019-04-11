package xin.ryven.project.common.vo.feign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xin.ryven.project.common.vo.ServerAddress;

/**
 * 组装保存用户服务器信息的Body
 *
 * @author gray
 */
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class SaveUserServer {

    private Integer userId;

    private ServerAddress serverAddress;
}
