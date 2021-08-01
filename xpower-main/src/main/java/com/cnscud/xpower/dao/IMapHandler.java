package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 单一Map结果集(如果ResultSet为空，则返回Map为空）
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年10月17日
 */
@FunctionalInterface
public interface IMapHandler<K, V> extends IResultHandler<Map<K, V>> {
    @Override
    default Map<K, V> parse(ResultSet rs) throws SQLException {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        while (rs.next()) {
            putMap(map, rs);
        }
        return map;
    }

    /**
     * 解析唯一一行记录，设置Map对象
     * 
     * @param map
     *            填充和最终返回的Map对象
     * @param rs
     *            结果集，已经掉用过{@link ResultSet#next()}}
     * @throws SQLException
     *             任何SQL异常
     */
    void putMap(LinkedHashMap<K, V> map, ResultSet rs) throws SQLException;
}
