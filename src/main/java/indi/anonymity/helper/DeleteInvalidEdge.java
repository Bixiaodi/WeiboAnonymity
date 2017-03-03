package indi.anonymity.helper;

import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

/**
 * Created by emily on 17/3/2.
 */
public class DeleteInvalidEdge {
    private  Connection connection;
    public DeleteInvalidEdge(Connection connection) {
        this.connection = connection;
    }
    public void record() throws FileNotFoundException {
        File index = new File("index.txt");
        PrintWriter output = new PrintWriter(index);
        String sqlVertexUrl = "select userUrl from user_info";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet urlResult = stmt.executeQuery(sqlVertexUrl);
            HashSet<String> urls = new HashSet<>();
            while (urlResult.next()) {
                urls.add(urlResult.getString(1));
            }
            System.out.println(urls.size());
            String sqlEdge = "select * from user_follow";
            ResultSet edgeResult = stmt.executeQuery(sqlEdge);
            while(edgeResult.next()) {
                String userId = edgeResult.getString(2);
                String followUserId = edgeResult.getString(3);
                if(!urls.contains(userId) || !urls.contains(followUserId)) {
                    output.println(edgeResult.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        output.close();
    }
}
