package indi.anonymity.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by zp on 04/03/2017.
 */
public interface SQLExecutor {

    Connection getConnection();

    default ResultSet executeSQL(String sql) throws SQLException {
        Statement stmt = getConnection().createStatement();
        return stmt.executeQuery(sql);
    }
}
