package xin.ryven.project.common.algorithm.impl.consistenthash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 多个一致性hash算法的抽象类
 *
 * @author gray
 */
public abstract class AbstractConsistentHash {

    /**
     * 新增节点
     *
     * @param key   key
     * @param value 节点
     */
    protected abstract void add(long key, String value);

    protected void sort() {
    }

    /**
     * 一致性hash算法获取第一个值
     *
     * @param value 待计算的值
     * @return 计算结果
     */
    protected abstract String getFirst(String value);

    /**
     * 清空列表的内容
     */
    protected abstract void clear();

    /**
     * 算法执行主流程
     *
     * @param value 待计算的value
     * @return 算法结果
     */
    public String process(String value) {
        return getFirst(value);
    }

    /**
     * 更新已有的数据列表，并进行排序
     *
     * @param list 数据列表
     */
    public void refreshList(List<String> list) {
        this.clear();
        list.forEach(s -> add(hash(s), s));
        sort();
    }

    /**
     * hash算法
     *
     * @param value 待hash的值
     * @return hash结果
     */
    public long hash(String value) {

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes;
        keyBytes = value.getBytes(StandardCharsets.UTF_8);

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        return hashCode & 0xffffffffL;
    }

}
