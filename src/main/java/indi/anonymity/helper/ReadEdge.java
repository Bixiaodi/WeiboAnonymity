package indi.anonymity.helper;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by emily on 17/2/20.
 */
public class ReadEdge {

    private final String SOURCE_ID = "userId";
    private final String TARGET_ID = "followUserId";

    private Connection connection;

    public ReadEdge(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<String> readEdgeBySource(String source) {
        ArrayList<String> target = new ArrayList<>();
        String sql = "select * from user_follow where userId = " + source;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                target.add(rs.getString(TARGET_ID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return target;
    }

    public ArrayList<String> readEdgeByTarget(String target) {
        ArrayList<String> source = new ArrayList<>();
        String sql = "select * from user_follow where followUserId = " + target;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                source.add(rs.getString(SOURCE_ID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return source;
    }



}
