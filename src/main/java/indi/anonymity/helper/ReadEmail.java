package indi.anonymity.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by zp on 04/03/2017.
 */
public class ReadEmail implements SQLExecutor {

    private final String SOURCE = "source";
    private final String TARGET = "target";

    private Connection connection;

    public ReadEmail(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public HashSet<Integer> readAllVertexes() {
        HashSet<Integer> ret = new HashSet<>();
        String sql = "SELECT " + SOURCE + ", " + TARGET + " FROM email";
        try {
            ResultSet rs = executeSQL(sql);
            while (rs.next()) {
                ret.add(rs.getInt(SOURCE));
                ret.add(rs.getInt(TARGET));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    public Map<Integer, ArrayList<Integer>> readEdges(int from, int until) {
        HashMap<Integer, ArrayList<Integer>> ret = new HashMap<>();
        String sql = "SELECT " + SOURCE + ", " + TARGET + " FROM email WHERE timestamp BETWEEN " +
                     from + " AND " + until;
        try {
            ResultSet rs = executeSQL(sql);
            while (rs.next()) {
                ret.putIfAbsent(rs.getInt(SOURCE), new ArrayList<>());
                ret.get(rs.getInt(SOURCE)).add(rs.getInt(TARGET));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
}
