package indi.anonymity.algorithm;

import indi.anonymity.elements.BaseVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.ReadEmail;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by emily on 17/3/6.
 */
public class BaseVertexDynamicAnonymity implements DynamicAnonymity<BaseVertex> {

    private HashMap<Integer, DirectedGraph<BaseVertex, DefaultEdge>> adjacentEdge;
    private HashMap<Integer, DirectedGraph<BaseVertex, DefaultEdge>> adjacentEdgeDec;
    private HashMap<Integer, HashSet<BaseVertex>> adjacentVertex;
    private HashMap<Integer, HashSet<BaseVertex>> adjacentVertexDec;
    private Connection connection;
    private int round;
    private int updateCount;
    private String dataFileName;
    private int from;
    private int to;
    private int cost;
    private int costThreshold;
    private int timeWindow;

    public BaseVertexDynamicAnonymity(Connection connection, int round, int updateCount, String dataFileName) {
        this.connection = connection;
        this.round = round;
        this.updateCount = updateCount;
        adjacentEdge = new HashMap<>();
        adjacentVertex = new HashMap<>();
        adjacentEdgeDec = new HashMap<>();
        adjacentVertexDec = new HashMap<>();
        this.dataFileName = dataFileName;
    }

    public String getDataFileName() {
        return dataFileName;
    }

    public HashMap<Integer, DirectedGraph<BaseVertex, DefaultEdge>> getAdjacentEdge() {
        return adjacentEdge;
    }

    public HashMap<Integer, DirectedGraph<BaseVertex, DefaultEdge>> getAdjacentEdgeDec() {
        return adjacentEdgeDec;
    }

    public HashMap<Integer, HashSet<BaseVertex>> getAdjacentVertex() {
        return adjacentVertex;
    }

    public HashMap<Integer, HashSet<BaseVertex>> getAdjacentVertexDec() {
        return adjacentVertexDec;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getRound() {
        return round;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    private DirectedGraph<BaseVertex, DefaultEdge> nextRound(int f, int t) {
        return new SlidingWindow(connection).graphBetweenInterval(f, t);
    }

    private int getAnonymityCost(DirectedGraph<BaseVertex, DefaultEdge> original) {
        DirectedGraph<BaseVertex, DefaultEdge> dg = nextRound(from, to);
        return dg.edgeSet().size() - original.edgeSet().size();
    }

    @Override
    public ArrayList<DirectedGraph<BaseVertex, DefaultEdge>> iteration(DirectedGraph<BaseVertex, DefaultEdge> cur,
                                                                       DirectedGraph<BaseVertex, DefaultEdge> supplementGraph,
                                                                       int n,
                                                                       int k) {
        return null;
    }

    @Override
    public void doExperiment(ArrayList<DirectedGraph<BaseVertex, DefaultEdge>> originalGraph, int curRound) throws IOException {

    }

    @Override
    public void execute(DirectedGraph<BaseVertex, DefaultEdge> original, int k, int round) throws IOException {
        // graphs list
        ArrayList<DirectedGraph<Vertex, DefaultEdge>> graphs = new ArrayList<>();
        DirectedGraph<BaseVertex, DefaultEdge> current = original;
        DirectedGraph<BaseVertex, DefaultEdge> next = null;
        for (int i = 0; i < round; i++) {
            to += timeWindow;
            cost += getAnonymityCost(original);
            next = nextRound(from, to);
            if (cost >= costThreshold) {
                cost = 0;
                // 预处理
                getAdjacentVertex().put(i, differentVertex(graphs.get(i - 1), graphs.get(i)));
                getAdjacentVertexDec().put(i, differentVertex(graphs.get(i), graphs.get(i - 1)));
                getAdjacentEdge().put(i, differentEdge(graphs.get(i - 1), graphs.get(i)));
                getAdjacentEdgeDec().put(i, differentEdge(graphs.get(i), graphs.get(i - 1)));
                // iteration
                iteration(next, generateSupplementGraph(next, i), i, k);
                // 写文件
                // doExperiment
                // graphs push back current
                current = next;
                next = null;
            }
        }
//        if (next != null) {
//            // push
//        }
        //record
        // doexp
    }
}
