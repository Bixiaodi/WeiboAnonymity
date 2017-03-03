package indi.anonymity;

import indi.anonymity.algorithm.DynamicAnonymity;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.DatabaseConnector;
import indi.anonymity.helper.DeleteInvalidEdge;
import indi.anonymity.helper.ReadVertexAndEdge;
import org.apache.camel.main.Main;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

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
//        DatabaseConnector databaseConnector = new DatabaseConnector();
//        databaseConnector.connect();
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();
        Connection connection = connector.getConnection();

        int initCount = 5, updateCount = 5, round = 3, k = 2;
        ReadVertexAndEdge readVAndE = new ReadVertexAndEdge(0, initCount, connection);
        ArrayList<Vertex> originalVetex = readVAndE.updateGraph(new ArrayList<Vertex>(), true);
        originalVetex = readVAndE.updateGraph(originalVetex, false);
        DirectedGraph<Vertex, DefaultEdge> originalGraph = readVAndE.addEdges(originalVetex);
        System.out.println("vertex size() = " + originalGraph.vertexSet().size());
        DynamicAnonymity dynamicAnonymity = new DynamicAnonymity(connection, round, updateCount);
        dynamicAnonymity.execute(originalGraph, k);
        connection.close();
    }

}

