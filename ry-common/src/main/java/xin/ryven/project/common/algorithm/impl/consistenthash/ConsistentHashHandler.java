package xin.ryven.project.common.algorithm.impl.consistenthash;

import xin.ryven.project.common.algorithm.RouteHandler;

import java.util.List;

/**
 * @author gray
 */
public class ConsistentHashHandler implements RouteHandler {

    private AbstractConsistentHash consistentHash;

    public void setConsistentHash(AbstractConsistentHash hash) {
        this.consistentHash = hash;
    }

    @Override
    public String route(List<String> strings, String string) {
        return this.consistentHash.process(strings, string);
    }

}
