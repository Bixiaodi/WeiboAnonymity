package indi.anonymity.algorithm;

import indi.anonymity.elements.BaseVertex;
import indi.anonymity.helper.ReadEmail;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by zp on 04/03/2017.
 */
public class SlidingWindow {

    private Connection connection;

    public SlidingWindow(Connection connection) {
        this.connection = connection;
    }

    public DirectedGraph<BaseVertex, DefaultEdge> graphBetweenInterval(int from, int to) {
        DirectedGraph<BaseVertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        ReadEmail re = new ReadEmail(connection);
        re.readAllVertexes().stream().forEach((v) -> ret.addVertex(new BaseVertex(v)));
        return fillEdges(ret, from, to);
    }

    private DirectedGraph<BaseVertex, DefaultEdge> fillEdges(DirectedGraph<BaseVertex, DefaultEdge> graph,
                                                            int from,
                                                            int to) {
        ReadEmail re = new ReadEmail(connection);
        Map<Integer, BaseVertex> vertexMap = graph
                .vertexSet()
                .stream()
                .collect(Collectors.toMap(BaseVertex::getId, Function.identity()));
        Map<Integer, ArrayList<Integer>> edges = re.readEdges(from, to);
        edges.keySet().stream().forEach((source) ->
                edges.get(source).forEach((target) ->
                        graph.addEdge(vertexMap.get(source), vertexMap.get(target))
                )
        );
        return graph;
    }
}
