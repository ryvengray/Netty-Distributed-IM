package xin.ryven.project.common.algorithm.impl.consistenthash;

import xin.ryven.project.common.algorithm.RouteService;

import java.util.List;

/**
 * @author gray
 */
public class ConsistentHashHandler implements RouteService {

    private AbstractConsistentHash consistentHash;

    public void setConsistentHash(AbstractConsistentHash hash) {
        this.consistentHash = hash;
    }

    @Override
    public String route(List<String> strings, String string) {
        return this.consistentHash.process(strings, string);
    }

}
