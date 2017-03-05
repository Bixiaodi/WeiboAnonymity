package indi.anonymity.experiment;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.ClusteringCoefficient;
import org.gephi.statistics.plugin.GraphDistance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.OptionalDouble;

/**
 * Created by emily on 17/3/5.
 */
public class BasicComputation {

    //average path length
    public static double averagePathLength(Graph graph) {
        GraphDistance d = new GraphDistance();
        d.initializeStartValues();
        d.calculateDistanceMetrics(graph, d.createIndiciesMap(graph), true, false);
        return d.getPathLength();
    }

    //cluster coefficient
    public static double computeClusterCoefficient(Graph graph) {
        ClusteringCoefficient cc = new ClusteringCoefficient();
        cc.execute(graph);
        return cc.getAverageClusteringCoefficient();
    }

    //betweenness, the average of all nodes
    public static double[] distance(Graph graph) {
        GraphDistance d = new GraphDistance();
        d.initializeStartValues();
        HashMap<Node, Integer> indicies = d.createIndiciesMap(graph);
        HashMap<String, double[]> metricsMap = (HashMap) d.calculateDistanceMetrics(graph, indicies, true, false);
        OptionalDouble betweenness = Arrays.stream(metricsMap.get(GraphDistance.BETWEENNESS)).average();
        OptionalDouble closeness = Arrays.stream(metricsMap.get(GraphDistance.CLOSENESS)).average();
        OptionalDouble harmonicCloseness = Arrays.stream(metricsMap.get(GraphDistance.HARMONIC_CLOSENESS)).average();
        OptionalDouble eccentricity = Arrays.stream(metricsMap.get(GraphDistance.ECCENTRICITY)).average();
        return new double[]{betweenness.getAsDouble(), closeness.getAsDouble(),
                harmonicCloseness.getAsDouble(), eccentricity.getAsDouble()};

    }



}
