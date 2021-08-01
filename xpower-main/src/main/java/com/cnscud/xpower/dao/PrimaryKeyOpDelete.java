
package com.cnscud.xpower.dao;

import static java.lang.String.format;

/**
 * 通过主键删除数据库记录(等价于调用UPDATE语句)
 * 
 * <pre>
 * dao.update(new PrimaryKeyOpDelete("users", "uid", bizName,1001));
 * 
 * 生成SQL命令： delete from users where uid=?
 * 
 * 参数： (1001)
 * </pre>
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年1月30日
 */
public class PrimaryKeyOpDelete extends OpUpdate {

    /**
     * 通过主键更新单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param primaryKeyName
     *            主键字段名称
     * @param bizName
     *            业务名称
     * @param primaryKeyValue
     *            主键值
     */
    public PrimaryKeyOpDelete(String tableName, //
            String primaryKeyName, //
            String bizName, //
            Object primaryKeyValue) {
        this(tableName, primaryKeyName, bizName, primaryKeyValue, Op.DEFAULT_INDEX);
    }

    /**
     * 通过主键更新单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param primaryKeyName
     *            主键字段名称
     * @param bizName
     *            业务名称
     * @param primaryKeyValue
     *            主键值
     * @param index
     *            业务路由字段
     * @see {@link Op#getRoutePattern()}
     * @since 2016年4月13日
     */
    public PrimaryKeyOpDelete(String tableName, String primaryKeyName, //
            String bizName, //
            Object primaryKeyValue, long index) {
        super(buildSql(tableName, primaryKeyName), bizName, index);
        super.addParams(primaryKeyValue);
    }

    @Override
    public PrimaryKeyOpDelete addParams(Object... params) {
        throw new UnsupportedOperationException();
    }

    static String buildSql(String tableName, String primaryKeyName) {
        return format("delete from %s where %s=?", tableName, primaryKeyName);
    }
}
