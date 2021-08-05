/**
 *
 */
package demo.dbn;

import com.cnscud.xpower.dbn.SimpleDBNConnectionPool;
import com.cnscud.xpower.dbn.SimpleDBNDataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author adyliu(imxylz @ gmail.com)
 * @since 2012-11-12
 */
public class SimpleDBNDemo {

    public static void main(String[] args) throws Exception {
        final String bizName = "dbn.test";
        Connection conn = SimpleDBNDataSourceFactory.getInstance().getConnection(bizName);
        conn.setAutoCommit(true);
        //
        PreparedStatement ps = conn.prepareStatement("create table IF NOT EXISTS demo(id INT,name varchar(32),age int)");
        ps.execute();
        ps.close();
        ps = conn.prepareStatement("insert ignore into demo values(100,'Ady Liu',1)");
        ps.execute();
        ps.close();
        //
        ps = conn.prepareStatement("select * from demo");
        ResultSet rs = ps.executeQuery();
        rs.next();
        System.out.println(rs.getInt(1) + " => " + rs.getString(2));
        System.out.println(conn);
        //
        ps.close();
        rs.close();
        conn.close();
        //

    }

}
