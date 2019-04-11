package xin.ryven.project.common.algorithm;

import java.util.List;

/**
 * 服务器路由操作
 *
 * @author gray
 */
public interface RouteHandler {

    /**
     * 根据路由算法，从列表中获取一个字符串
     *
     * @param strings 数据
     * @param string  需要负载的key
     * @return 数据中的负载结果
     */
    String route(List<String> strings, String string);
}
