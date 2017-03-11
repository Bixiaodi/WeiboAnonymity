package indi.anonymity;

import indi.anonymity.algorithm.BaseVertexDynamicAnonymity;
import indi.anonymity.algorithm.SlidingWindow;
import indi.anonymity.algorithm.VertexDynamicAnonymity;
import indi.anonymity.algorithm.GraphUpdate;
import indi.anonymity.elements.BaseVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.DatabaseConnector;
import indi.anonymity.helper.JGraph2GephiAdapter;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * A Camel Application
 */
public class MainApp implements JGraph2GephiAdapter {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */

    public void weibo(Connection connection) throws IOException {
        int initCount = 100, updateCount = 3, round = 4, k = 2;
        String fileName = "init_" + initCount + "_update_" + updateCount + "_k_" + k + ".txt";
        long beginInit = System.currentTimeMillis();
        System.out.println("init count - " + initCount  + ", update count - " + updateCount);
        System.out.println("start init graph");
        GraphUpdate gu = new GraphUpdate(0, initCount, connection);
        DirectedGraph<Vertex, DefaultEdge> originalGraph = gu.updateGraph(new ArrayList<>(), 0);
        long endInit = System.currentTimeMillis();
        System.out.println("end init graph");
        System.out.println("init time: " + (endInit - beginInit) + " ms");
        VertexDynamicAnonymity dynamicAnonymity = new VertexDynamicAnonymity(connection, round, updateCount, fileName);

        long beginExecute = System.currentTimeMillis();
        System.out.println("start execute graph");
        dynamicAnonymity.execute(originalGraph, k, round);
        long endExecute = System.currentTimeMillis();
        System.out.println("end execute graph");
        System.out.println("execute time: " + (endExecute - beginExecute) + "ms");
    }
    public void email(Connection connection) throws IOException {
        int initCount = 100, updateCount = 3, round = 4, k = 2, from = 0, to = 50000, costThreshold = 10, timeWindow = 20000;
        String fileName = "from_" + from + "_to_" + to + "_costThreshold_" + costThreshold + "_timeWindow_" + timeWindow + "_k_" + k + ".txt";
        long beginInit = System.currentTimeMillis();
        System.out.println("to = " + to + " costThreshold = " + costThreshold + " timeWindow = " + timeWindow);
        System.out.println("start init graph");
        SlidingWindow sw = new SlidingWindow(connection);
        DirectedGraph<BaseVertex, DefaultEdge> originalGraph = sw.graphBetweenInterval(from, to);
        long endInit = System.currentTimeMillis();
        System.out.println("end init graph");
        System.out.println("init time: " + (endInit - beginInit) + " ms");
        BaseVertexDynamicAnonymity baseDynamicAnonymity = new BaseVertexDynamicAnonymity(connection, round, updateCount,
                fileName, from, to, costThreshold, timeWindow);

        long beginExecute = System.currentTimeMillis();
        System.out.println("start execute graph");
        baseDynamicAnonymity.execute(originalGraph, k, round);
        long endExecute = System.currentTimeMillis();
        System.out.println("end execute graph");
        System.out.println("execute time: " + (endExecute - beginExecute) + "ms");

        BufferedWriter output = new BufferedWriter(new FileWriter(fileName, true));
        output.write("init time: " + (endInit - beginInit) + " ms\n");
        output.write("execute time: " + (endExecute - beginExecute) + "ms\n");
        output.close();
    }

    public static void main(String... args) throws Exception  {
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();
        Connection connection = connector.getConnection();
        MainApp mainApp = new MainApp();
 //       mainApp.weibo(connection);
        mainApp.email(connection);
        connection.close();
    }

}

