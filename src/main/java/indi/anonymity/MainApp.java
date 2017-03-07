package indi.anonymity;

import indi.anonymity.algorithm.BaseVertexDynamicAnonymity;
import indi.anonymity.algorithm.SlidingWindow;
import indi.anonymity.algorithm.VertexDynamicAnonymity;
import indi.anonymity.algorithm.GraphUpdate;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.DatabaseConnector;
import indi.anonymity.helper.JGraph2GephiAdapter;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.ArrayList;

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


        // Change file name
//        BaseVertexDynamicAnonymity bvda = new BaseVertexDynamicAnonymity(connection, round, updateCount, fileName, 0, 1000, 200, 60);
//        bvda.execute(new SlidingWindow(connection).graphBetweenInterval(0, 1000), k, round);


        BufferedWriter output = new BufferedWriter(new FileWriter(fileName, true));
        output.write("init time: " + (endInit - beginInit) + " ms\n");
        output.write("execute time: " + (endExecute - beginExecute) + "ms\n");
        output.close();
        connection.close();
    }

}

