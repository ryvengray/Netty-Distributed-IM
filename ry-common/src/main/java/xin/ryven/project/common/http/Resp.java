package xin.ryven.project.common.http;

import lombok.Getter;
import lombok.Setter;
import xin.ryven.project.common.enums.Status;

/**
 * @author gray
 */
@Getter
public class Resp<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> Resp<T> status(Status status, T t) {
        Resp<T> resp = new Resp<>();
        resp.setCode(status.getCode());
        resp.setMsg(status.getMsg());
        resp.setData(t);
        return resp;
    }

    public static Resp status(Status status) {
        Resp resp = new Resp();
        resp.setCode(status.getCode());
        resp.setMsg(status.getMsg());
        return resp;
    }

    public Resp setCode(int code) {
        this.code = code;
        return this;
    }

    public Resp setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Resp<T> setData(T data) {
        this.data = data;
        return this;
    }
}
