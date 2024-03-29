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
 * 测试.
 * 请先在zookeeper里面设置/xpower/dbn/dbn.test节点, 内容参考dbn.test.json.
 *
 */
public class SimpleDBNDemo {

    public static void main(String[] args) throws Exception {
        final String bizName = "dbn.test";
        Connection conn = SimpleDBNDataSourceFactory.getInstance().getDataSource(bizName).getConnection();
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
