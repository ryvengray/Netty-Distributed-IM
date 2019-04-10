package xin.ryven.project.common.vo;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author gray
 */
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MsgVo {

    @NotNull(message = "User id can't be null")
    private Integer userId;

    private String userName;

    private Integer toUserId;

    @NotNull(message = "Type can't be null")
    private Integer type;

    private String content;

}
