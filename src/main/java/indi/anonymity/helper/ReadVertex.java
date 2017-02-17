package indi.anonymity.helper;

import indi.anonymity.elements.Vertex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by emily on 17/2/15.
 */
public class ReadVertex {
    private int count;
    private Connection connection;
    private final String ID = "id";
    private final String USER_URL = "userUrl";
    private final String USER_NAME = "userName";
    private final String GENDER = "gender";
    private final String USER_LOC = "userLoc";
    private final String DESCRIPTION = "description";
    private final String EDU_INFO = "eduInfo";
    private final String USER_TAG = "userTag";

    public ReadVertex(Connection connection) {
        this.connection = connection;
    }

    public ReadVertex(int count) {
        this.count = count;
        DatabaseConnector connector = new DatabaseConnector();
        try {
            connector.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.connection = connector.getConnection();
    }

    public Vertex fillProperty(ResultSet rs) {
        Vertex v = new Vertex();
        try {
            v.setId(rs.getInt(ID));
            v.setUrlId(rs.getString(USER_URL));
            v.setUserName(rs.getString(USER_NAME));
            v.setGender(rs.getInt(GENDER));
            v.setLocation(rs.getString(USER_LOC));
            v.setUserName(rs.getString(USER_TAG));
            v.setDescription(rs.getString(DESCRIPTION));
            v.setEducationInformation(rs.getString(EDU_INFO));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }

    public Vertex readById(int id) {
        Vertex v = new Vertex();
        String sql = "select * from user_info where id = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                v = fillProperty(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }

    public ArrayList<Vertex> readRandomly() {
        ArrayList<Vertex> ret = new ArrayList<>();
        String sql = "select * from user_info where id = ?";
        try {
            PreparedStatement pstmt = connection.prepareCall(sql);
            for (int i = 0; i < count; i++) {
                Random random = new Random();
                int id = random.nextInt(Vertex.TOTAL + 1);
                ret.add(readById(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    //    private final String ID = "id";
//    private final String USER_URL_ID = "userUrlId";
//    private final String USER_NAME = "userName";
//    private final String GENDER = "gender";
//    private final String USER_LOC = "userLoc";
//    private final String DESCRIPTION = "description";
//    private final String EDU_INFO = "eduInfo";
//    private final String USER_TAG = "userTag";
//    public String combine(Vertex v) {
//        return "id = " + v.getId() +
//                " urlId = " + v.getUrlId() +
//                " name = " + v.getUserName() +
//                " gender = " + v.getGender() +
//                " loc = " + v.getLocation() +
//                " decription　=　" + v.getDescription() +
//                " userTag = " + v.getUserTag() +
//                "　eduInfo = " + v.getEducationInformation();
//    }
    public String combine(Vertex v) {
        return " gender = " + v.getGender() +
                " loc = " + v.getLocation() +
                " decription　=　" + v.getDescription() +
                " userTag = " + v.getUserTag() +
                "　eduInfo = " + v.getEducationInformation();
    }
}
