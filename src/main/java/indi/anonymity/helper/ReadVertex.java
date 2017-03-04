package indi.anonymity.helper;

import indi.anonymity.elements.Vertex;

import java.sql.*;
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
    private final String CODE_8 = "simhash8";
    private final String CODE_16 = "simhash16";
    private final String CODE_32 = "simhash32";
    private final String CODE_64 = "simhash64";

    public ReadVertex(Connection connection) {
        this.connection = connection;
    }

    public ReadVertex(int count, Connection connection) {
        this.count = count;
        this.connection = connection;
    }

    public Vertex fillProperty(ResultSet rs) {
        Vertex v = new Vertex();
        try {
            v.setId(rs.getInt(ID));
            v.setUrlId(rs.getString(USER_URL));
            v.setUserName(rs.getString(USER_NAME));
            v.setGender(rs.getInt(GENDER));
            v.setLocation(rs.getString(USER_LOC));
            v.setUserTag(rs.getString(USER_TAG));
            v.setDescription(rs.getString(DESCRIPTION));
            v.setEducationInformation(rs.getString(EDU_INFO));
            v.setCode8(rs.getString(CODE_8));
            v.setCode16(rs.getString(CODE_16));
            v.setCode32(rs.getString(CODE_32));
            v.setCode64(rs.getString(CODE_64));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }

    public Vertex readById(int id) {
        Vertex v = new Vertex();
        String sql = "SELECT * FROM user_info WHERE id = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                v = fillProperty(rs);
            }
        } catch (SQLException e) {
            if(connection == null) {
                System.out.println("connection is null");
            }
            e.printStackTrace();
        }
        return v;
    }

    public Vertex readRandomly() {
        return readById((new Random()).nextInt(Vertex.TOTAL + 1));
    }

    public Vertex readByUserUrl(String userUrl) {
        Vertex v = new Vertex();
        String sql = "SELECT * FROM user_info WHERE userUrl = '" + userUrl + "'";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                v = fillProperty(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }
}
