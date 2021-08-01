/**
 * 
 */
package com.cnscud.xpower.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 通过主键更新指定字段的值(等价于调用UPDATE语句)
 * 
 * <pre>
 * dao.update(new PrimaryKeyOpUpdate("users", "uid", bizName, "name", "age").addParams("adyliu", 28, 1001));
 * 
 * 生成SQL命令： update users set name=?,age=? where uid=?
 * 
 * 参数： ('adyliu', 28, 1001)
 * </pre>
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年1月30日
 */
public class PrimaryKeyOpUpdate extends OpUpdate {
    /**
     * 通过主键更新单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param primaryKeyName
     *            主键字段名称
     * @param bizName
     *            业务名称
     * @param updateFieldNames
     *            要更新的字段
     */
    public PrimaryKeyOpUpdate(String tableName, //
            String primaryKeyName,//
            String bizName, //
            String... updateFieldNames) {
        this(tableName, primaryKeyName, bizName, Arrays.asList(updateFieldNames));
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
     * @param updateFieldNames
     *            要更新的字段
     */
    public PrimaryKeyOpUpdate(String tableName, //
            String primaryKeyName,//
            String bizName, //
            Collection<String> updateFieldNames) {
        this(tableName, primaryKeyName, bizName, updateFieldNames, Op.DEFAULT_INDEX);
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
     * @param updateFieldNames
     *            要更新的字段
     * @param index
     *            业务路由字段
     * @see {@link Op#getRoutePattern()}
     */
    public PrimaryKeyOpUpdate(String tableName, String primaryKeyName,//
            String bizName, //
            Collection<String> updateFieldNames, long index) {
        super(buildSql(tableName, primaryKeyName, Function.identity(), updateFieldNames.stream()), bizName, index);
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
     * @param updateFieldNames
     *            要更新的字段
     * @param index
     *            业务路由字段
     * @see {@link Op#getRoutePattern()}
     * @since 2015年4月24日
     */
    public PrimaryKeyOpUpdate(String tableName, String primaryKeyName,//
            String bizName, //
            List<String> updateFieldNames, long index,//
            Function<String, String> where//
    ) {
        super(buildSql(tableName, primaryKeyName, where, updateFieldNames.stream()), bizName, index);
    }

    /**
     * 预编译参数的个数必须是 updateFieldNames 个数+1（位置相同），最后一个参数是主键的值
     */
    @Override
    public PrimaryKeyOpUpdate addParams(Object... params) {
        return (PrimaryKeyOpUpdate) super.addParams(params);
    }

    static String buildSql(String tableName, String primaryKeyName,//
            Function<String, String> where,//
            String... updateFieldNames) {
        return buildSql(tableName, primaryKeyName, where, Arrays.stream(updateFieldNames));
    }

    static String buildSql(String tableName, String primaryKeyName,//
            Function<String, String> where,//
            Stream<String> updateFieldNames) {
        return where.apply(String.format("update %s set %s where %s=?",//
                tableName,//
                updateFieldNames.collect(Collectors.joining("=?,", "", "=?")),//
                primaryKeyName));
    }
}
