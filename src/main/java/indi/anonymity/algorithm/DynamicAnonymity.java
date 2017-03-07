package indi.anonymity.algorithm;

import indi.anonymity.elements.BaseVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.JGraph2GephiAdapter;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedGraphUnion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by emily on 17/3/6.
 */

public interface DynamicAnonymity<T extends BaseVertex> {

    HashMap<Integer, DirectedGraph<T, DefaultEdge>> getAdjacentEdge();
    HashMap<Integer, DirectedGraph<T, DefaultEdge>> getAdjacentEdgeDec();
    HashMap<Integer, HashSet<T>> getAdjacentVertex();
    HashMap<Integer, HashSet<T>> getAdjacentVertexDec();
    Connection getConnection();
    int getRound();

    void doExperiment(ArrayList<DirectedGraph<T, DefaultEdge>> originalGraph, int curRound) throws IOException;
    ArrayList<DirectedGraph<T, DefaultEdge>> iteration(DirectedGraph<T, DefaultEdge> cur,
                                                                    DirectedGraph<T, DefaultEdge> supplementGraph,
                                                                    int n,
                                                                    int k);
    void execute(DirectedGraph<T, DefaultEdge> original, int k, int round) throws IOException;

    default void recordOriginalGraph(ArrayList<DirectedGraph<T, DefaultEdge>> graphs) throws FileNotFoundException {
        String file = "original/";
        for(int i = 0; i < graphs.size(); i++) {
            File outputFile = new File(file + i + ".txt");
            PrintWriter output = new PrintWriter(outputFile);
            output.println(graphs.get(i).vertexSet().size());
            for(T v: graphs.get(i).vertexSet()) {
                output.println(v.getId());
            }
            output.println(graphs.get(i).edgeSet().size());
            for(DefaultEdge e: graphs.get(i).edgeSet()) {
                output.println(graphs.get(i).getEdgeSource(e).getId() + " " + graphs.get(i).getEdgeTarget(e).getId());
            }
            output.close();
        }
    }
    default void recordAnonymityResult(int curRound, ArrayList<DirectedGraph<T, DefaultEdge>> graphs)
            throws FileNotFoundException {
        for (int i = graphs.size() - 1, j = 0; i >= 0; i--, j++) {
            String path = "result/";
            File file = new File(path + curRound + "-" + j + ".txt");
            PrintWriter output = new PrintWriter(file);
            output.println(graphs.get(i).vertexSet().size());
            for (T v : graphs.get(i).vertexSet()) {
                output.println(v.getId());
            }
            output.println(graphs.get(i).edgeSet().size());
            for (DefaultEdge e : graphs.get(i).edgeSet()) {
                output.println(graphs.get(i).getEdgeSource(e).getId() + " " + graphs.get(i).getEdgeTarget(e).getId());
            }
            output.close();
        }
    }
    default int check(ArrayList<ArrayList<T>> group,
                      DirectedGraph<T, DefaultEdge> supplementGraph,
                      T v,
                      int k) {
        int index = -1;
        for (int i = 0; i < group.size(); i++) {
            if (group.get(i).size() >= k || group.get(i).get(0).getRound() != v.getRound()) {
                continue;
            }
            for (int j = 0; j < group.get(i).size(); j++) {
                if (!(supplementGraph.containsEdge(group.get(i).get(j), v) &&
                        supplementGraph.containsEdge(v, group.get(i).get(j)))) {
                    return i;
                }
            }
        }
        return index;
    }

    //生成supplement graph
    default DirectedGraph<T, DefaultEdge> generateSupplementGraph(DirectedGraph<T, DefaultEdge> cur, int n) {
        DirectedGraph<T, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        //current graph ET
        for (T v : cur.vertexSet()) {
            ret.addVertex(v);
        }
        for (DefaultEdge e : cur.edgeSet()) {
            ret.addEdge(cur.getEdgeSource(e), cur.getEdgeTarget(e));
        }
        //0 ~ T-1
        for (int i = 1; i <= n; i++) {
            ret = new DirectedGraphUnion<>(ret, getAdjacentEdge().get(i));
        }
        return ret;
    }

    //相邻两个图边的差集, correct
    default DirectedGraph<T, DefaultEdge> differentEdge(DirectedGraph<T, DefaultEdge> g1, DirectedGraph<T, DefaultEdge> g2) {
        DirectedGraph<T, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        Set<DefaultEdge> edges = g1.edgeSet();
        for (DefaultEdge e : edges) {
            if (!g2.containsEdge(e)) {
                ret.addVertex(g1.getEdgeSource(e));
                ret.addVertex(g1.getEdgeTarget(e));
                ret.addEdge(g1.getEdgeSource(e), g1.getEdgeTarget(e));
            }
        }
        return ret;
    }

    //相邻两个图结点的差集,在g1的但不在g2的。correct
    default HashSet<T> differentVertex(DirectedGraph<T, DefaultEdge> g1, DirectedGraph<T, DefaultEdge> g2) {
        HashSet<T> ret = new HashSet<>();
        //对一个图进行遍历,对判断结点是不是不在另一个图里面,在g1里,但不在g2里,2比1少的。
        Set<T> vertexes = g1.vertexSet();
        for (T v : vertexes) {
            if (!g2.containsVertex(v)) {
                ret.add(v);
            }
        }
        return ret;
    }

    default DirectedGraph<T, DefaultEdge> copyGraph(DirectedGraph<T, DefaultEdge> original) {
        DirectedGraph<T, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        for (T v : original.vertexSet()) {
            ret.addVertex(v);
        }
        for (DefaultEdge e : original.edgeSet()) {
            ret.addEdge(original.getEdgeSource(e), original.getEdgeTarget(e));
        }
        return ret;
    }

}
