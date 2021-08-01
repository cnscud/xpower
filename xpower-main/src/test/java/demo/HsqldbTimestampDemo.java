/**
 * 
 */
package demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import com.cnscud.xpower.dao.DaoFactory;
import com.cnscud.xpower.ddd.DataSourceFactory;

/**
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2016年7月14日
 */
public class HsqldbTimestampDemo {

    public static void main(String[] args) throws Exception {
        final String bizName = "compass";
        Connection conn = DataSourceFactory.getInstance().getWriteConnection(bizName);
        conn.setAutoCommit(true);
        //
        try (PreparedStatement ps = conn.prepareStatement("drop table IF EXISTS test.demo")) {
            ps.execute();
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "CREATE TABLE test.demo (`id` int(11) unsigned NOT NULL AUTO_INCREMENT,`created_at` datetime DEFAULT NULL,PRIMARY KEY (`id`))")) {
            ps.execute();
        }
        try (PreparedStatement ps = conn.prepareStatement("insert into test.demo values(?,?)")) {
            ps.setInt(1, 100);
            ps.setObject(2, LocalDateTime.now());
            ps.execute();
        }
        DaoFactory.getIDao().update("insert into test.demo values(?,?)", bizName, 123456, LocalDateTime.now());
        //
        try (PreparedStatement ps = conn.prepareStatement("select * from test.demo"); //
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " => " + rs.getObject(2));
            }
        }
        conn.close();
        //
        List<?> r = DaoFactory.getIDao().queryList("select * from test.demo", bizName, (rs, row) -> rs.getObject(2));
        System.out.println(r);
    }

}
