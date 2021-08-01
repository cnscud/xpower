/**
 * 
 */
package demo;

import java.util.Arrays;

import com.cnscud.xpower.dao.DaoFactory;
import com.cnscud.xpower.dao.FieldValue;
import com.cnscud.xpower.dao.IDao;
import com.cnscud.xpower.dao.IRowMapper;

/**
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-11-12
 */
public class HsqldbTransactionDemo {

    public static void main(String[] args) throws Exception {
        //
        final String SQL_COUNT = "select count(*) from demo";
        final String SQL_INSERT = "insert into demo values(?,?)";
        //
        final String bizName = "tags.memory";
        final String tableName = "demo";
        // BasicConfigurator.configure();
        // ---------创建表---------
        IDao dao = DaoFactory.getIDao();
        dao.update("create table demo(id INTEGER IDENTITY,name varchar(32))", bizName);
        // ---------添加两条记录---------
        dao.doWithTransaction(bizName, transactionDao -> {
            transactionDao.batchUpdate(SQL_INSERT, Arrays.asList(Arrays.asList(100, "adyliu"), Arrays.asList(101, "tony")));
            Long pk = transactionDao.insert("demo", new FieldValue("id", 999, "name", "玫瑰"));
            System.out.println("pk="+pk);
        });
        // ---------查询结果等于3---------
        int count = dao.queryInt(SQL_COUNT, bizName, 0);
        System.out.printf("count=3 => %d\n", count);
        try {
            dao.doWithTransaction(bizName, transactionDao -> {
                // 新插入一条数据
                transactionDao.update(SQL_INSERT, 103, "vincent");

                int nowCount = transactionDao.queryUniq(SQL_COUNT, IRowMapper.INTEGER);
                System.out.println("now must be 4? -> " + nowCount);

                // 插入一条重复数据
                transactionDao.update(SQL_INSERT, 101, "tony");
            });
        } catch (Exception ex) {
            System.err.println("ERROR:" + ex.getMessage());
        }
        // ---------查询结果依然等于3---------
        count = dao.queryInt(SQL_COUNT, bizName, 0);
        System.out.printf("count=3 => %d\n", count);
        //
        count = dao.delete(bizName, tableName, "id", 100);
        System.out.printf("删除一行 count=1 => %d\n", count);
        //
        count = dao.delete(bizName, tableName, new FieldValue("id", 101, "name", "tony"));
        System.out.printf("又删除一行 count=1 => %d\n", count);
    }

}
