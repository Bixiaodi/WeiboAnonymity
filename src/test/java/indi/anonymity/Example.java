package indi.anonymity;

import indi.anonymity.algorithm.SortVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.ReadVertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by emily on 17/2/15.
 */
public class Example {
    public static void main(String[] args) {
//        ReadVertex readVertex = new ReadVertex(10);
//        ArrayList<Vertex> currentVertex = readVertex.readRandomly();
//        SortVertex sortVertex = new SortVertex();
//        sortVertex.sort(currentVertex);
//        for(int i = 0; i < 10; i++) {
//            System.out.println(readVertex.combine(currentVertex.get(i)));
//        }
//        GenerateSimhash generateSimhash = new GenerateSimhash();
//        generateSimhash.write();

        DirectedGraph<URL, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        try {
            URL amazon = new URL("http://www.amazon.com");
            URL yahoo = new URL("httpp://www.yahoo.com");
            URL ebay = new URL("http://www.ebay.com");
            g.addVertex(amazon);
            g.addVertex(yahoo);
            g.addVertex(ebay);
            g.addEdge(yahoo, amazon);
            g.addEdge(yahoo, ebay);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
