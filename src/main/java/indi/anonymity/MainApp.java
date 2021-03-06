package indi.anonymity;

import indi.anonymity.algorithm.DynamicAnonymity;
import indi.anonymity.algorithm.SlidingWindow;
import indi.anonymity.elements.BaseVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.DatabaseConnector;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.sql.Connection;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
//        Main main = new Main();
//        main.addRouteBuilder(new MyRouteBuilder());
//        main.run(args);
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();
        Connection connection = connector.getConnection();

//        int initCount = 10, updateCount = 3, round = 3, k = 2;
//        GraphUpdate gu = new GraphUpdate(0, initCount, connection);
//        DirectedGraph<Vertex, DefaultEdge> originalGraph = gu.updateGraph(new ArrayList<>(),0);
//
//        DynamicAnonymity dynamicAnonymity = new DynamicAnonymity(connection, round, updateCount);
//        dynamicAnonymity.execute(originalGraph, k);

        SlidingWindow sw = new SlidingWindow(connection);
        DirectedGraph<BaseVertex, DefaultEdge> graph = sw.graphBetweenInterval(0, 23175);


        connection.close();
    }

}

