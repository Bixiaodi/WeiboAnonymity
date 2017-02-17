package indi.anonymity;

import indi.anonymity.algorithm.SortVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.ReadVertex;

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
        GenerateSimhash generateSimhash = new GenerateSimhash();
        generateSimhash.write();
    }
}
