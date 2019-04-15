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
     * ø
     *
     * @param string 需要负载的key
     * @return 数据中的负载结果
     */
    String route(String string);

    /**
     * 更新服务列表
     *
     * @param values 服务列表
     */
    void refreshList(List<String> values);
}
