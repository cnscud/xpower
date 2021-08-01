/**
 * 
 */
package demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cnscud.xpower.dao.DaoFactory;
import com.cnscud.xpower.dao.DefaultOpList;
import com.cnscud.xpower.dao.IDao;
import com.cnscud.xpower.dao.OpWithConnection;
import com.cnscud.xpower.dao.PrimaryKeyOpInsert;
import com.cnscud.xpower.dao.PrimaryKeyOpUpdate;
import com.cnscud.xpower.ddd.DataSourceFactory;

/**
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-11-12
 */
public class HsqldbDemo {

    public static void main(String[] args) throws Exception {
        final String bizName = "tags.memory";
        Connection conn = DataSourceFactory.getInstance().getWriteConnection(bizName);
        conn.setAutoCommit(true);
        //
        PreparedStatement ps = conn.prepareStatement("create table demo(id INTEGER IDENTITY,name varchar(32),age INTEGER)");
        ps.execute();
        ps.close();
        ps = conn.prepareStatement("insert into demo values(100,'Ady Liu',1)");
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
        IDao dao = DaoFactory.getIDao();
        // ----------------------------单数据库/连接事务---------------------------
        dao.doWithConnection(new OpWithConnection(bizName, false) {
            @Override
            protected Boolean doInConnection(Connection conn) throws SQLException {
                conn.setAutoCommit(false);
                try (Statement st = conn.createStatement()) {
                    st.execute("insert into demo values(102,'Ady Liu'),1");
                    st.execute("insert into demo values(102,'Ady Liu'),1");
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    // e.printStackTrace();
                }
                return Boolean.TRUE;
            }
        });
        //

        //
        // ----------------------------多数据库/连接事务---------------------------
        try (Connection conn1 = dao.getConnection(bizName, null, false, null); Connection conn2 = dao.getConnection(bizName, null, false, null)) {
            try {
                // 干活
                conn1.commit();
                conn2.commit();
            } catch (SQLException ex) {
                conn1.rollback();
                conn2.rollback();
                throw ex;
            }

        }

        // ---------------------------主键更新简单操作---------------------------
        dao.update(new PrimaryKeyOpUpdate("demo", "id", bizName, "name", "age").addParams("admin", 28, 100));
        Long tonyId = dao.insert(new PrimaryKeyOpInsert("demo", bizName, "name", "age").addParams("Tony", 27));
        //
        System.out.println("tonyId=" + tonyId);
        dao.queryList(new DefaultOpList.IntegerList("select age from demo", bizName)).stream().forEach(System.out::println);

        // ---------------------------单连接事务操作---------------------------
        try {
            dao.doWithTransaction(new OpWithConnection<Void>(bizName, false) {
                @Override
                protected Void doInConnection(Connection conn) throws SQLException {
                    try (Statement st = conn.createStatement()) {
                        st.execute("insert into demo values(112,'Ady Liu'),1");
                        st.execute("insert into demo values(112,'Ady Liu'),1");
                    }
                    return null;
                }
            });
        } catch (Exception se) {
            //
        }
        //

    }

}
