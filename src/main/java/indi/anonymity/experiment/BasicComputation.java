package indi.anonymity.experiment;

import org.gephi.graph.api.Graph;
import org.gephi.statistics.plugin.ClusteringCoefficient;
import org.gephi.statistics.plugin.GraphDistance;

/**
 * Created by emily on 17/3/5.
 */
public class BasicComputation {

    public static double averagePathLength(Graph graph) {
        GraphDistance d = new GraphDistance();
        d.initializeStartValues();
        d.calculateDistanceMetrics(graph, d.createIndiciesMap(graph), true, false);
        return d.getPathLength();
    }

    public static void computeClusterCoefficient(Graph graph) {
        ClusteringCoefficient cc = new ClusteringCoefficient();
        cc.execute(graph);
        cc.getAverageClusteringCoefficient();
        cc.getTriangesReuslts();
        cc.getCoefficientReuslts();
        cc.getReport();
    }
}
