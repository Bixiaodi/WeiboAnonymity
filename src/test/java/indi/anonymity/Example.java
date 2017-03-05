package indi.anonymity;

import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;
import org.gephi.project.api.Workspace;

import java.util.HashMap;

/**
 * Created by emily on 17/2/15.
 */
public class Example {

//    public double testClusterCoefficient() {
//        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
//        UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
//        Node node1 = graphModel.factory().newNode("0");
//        Node node2 = graphModel.factory().newNode("1");
//        Node node3 = graphModel.factory().newNode("2");
//        Node node4 = graphModel.factory().newNode("3");
//        Node node5 = graphModel.factory().newNode("4");
//        Node node6 = graphModel.factory().newNode("5");
//        Node node7 = graphModel.factory().newNode("6");
//        undirectedGraph.addNode(node1);
//        undirectedGraph.addNode(node2);
//        undirectedGraph.addNode(node3);
//        undirectedGraph.addNode(node4);
//        undirectedGraph.addNode(node5);
//        undirectedGraph.addNode(node6);
//        undirectedGraph.addNode(node7);
//        Edge edge12 = graphModel.factory().newEdge(node1, node2, false);
//        Edge edge23 = graphModel.factory().newEdge(node2, node3, false);
//        Edge edge31 = graphModel.factory().newEdge(node3, node1, false);
//        Edge edge14 = graphModel.factory().newEdge(node1, node4, false);
//        Edge edge45 = graphModel.factory().newEdge(node4, node5, false);
//        Edge edge51 = graphModel.factory().newEdge(node5, node1, false);
//        Edge edge16 = graphModel.factory().newEdge(node1, node6, false);
//        Edge edge67 = graphModel.factory().newEdge(node6, node7, false);
//        Edge edge71 = graphModel.factory().newEdge(node7, node1, false);
//        undirectedGraph.addEdge(edge12);
//        undirectedGraph.addEdge(edge23);
//        undirectedGraph.addEdge(edge31);
//        undirectedGraph.addEdge(edge14);
//        undirectedGraph.addEdge(edge45);
//        undirectedGraph.addEdge(edge51);
//        undirectedGraph.addEdge(edge16);
//        undirectedGraph.addEdge(edge67);
//        undirectedGraph.addEdge(edge71);
//
//        Graph graph = graphModel.getGraph();
//        ClusteringCoefficient cc = new ClusteringCoefficient();
//
//        ArrayWrapper[] network = new ArrayWrapper[7];
//        int[] triangles = new int[7];
//        double[] nodeClustering = new double[7];
//
//        HashMap<String, Double> results = cc.computeClusteringCoefficient(graph, network, triangles, nodeClustering, false);
//
//        double cl2 = nodeClustering[1];
//        double avClusteringCoefficient = results.get("clusteringCoefficient");
//
//        double resAv = 0.8857;
//        double diff = 0.01;
//
//    }
    public static void testAPL() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        GraphController gc = Lookup.getDefault().lookup(GraphController.class);

        GraphModel graphModel = gc.getGraphModel(workspace);
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        Node node1 = graphModel.factory().newNode("0");
        Node node2 = graphModel.factory().newNode("1");
        Node node3 = graphModel.factory().newNode("2");
        Node node4 = graphModel.factory().newNode("3");
        Node node5 = graphModel.factory().newNode("4");

        directedGraph.addNode(node1);
        directedGraph.addNode(node2);
        directedGraph.addNode(node3);
        directedGraph.addNode(node4);
        directedGraph.addNode(node5);

        Edge edge15 = graphModel.factory().newEdge(node1, node5);
        Edge edge52 = graphModel.factory().newEdge(node5, node2);
        Edge edge53 = graphModel.factory().newEdge(node5, node3);
        Edge edge45 = graphModel.factory().newEdge(node4, node5);

        directedGraph.addEdge(edge15);
        directedGraph.addEdge(edge52);
        directedGraph.addEdge(edge53);
        directedGraph.addEdge(edge45);

        GraphDistance d = new GraphDistance();
        d.initializeStartValues();
        UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
        HashMap<Node, Integer> indicies = d.createIndiciesMap(undirectedGraph);

        d.calculateDistanceMetrics(graphModel.getGraph(), indicies, true, false);

        double averageDegree = d.getPathLength();
        System.out.println(averageDegree);
    }

    public static void main(String[] args) {
        Example.testAPL();
    }
}
