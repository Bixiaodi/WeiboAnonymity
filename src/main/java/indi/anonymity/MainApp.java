package indi.anonymity;

import indi.anonymity.algorithm.DynamicAnonymity;
import indi.anonymity.algorithm.GraphUpdate;
import indi.anonymity.algorithm.SlidingWindow;
import indi.anonymity.elements.BaseVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.DatabaseConnector;
import indi.anonymity.helper.JGraph2GephiAdapter;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A Camel Application
 */
public class MainApp implements JGraph2GephiAdapter {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception  {
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();
        Connection connection = connector.getConnection();

//        int initCount = 10, updateCount = 3, round = 3, k = 2;
//        GraphUpdate gu = new GraphUpdate(0, initCount, connection);
//        DirectedGraph<Vertex, DefaultEdge> originalGraph = gu.updateGraph(new ArrayList<>(),0);
//
//        DynamicAnonymity dynamicAnonymity = new DynamicAnonymity(connection, round, updateCount);
//        dynamicAnonymity.execute(originalGraph, k);

//        SlidingWindow sw = new SlidingWindow(connection);
//        DirectedGraph<BaseVertex, DefaultEdge> graph = sw.graphBetweenInterval(0, 23175);

        int initCount = 200, updateCount = 60, round = 4, k = 2;
        String fileName = "init_" + initCount + "_update_" + updateCount + "_k_" + k + ".txt";

        long beginInit = System.currentTimeMillis();
        System.out.println("init count - " + initCount  + ", update count - " + updateCount);
        System.out.println("start init graph");
        GraphUpdate gu = new GraphUpdate(0, initCount, connection);
        DirectedGraph<Vertex, DefaultEdge> originalGraph = gu.updateGraph(new ArrayList<>(),0);
        long endInit = System.currentTimeMillis();
        System.out.println("end init graph");
        System.out.println("init time: " + (endInit - beginInit) + " ms");
        DynamicAnonymity dynamicAnonymity = new DynamicAnonymity(connection, round, updateCount, fileName);

        long beginExecute = System.currentTimeMillis();
        System.out.println("start execute graph");
        dynamicAnonymity.execute(originalGraph, k);
        long endExecute = System.currentTimeMillis();
        System.out.println("end execute graph");
        System.out.println("execute time: " + (endExecute - beginExecute) + "ms");

        BufferedWriter output = new BufferedWriter(new FileWriter(fileName, true));
        output.write("init time: " + (endInit - beginInit) + " ms\n");
        output.write("execute time: " + (endExecute - beginExecute) + "ms\n");
        output.close();
        connection.close();
    }

}

