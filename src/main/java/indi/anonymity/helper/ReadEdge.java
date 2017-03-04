package indi.anonymity.helper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by emily on 17/2/20.
 */
public class ReadEdge implements SQLExecutor {

    private final String SOURCE_ID = "userId";
    private final String TARGET_ID = "followUserId";

    private Connection connection;

    public ReadEdge(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public ArrayList<String> readEdgeBySource(String source) {
        ArrayList<String> target = new ArrayList<>();
        String sql = "SELECT * FROM user_follow WHERE userId = " + source;
        try {
            ResultSet rs = executeSQL(sql);
            while (rs.next()) {
                target.add(rs.getString(TARGET_ID));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return target;
    }

    public HashMap<String, String> readEdgeByUrlId(String urlId) {
        String sql = "SELECT * FROM user_follow WHERE userId = '" + urlId +
                "' OR followUserId = '" + urlId + "'";
        try {
            return buildEdges(executeSQL(sql));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public HashMap<String, String> readEdgeWithinUserId(String IDString) {
        String sql = "SELECT * FROM user_follow uf WHERE uf.userId IN " + IDString +
                     "AND uf.followUserId IN " + IDString;
        try {
            return buildEdges(executeSQL(sql));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private HashMap<String, String> buildEdges(ResultSet rs) throws SQLException {
        HashMap<String, String> ret = new HashMap<>();
        while (rs.next()) {
            ret.put(rs.getString(2), rs.getString(3));
        }
        return ret;
    }


}
