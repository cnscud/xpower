package com.cnscud.xpower.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 批量SQL操作
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年3月29日
 */
public class OpBatchUpdate extends OpUpdate {

    private List<Collection<?>> paramsLists = new ArrayList<>();

    /**
     * @param sql
     * @param bizName
     * @param index
     */
    public OpBatchUpdate(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    /**
     * @param sql
     * @param bizName
     */
    public OpBatchUpdate(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    @Override
    public final OpUpdate addParams(Object... params) {
        throw new UnsupportedOperationException();
    }

    public OpBatchUpdate addParamList(Collection<?> oneRecordParamList) {
        this.paramsLists.add(oneRecordParamList);
        return this;
    }
    public OpBatchUpdate addParamLists(Collection<Collection<?>> allRecordParamLists) {
        this.paramsLists.addAll(allRecordParamLists);
        return this;
    }
    @Override
    protected final void setParams(PreparedStatement ps) throws SQLException {
        for (Collection<?> paramList : paramsLists) {
            int index = 1;
            for (Object param : paramList) {
                ps.setObject(index, param);
                index++;
            }
            ps.addBatch();
        }
    }
}
