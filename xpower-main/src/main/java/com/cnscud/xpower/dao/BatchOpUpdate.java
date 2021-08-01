package com.cnscud.xpower.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangcong on 14-10-11.
 */
public class BatchOpUpdate extends OpUpdate {
    /**
     * 每次批量参数的大小限制
     */
    private int batchParamSize;

    public BatchOpUpdate(CharSequence sql, String bizName,int batchParamSize) {
        super(sql, bizName);
        this.batchParamSize = batchParamSize;
    }

    public BatchOpUpdate(CharSequence sql, String bizName, long index,int batchParamSize) {
        super(sql, bizName, index);
        this.batchParamSize = batchParamSize;
    }

    protected void setParams(PreparedStatement ps) throws SQLException {
        //super.setParams(ps);
        List<Object> paramList = getParamList();
        if (paramList != null && paramList.size() > 0) {
            int index = 1;
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(index, paramList.get(i));
                if (index == batchParamSize) {
                    ps.addBatch();
                    index = 0;
                }
                index++;
            }
        }
    }
}
