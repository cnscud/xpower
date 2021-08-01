package com.cnscud.xpower.knife.lambda;

import com.cnscud.xpower.knife.IPage;
import com.cnscud.xpower.knife.impl.Pagination;
import com.cnscud.xpower.knife.impl.TableCache;
import com.cnscud.xpower.knife.impl.TableInfo;
import com.cnscud.xpower.knife.impl.TableInfoMapper;
import com.cnscud.xpower.dao.DaoFactory;
import com.cnscud.xpower.dao.IDao;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author mie peng 2021-02-22 15:21
 * @version 1.0.0
 */
public class LambdaService {

    private TableInfo table;
    private String bizName;
    private String tableName;
    private IDao dao = DaoFactory.getIDao();


    public <T> LambdaService(Class<T> clazz,String bizName) {
        this.table = TableCache.get(clazz);
        this.tableName = table.getTableName();
        this.bizName = bizName;
    }

    public <T> T getOne(LambdaQuery<T> lambdaQuery){
        return dao.queryUniq(wrapperSql(LambdaEnum.getOne,lambdaQuery), bizName, new TableInfoMapper<>(table),lambdaQuery.params);
    }

    public <T> List<T> list(LambdaQuery<T> lambdaQuery){
        return dao.queryList(wrapperSql(LambdaEnum.list,lambdaQuery), bizName, new TableInfoMapper<>(table),lambdaQuery.params);
    }

    public <T> IPage<T> page(LambdaQuery<T> lambdaQuery,String orderByColumn,boolean desc, long page, long pageSize) {
        final long count = dao.queryLong(wrapperSql(LambdaEnum.count, lambdaQuery), bizName, 0, lambdaQuery.params);
        Pagination<T> paging = new Pagination<>(page, pageSize, count);
        StringBuilder sql = new StringBuilder(wrapperSql(LambdaEnum.list, lambdaQuery));
        if (StringUtils.isNotBlank(orderByColumn)) {
            sql.append(String.format(" order by %s %s", orderByColumn, desc ? "DESC" : "ASC"));
        }
        sql.append(String.format(" limit %s , %s", paging.offset(), paging.limit()));
        List<T> list = dao.queryList(sql, bizName, new TableInfoMapper<>(table), lambdaQuery.params);
        paging.getList().addAll(list);
        return paging;
    }

    public <T> boolean  update(LambdaUpdate<T> lambdaUpdate){
        return dao.update(wrapperSql(LambdaEnum.update,lambdaUpdate),bizName,lambdaUpdate.params) > 0;
    }




    private String wrapperSql(LambdaEnum le,LambdaQuery lambdaQuery){
        return prepareSql(le)+lambdaQuery.sb.toString();
    }

    private String wrapperSql(LambdaEnum le,LambdaUpdate lambdaUpdate){
        return prepareSql(le)+lambdaUpdate.sb.toString();
    }

    private String prepareSql(LambdaEnum le){
        switch (le){
            case getOne:
            case list:
                return "select * from "+ tableName +" where 1";
            case update:
                return "update " + tableName + " set ";
            case count:
                return "select count(*) from "+ tableName +" where 1";
        }
        return null;
    }

    private String validatorSql(String sql){
        if (sql.contains("set") && !sql.contains("and")){
            throw new RuntimeException();
        }
        return null;
    }
}
