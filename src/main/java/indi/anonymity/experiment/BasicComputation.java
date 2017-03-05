package indi.anonymity.experiment;

import indi.anonymity.elements.Vertex;
import org.gephi.graph.api.*;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.project.api.ProjectController;
import org.gephi.statistics.plugin.ClusteringCoefficient;
import org.gephi.statistics.plugin.GraphDistance;
import org.jgrapht.*;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.graph.DefaultEdge;
import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by emily on 17/3/5.
 */
public class BasicComputation {
//    public static double directedGraphClusteringCoefficient(Graph graph) {
//        ClusteringCoefficient cc = new ClusteringCoefficient();
//        ArrayWrapper[] network = new ArrayWrapper[4];
//        int[] triangles = new int[4];
//        double[] nodeClustering = new double[4];
//
//        HashMap<String, Double> results = cc.computeClusteringCoefficient(graph, network, triangles, nodeClustering, true);
//        double avClusteringCoefficient = results.get("clusteringCoefficient");
//        return avClusteringCoefficient;
//    }
    public static double averagePathLength(Graph graph) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        GraphDistance d = new GraphDistance();
        d.initializeStartValues();
        UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
        HashMap<Node, Integer> indicies = d.createIndiciesMap(undirectedGraph);
        d.calculateDistanceMetrics(graphModel.getGraph(), indicies, true, false);
        double apl = d.getPathLength();
        return apl;
    }

    public static double undirectedClusterCoefficient(org.jgrapht.DirectedGraph<Vertex, DefaultEdge> g) {
        double total = 0.0;
        DirectedNeighborIndex neighborIndex = new DirectedNeighborIndex(g);
        for(Vertex v: g.vertexSet()) {
            int degree = g.inDegreeOf(v) + g.outDegreeOf(v);
            int possible = degree * (degree - 1);
            int actual = 0;
            ArrayList<Vertex> adj = new ArrayList<>();
            adj.addAll(neighborIndex.predecessorListOf(v));
            adj.addAll(neighborIndex.successorListOf(v));
            for(Vertex u: adj) {
                for(Vertex w: adj) {
                    if(g.containsEdge(u, w) || g.containsEdge(w, u)) {
                        actual++;
                    }
                }
            }
            if(possible > 0) {
                total += 1.0 * actual / possible;
            }
        }
        return total / g.vertexSet().size();
    }
}
