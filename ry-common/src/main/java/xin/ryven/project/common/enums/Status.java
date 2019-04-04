package xin.ryven.project.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gray
 */
@AllArgsConstructor
@Getter
public enum Status {
    /**
     * 返回的状态码
     */
    OK(0, "Success"),
    UN_LOGIN(1, "Not logged in"),
    FAILED(65, "Failed");


    private int code;

    private String msg;


}
