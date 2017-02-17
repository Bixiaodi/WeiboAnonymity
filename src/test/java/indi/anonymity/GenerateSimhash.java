package indi.anonymity;

import indi.anonymity.algorithm.Simhash;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.DatabaseConnector;
import indi.anonymity.helper.ReadVertex;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by emily on 17/2/16.
 */
public class GenerateSimhash {
    private Connection connection;
    private DatabaseConnector connector;
    public GenerateSimhash() {
        new DatabaseConnector();
        try {
            connector = new DatabaseConnector();
            connector.connect();
            this.connection = connector.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String[] read(int id) {
        ReadVertex readVertex = new ReadVertex(this.connection);
        return readVertex.readById(id).toArray();

    }
    public void write() {
        ArrayList<String> bits = new ArrayList<>();
        for(int i = 8; i <= 64; i *= 2){
            bits.add("simhash" + i);
        }
        for(int i = 1; i <= Vertex.TOTAL; i++) {
            String[] vertexInfo = read(i);
            for (int j = 0, bit = 8; j < bits.size(); j++, bit *= 2) {
                Simhash curSimhash = new Simhash(vertexInfo, bit);
                String sql = "update user_info set " + bits.get(j) + " = \'" + curSimhash.getStrSimhash() + "\' where id = " + i;
                try {
                    Statement stmt = connection.createStatement();
                    int rs = stmt.executeUpdate(sql);
                    if ((rs < 1) || (curSimhash.getStrSimhash().length() != bit)) {
                        System.out.println("fail : id = " + i + " bit = " + bit);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(i % 1000 == 0) {
                System.out.println("finish = " + i);
            }
        }
        connector.stopConnection();
    }
}
