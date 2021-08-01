/**
 * 
 */
package demo;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSetMetaData;

import com.mysql.jdbc.MysqlIO;
import com.cnscud.xpower.dao.DaoFactory;
import com.cnscud.xpower.dao.DefaultOpList;
import com.cnscud.xpower.dao.IDao;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年3月30日
 */
public class Utf8mb4Demo {
    static String bizName = "im";
    static String tableName = "_im";
   
    public static void main(String[] args) throws Exception {
        if(args.length > 0) {
            bizName = args[0];
        }
        //BasicConfigurator.configure();
        
        IDao dao = DaoFactory.getIDao();
        
        Connection connection = dao.getConnection1(bizName, null, false);
        System.out.println(connection.getClass()+", "+connection);
        Connection conn2 = getField(connection.getClass(),connection, "delegate");
        System.out.println(conn2.getClass()+", "+conn2);
        
        conn2 = getField(org.apache.commons.dbcp.DelegatingConnection.class,conn2, "_conn");
        System.out.println(conn2.getClass()+", "+conn2);
        
        Object serverVariables = getField(com.mysql.jdbc.ConnectionImpl.class,conn2,"serverVariables");
        System.out.println("serverVariables="+serverVariables);
        
        MysqlIO io = getField(com.mysql.jdbc.ConnectionImpl.class, conn2, "io");
        System.out.println("io="+io);
        
        Object serverCharsetIndex = getField(MysqlIO.class, io, "serverCharsetIndex");
        System.out.println("serverCharsetIndex="+serverCharsetIndex);
        System.out.println("---------------------------------------------------------");
        run1();
    }
    static <T> T getField(Class clazz,Object v, String name) throws Exception{
        
        Field f = clazz.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(v);
    }
    
    static void run1() {
        IDao dao = DaoFactory.getIDao();
        dao.update("drop table IF EXISTS "+tableName, bizName);
        dao.update("CREATE TABLE "+tableName+" (`id` int(11) unsigned NOT NULL AUTO_INCREMENT, `msg` varchar(128) DEFAULT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;", bizName);
        
        StringBuilder queryBuf = new StringBuilder().append("SELECT");
        queryBuf.append("  @@session.auto_increment_increment AS auto_increment_increment");
        queryBuf.append(", @@character_set_client AS character_set_client");
        queryBuf.append(", @@character_set_connection AS character_set_connection");
        queryBuf.append(", @@character_set_results AS character_set_results");
        queryBuf.append(", @@character_set_server AS character_set_server");
        queryBuf.append(", @@init_connect AS init_connect");
        queryBuf.append(", @@interactive_timeout AS interactive_timeout");
       
        queryBuf.append(", @@license AS license");
        queryBuf.append(", @@lower_case_table_names AS lower_case_table_names");
        queryBuf.append(", @@max_allowed_packet AS max_allowed_packet");
        queryBuf.append(", @@net_buffer_length AS net_buffer_length");
        queryBuf.append(", @@net_write_timeout AS net_write_timeout");
        queryBuf.append(", @@query_cache_size AS query_cache_size");
        queryBuf.append(", @@query_cache_type AS query_cache_type");
        queryBuf.append(", @@sql_mode AS sql_mode");
        queryBuf.append(", @@system_time_zone AS system_time_zone");
        queryBuf.append(", @@time_zone AS time_zone");
        queryBuf.append(", @@tx_isolation AS tx_isolation");
        queryBuf.append(", @@wait_timeout AS wait_timeout");
        
        
        dao.queryResult(queryBuf, bizName, rs->{
            while(rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    System.out.println(rsmd.getColumnLabel(i)+" -> "+ rs.getString(i));
                }
            }
            return Boolean.TRUE;
        });
        System.out.println("---------------------------------------------------------");
        //
        dao.update("insert into "+tableName+"(msg) values(?)",bizName,"😊👨‍👩‍👧‍👦🏡");
        dao.update("insert into "+tableName+"(msg) values(?)",bizName,"🚌☃️🏝");
        //
        final String imsql = "select msg from "+tableName;
        dao.queryList(new DefaultOpList<>(imsql, bizName, (rs, row) -> {
            return rs.getString(1);
        })).forEach(System.out::println);
        //
        dao.update("drop table IF EXISTS "+tableName, bizName);
        System.out.println("---------------------------------------------------------");

    }

}
