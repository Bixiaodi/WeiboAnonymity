package indi.anonymity;

import indi.anonymity.helper.DatabaseConnector;
import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by emily on 17/3/11.
 */
public class dataAnalyse {

    public dataAnalyse() throws Exception {

    }
    public void execute() throws Exception {
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();
        Connection connection = connector.getConnection();

        String sql = "select userId, followUserId from user_follow";
 //       String sql = "select timestamp from email";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        HashMap<String, Integer> degree = new HashMap<>();
//        HashMap<Integer, Integer> sub = new HashMap<>();
//        int last = 0;
//        while(rs.next()) {
//            int now = rs.getInt(1);
//            sub.putIfAbsent(now - last, 0);
//            sub.put(now - last, sub.get(now - last) + 1);
//        }
//        TreeMap<Integer, Integer> time = new TreeMap<>();
//        for(Integer t: sub.keySet()) {
//            int s = sub.get(t);
//            time.putIfAbsent(s, 0);
//            time.put(s, time.get(s) + 1);
//        }
        while(rs.next()) {
            String source = rs.getString(1);
            String target = rs.getString(2);
            degree.putIfAbsent(source, 0);
            degree.putIfAbsent(target, 0);
            degree.put(source, degree.get(source) + 1);
            degree.put(target, degree.get(target) + 1);
        }
        TreeMap<Integer, Integer> state = new TreeMap<>();
        for(String id: degree.keySet()) {
            int deg = degree.get(id);
            state.putIfAbsent(deg, 0);
            state.put(deg, state.get(deg) + 1);
        }

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("weibo_degree");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int) 0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        HSSFCell cell = row.createCell((short) 0);
        cell.setCellValue("");
        cell.setCellStyle(style);
        cell = row.createCell((short) 1);
        cell.setCellValue("degree");
        cell.setCellStyle(style);

        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
        int id = 1;
        for(Integer key: state.keySet()) {
            row = sheet.createRow((int)(id++));
            // 第四步，创建单元格，并设置值
            row.createCell((short) 0).setCellValue(key);
            row.createCell((short) 1).setCellValue(state.get(key));
        }
        // 第六步，将文件存到指定位置
        try
        {
            FileOutputStream fout = new FileOutputStream("weibo_degree.xls");
            wb.write(fout);
            fout.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        dataAnalyse dataAnalyse = new dataAnalyse();
        dataAnalyse.execute();
    }

}

