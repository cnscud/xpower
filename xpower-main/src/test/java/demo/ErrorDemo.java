package demo;

import java.sql.Connection;

import com.cnscud.xpower.dao.DaoFactory;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-12-03
 */
public class ErrorDemo {
    public static void main(String[] args) throws Exception {
        Connection conn = DaoFactory.getIDao().getConnection("error", "", true, null);
        System.out.println("Connection is " + conn);
        conn.close();
    }
}
