package indi.anonymity.algorithm;

/**
 * Created by emily on 17/2/16.
 */

import indi.anonymity.elements.Vertex;
import indi.anonymity.experiment.BasicComputation;
import indi.anonymity.helper.JGraph2Gephi;
import org.gephi.graph.api.Graph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedGraphUnion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.Collator;
import java.util.*;

/**
 * 得到当前的结点集合,进行排序,返回一个list
 * 每次读一个结点
 */
public class DynamicAnonymity {

    private HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adjacentEdge;
    private HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adjacentEdgeDec;
    private HashMap<Integer, HashSet<Vertex>> adjacentVertex;
    private HashMap<Integer, HashSet<Vertex>> adjacentVertexDec;
    private Connection connection;
    private int round;
    private int updateCount;

    public DynamicAnonymity(Connection connection, int round, int updateCount) {
        this.connection = connection;
        this.round = round;
        this.updateCount = updateCount;
        adjacentEdge = new HashMap<>();
        adjacentVertex = new HashMap<>();
        adjacentEdgeDec = new HashMap<>();
        adjacentVertexDec = new HashMap<>();
    }

    public void execute(DirectedGraph<Vertex, DefaultEdge> original, int k) throws FileNotFoundException {
        ArrayList<DirectedGraph<Vertex, DefaultEdge>> graphs = new ArrayList<>();
        for (int i = 0; i < this.round; i++) {
            System.out.println("------round------" + i);
            if (i == 0) {
                //直接匿名,没有supplement graph
                graphs.add(original);
                recordAnonymityResult(i, graphs);
            } else {
                DirectedGraph<Vertex, DefaultEdge> cur = nextRound(new ArrayList<>(graphs.get(i - 1).vertexSet()), i);
                graphs.add(cur);
                adjacentVertex.put(i, differentVertex(graphs.get(i - 1), graphs.get(i)));
                adjacentVertexDec.put(i, differentVertex(graphs.get(i), graphs.get(i - 1)));
                adjacentEdge.put(i, differentEdge(graphs.get(i - 1), graphs.get(i)));
                adjacentEdgeDec.put(i, differentEdge(graphs.get(i), graphs.get(i - 1)));
                DirectedGraph<Vertex, DefaultEdge> supplementGraph = generateSupplementGraph(graphs.get(i), i);
                // 执行匿名过程,迭代
                ArrayList<DirectedGraph<Vertex, DefaultEdge>> anonymousGraphs = iteration(original, supplementGraph, i, k);
                recordAnonymityResult(i, anonymousGraphs);
                doExperiment(anonymousGraphs, i);
            }
        }
        recordOriginalGraph(graphs);
        doExperiment(graphs, -1);
    }

    //For experiment
    public void doExperiment(ArrayList<DirectedGraph<Vertex, DefaultEdge>> originalGraph, int curRound) {
        System.out.println("in round " + curRound);
        JGraph2Gephi j2g = new JGraph2Gephi();
        for(int i = 0; i < originalGraph.size(); i++) {
            Graph g = j2g.transform(originalGraph.get(i));
            double apl = BasicComputation.averagePathLength(g);
            System.out.println(apl);
//            double ce = BasicComputation.undirectedClusterCoefficient(originalGraph.get(i));
//            System.out.println(ce);
        }
    }




    private void recordOriginalGraph(ArrayList<DirectedGraph<Vertex, DefaultEdge>> graphs) throws FileNotFoundException {
        String file = "original/";
        for(int i = 0; i < graphs.size(); i++) {
            File outputFile = new File(file + i + ".txt");
            PrintWriter output = new PrintWriter(outputFile);
            output.println(graphs.get(i).vertexSet().size());
            for(Vertex v: graphs.get(i).vertexSet()) {
                output.println(v.getId());
            }
            output.println(graphs.get(i).edgeSet().size());
            for(DefaultEdge e: graphs.get(i).edgeSet()) {
                output.println(graphs.get(i).getEdgeSource(e).getId() + " " + graphs.get(i).getEdgeTarget(e).getId());
            }
            output.close();
        }
    }
    // update graph, correct
    private DirectedGraph<Vertex, DefaultEdge> nextRound(ArrayList<Vertex> oldVertex, int curRound) {
        return new GraphUpdate(updateCount, updateCount, connection).updateGraph(oldVertex, curRound);
    }

    //取出结点-排序-遍历是否满足三个条件。
    private ArrayList<DirectedGraph<Vertex, DefaultEdge>> iteration(DirectedGraph<Vertex, DefaultEdge> cur,
                                                                    DirectedGraph<Vertex, DefaultEdge> supplementGraph,
                                                                    int n,
                                                                    int k) {
        ArrayList<Vertex> vertexes = new ArrayList<>(cur.vertexSet());
        ArrayList<ArrayList<Vertex>> group = new ArrayList<>();
        ArrayList<DirectedGraph<Vertex, DefaultEdge>> ret = new ArrayList<DirectedGraph<Vertex, DefaultEdge>>();
        HashMap<Integer, Integer> id2group = new HashMap<>();
        Collections.sort(vertexes, (Vertex o1, Vertex o2) -> {
            if (o1.getGender() == o2.getGender()) {
                int location = Collator.getInstance(Locale.CHINA).compare(o1.getLocation(), o2.getLocation());
                if (location == 0) {
                    int description = Collator.getInstance(Locale.CHINA).compare(o1.getDescription(), o2.getDescription());
                    if (description == 0) {
                        return Collator.getInstance(Locale.CHINA).compare(o1.getUserTag(), o2.getUserTag());
                    } else {
                        return description;
                    }
                } else {
                    return location;
                }
            } else {
                return o1.getGender() - o2.getGender();
            }
        });

        for (int i = 0, len = vertexes.size(); i < len; i++) {
            Vertex curv = vertexes.get(i);
            int insertId = check(group, supplementGraph, curv, k);
            if (insertId == -1) {
                group.add(new ArrayList<>());
                group.get(group.size() - 1).add(curv);
                id2group.put(curv.getId(), group.size() - 1);
            } else {
                group.get(insertId).add(curv);
                id2group.put(curv.getId(), insertId);
            }
        }
        //deleteEdge is the edge in supplementGraph but not in cur
        //copy the supplementGraph and remove edge in {deleteEdge}
        DirectedGraph<Vertex, DefaultEdge> deleteEdge = differentEdge(supplementGraph, cur);
        DirectedGraph<Vertex, DefaultEdge> anonymousGraph = copyGraph(supplementGraph);
        anonymousGraph.removeAllEdges(deleteEdge.edgeSet());
        ret.add(anonymousGraph);

        for (int i = n; i > 0; i--) {
            DirectedGraph<Vertex, DefaultEdge> current = copyGraph(ret.get(ret.size() - 1));
            current.removeAllVertices(adjacentVertexDec.get(i));
            current.removeAllEdges(adjacentEdgeDec.get(i).edgeSet());
            ret.add(current);
        }
        return ret;
    }

    private int check(ArrayList<ArrayList<Vertex>> group,
                      DirectedGraph<Vertex, DefaultEdge> supplementGraph,
                      Vertex v,
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

    private DirectedGraph<Vertex, DefaultEdge> copyGraph(DirectedGraph<Vertex, DefaultEdge> original) {
        DirectedGraph<Vertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        for (Vertex v : original.vertexSet()) {
            ret.addVertex(v);
        }
        for (DefaultEdge e : original.edgeSet()) {
            ret.addEdge(original.getEdgeSource(e), original.getEdgeTarget(e));
        }
        return ret;
    }

    private void recordAnonymityResult(int curRound, ArrayList<DirectedGraph<Vertex, DefaultEdge>> graphs)
            throws FileNotFoundException {
        for (int i = graphs.size() - 1, j = 0; i >= 0; i--, j++) {
            String path = "result/";
            File file = new File(path + curRound + "-" + j + ".txt");
            PrintWriter output = new PrintWriter(file);
            output.println(graphs.get(i).vertexSet().size());
            for (Vertex v : graphs.get(i).vertexSet()) {
                output.println(v.getId());
            }
            output.println(graphs.get(i).edgeSet().size());
            for (DefaultEdge e : graphs.get(i).edgeSet()) {
                output.println(graphs.get(i).getEdgeSource(e).getId() + " " + graphs.get(i).getEdgeTarget(e).getId());
            }
            output.close();
        }
    }

    //生成supplement graph
    private DirectedGraph<Vertex, DefaultEdge> generateSupplementGraph(DirectedGraph<Vertex, DefaultEdge> cur, int n) {
        DirectedGraph<Vertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        //current graph ET
        for (Vertex v : cur.vertexSet()) {
            ret.addVertex(v);
        }
        for (DefaultEdge e : cur.edgeSet()) {
            ret.addEdge(cur.getEdgeSource(e), cur.getEdgeTarget(e));
        }
        //0 ~ T-1
        for (int i = 1; i <= n; i++) {
            ret = new DirectedGraphUnion<>(ret, adjacentEdge.get(i));
        }
        return ret;
    }

    //相邻两个图结点的差集,在g1的但不在g2的。correct
    private HashSet<Vertex> differentVertex(DirectedGraph<Vertex, DefaultEdge> g1, DirectedGraph<Vertex, DefaultEdge> g2) {
        HashSet<Vertex> ret = new HashSet<>();
        //对一个图进行遍历,对判断结点是不是不在另一个图里面,在g1里,但不在g2里,2比1少的。
        Set<Vertex> vertexes = g1.vertexSet();
        for (Vertex v : vertexes) {
            if (!g2.containsVertex(v)) {
                ret.add(v);
            }
        }
        return ret;
    }

    //相邻两个图边的差集, correct
    private DirectedGraph<Vertex, DefaultEdge> differentEdge(DirectedGraph<Vertex, DefaultEdge> g1, DirectedGraph<Vertex, DefaultEdge> g2) {
        DirectedGraph<Vertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
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

    private void checkEdge(HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index - 1) + " not in " + index));
            for(DefaultEdge e: adj.get(index).edgeSet()) {
                System.out.println(adj.get(index).getEdgeSource(e).getId() + " -> " + adj.get(index).getEdgeTarget(e).getId());
            }
            System.out.println();
        }
    }
    private void checkVertex(HashMap<Integer, HashSet<Vertex>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index - 1) + " not in " + index));
            for(Vertex v: adj.get(index)) {
                System.out.println(v.getId());
            }
            System.out.println();
        }
    }

    private void checkEdge2(HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index) + " not in " + (index - 1)));
            for(DefaultEdge e: adj.get(index).edgeSet()) {
                System.out.println(adj.get(index).getEdgeSource(e).getId() + " -> " + adj.get(index).getEdgeTarget(e).getId());
            }
            System.out.println();
        }
    }
    private void checkVertex2(HashMap<Integer, HashSet<Vertex>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index) + " not in " + (index - 1)));
            for(Vertex v: adj.get(index)) {
                System.out.println(v.getId());
            }
            System.out.println();
        }
    }
    private void printGraph(DirectedGraph<Vertex, DefaultEdge> g) {
        System.out.println("start");
        for(DefaultEdge e: g.edgeSet()) {
            System.out.println(g.getEdgeSource(e).getId() + " -> " + g.getEdgeTarget(e).getId());
        }
        for(Vertex v: g.vertexSet()) {
            System.out.println(v.getId());
        }
        System.out.println("end");
    }

}
