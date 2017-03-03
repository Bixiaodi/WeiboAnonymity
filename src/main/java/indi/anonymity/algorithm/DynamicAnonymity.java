package indi.anonymity.algorithm;

/**
 * Created by emily on 17/2/16.
 */

import indi.anonymity.elements.Edge;
import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.ReadEdge;
import indi.anonymity.helper.ReadVertex;
import indi.anonymity.helper.ReadVertexAndEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedGraphUnion;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Array;
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

    public void checkEdge(HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index - 1) + " not in " + index));
            for(DefaultEdge e: adj.get(index).edgeSet()) {
                System.out.println(adj.get(index).getEdgeSource(e).getId() + " -> " + adj.get(index).getEdgeTarget(e).getId());
            }
            System.out.println();
        }
    }
    public void checkVertex(HashMap<Integer, HashSet<Vertex>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index - 1) + " not in " + index));
            for(Vertex v: adj.get(index)) {
                System.out.println(v.getId());
            }
            System.out.println();
        }
    }

    public void checkEdge2(HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index) + " not in " + (index - 1)));
            for(DefaultEdge e: adj.get(index).edgeSet()) {
                System.out.println(adj.get(index).getEdgeSource(e).getId() + " -> " + adj.get(index).getEdgeTarget(e).getId());
            }
            System.out.println();
        }
    }
    public void checkVertex2(HashMap<Integer, HashSet<Vertex>> adj) {
        for(int index: adj.keySet()) {
            System.out.println(("in " + (index) + " not in " + (index - 1)));
            for(Vertex v: adj.get(index)) {
                System.out.println(v.getId());
            }
            System.out.println();
        }
    }

    public void printGraph(DirectedGraph<Vertex, DefaultEdge> g) {
        System.out.println("start");
        for(DefaultEdge e: g.edgeSet()) {
            System.out.println(g.getEdgeSource(e).getId() + " -> " + g.getEdgeTarget(e).getId());
        }
        for(Vertex v: g.vertexSet()) {
            System.out.println(v.getId());
        }
        System.out.println("end");
    }

    public void execute(DirectedGraph<Vertex, DefaultEdge> original, int k) throws FileNotFoundException {

        ArrayList<DirectedGraph<Vertex, DefaultEdge>> graphs = new ArrayList<>();
        ReadVertexAndEdge readVertexAndEdge = new ReadVertexAndEdge(updateCount, updateCount, connection);
        for(int i = 0; i < this.round; i++) {
            System.out.println("------round------");
            if(i == 0) {
                //直接匿名,没有suplement graph
                graphs.add(original);
            } else {
                DirectedGraph<Vertex, DefaultEdge> cur = nextRound(new ArrayList<Vertex>(original.vertexSet()));
                graphs.add(cur);
                adjacentVertex.put(i, differentVertex(graphs.get(i - 1), graphs.get(i)));
                adjacentVertexDec.put(i, differentVertex(graphs.get(i), graphs.get(i - 1)));
                adjacentEdge.put(i, differentEdge(graphs.get(i - 1), graphs.get(i)));
                adjacentEdgeDec.put(i, differentEdge(graphs.get(i), graphs.get(i - 1)));
                //DirectedGraph<Vertex, DefaultEdge> suplementGraph = generateSuplementGraph(original, i);
                //执行匿名过程,迭代
//                ArrayList<DirectedGraph<Vertex, DefaultEdge>> anonymousGraphs = iteration(original, suplementGraph, i, k);
//                recordAnoymityResult(anonymousGraphs);
            }
        }

        for(int i = 0; i < graphs.size(); i++) {
            System.out.println("graph - " + i);
            printGraph(graphs.get(i));
        }

        checkEdge(adjacentEdge);
        checkEdge2(adjacentEdgeDec);
        checkVertex(adjacentVertex);
        checkVertex2(adjacentVertexDec);

    }

    public DirectedGraph<Vertex, DefaultEdge> nextRound(ArrayList<Vertex> oldVertex) {
        ReadVertexAndEdge readVertexAndEdge = new ReadVertexAndEdge(updateCount, updateCount, connection);
        ArrayList<Vertex> newVertex = readVertexAndEdge.updateGraph(oldVertex, false);
        DirectedGraph<Vertex, DefaultEdge> ret = readVertexAndEdge.addEdges(newVertex);
        return ret;
    }


    //取出结点-排序-遍历是否满足三个条件。
    public ArrayList<DirectedGraph<Vertex, DefaultEdge>> iteration (DirectedGraph<Vertex, DefaultEdge> cur, DirectedGraph<Vertex, DefaultEdge> suplementGraph, int n, int k) {
        ArrayList<Vertex> vertexes = new ArrayList<Vertex>(cur.vertexSet());
        ArrayList<ArrayList<Vertex>> group = new ArrayList<ArrayList<Vertex>>();
        ArrayList<DirectedGraph<Vertex, DefaultEdge>> ret = new ArrayList<DirectedGraph<Vertex, DefaultEdge>>();
        Collections.sort(vertexes, weiboVertexCompare);
        for(int i = 0, len = vertexes.size(); i < len; i++) {
            Vertex curv = vertexes.get(i);
            int insertId = check(group, suplementGraph, curv, k);
            if(insertId == -1) {
                group.add(new ArrayList<Vertex>());
                group.get(group.size() - 1).add(curv);
            } else {
                group.get(insertId).add(curv);
            }
        }
        DirectedGraph<Vertex, DefaultEdge> deleteEdge = differentEdge(suplementGraph, cur);
        DirectedGraph<Vertex, DefaultEdge> anonymousGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        for(Vertex v: suplementGraph.vertexSet()) {
            v.resetGroupId();
            anonymousGraph.addVertex(v);
        }
        for(DefaultEdge e: suplementGraph.edgeSet()) {
            anonymousGraph.addEdge(suplementGraph.getEdgeSource(e), suplementGraph.getEdgeTarget(e));
        }
        anonymousGraph.removeAllEdges(deleteEdge.edgeSet());
        ret.add(anonymousGraph);
        for(int i = n - 1; i > 0; i--) {
            DirectedGraph<Vertex, DefaultEdge> current = new DefaultDirectedGraph<>(DefaultEdge.class);
            current.removeAllVertices(adjacentVertexDec.get(i));
            current.removeAllEdges(adjacentEdgeDec.get(i).edgeSet());
            ret.add(current);
        }
        return ret;
    }

    public int check(ArrayList<ArrayList<Vertex>> group, DirectedGraph<Vertex, DefaultEdge> suplementGraph, Vertex v, int k) {
        int index = -1;
        for(int i = 0; i < group.size(); i++) {
            if(group.get(i).size() >= k || group.get(i).get(0).getRound() != v.getRound()) {
                continue;
            }
            for(int j = 0; j < group.get(i).size(); j++) {
                if(!(suplementGraph.containsEdge(group.get(i).get(j), v) && suplementGraph.containsEdge(v, group.get(i).get(j)))) {
                    return i;
                }
            }
        }
        return index;
    }


    public void recordAnoymityResult(ArrayList<DirectedGraph<Vertex, DefaultEdge>> graphs) throws FileNotFoundException {

        File file = new File("result.txt");
        PrintWriter output = new PrintWriter(file);
        for(int i = 0; i < graphs.size(); i++) {
            output.println(i);
            for(Vertex v: graphs.get(i).vertexSet()) {
                output.println(v.getId());
            }
            for(DefaultEdge e: graphs.get(i).edgeSet()) {
                output.println(graphs.get(i).getEdgeSource(e).getUrlId() + " " + graphs.get(i).getEdgeTarget(e).getUrlId());
            }
        }
        output.close();
    }
    
    //生成suplement graph
    public DirectedGraph<Vertex, DefaultEdge> generateSuplementGraph(DirectedGraph<Vertex, DefaultEdge> cur, int n) {
        DirectedGraph<Vertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        //current graph ET
        for(Vertex v: cur.vertexSet()) {
            ret.addVertex(v);
        }
        for(DefaultEdge e: cur.edgeSet()) {
            ret.addEdge(cur.getEdgeSource(e), cur.getEdgeTarget(e));
        }
        //0 ~ T-1
        for(int i = 1; i <= n; i++) {
            ret = new DirectedGraphUnion<>(ret, adjacentEdge.get(i));
        }
        return ret;
    }

    //相邻两个图结点的差集,在g1的但不在g2的。
    public HashSet<Vertex> differentVertex(DirectedGraph<Vertex, DefaultEdge> g1, DirectedGraph<Vertex, DefaultEdge> g2) {
        HashSet<Vertex> ret = new HashSet<>();
        //对一个图进行遍历,对判断结点是不是不在另一个图里面,在g1里,但不在g2里,2比1少的。
        Set<Vertex> vertexes = g1.vertexSet();
        for(Vertex v: vertexes) {
            if(!g2.containsVertex(v)) {
                ret.add(v);
            }
        }
        return ret;
    }
    //相邻两个图边的差集
    public DirectedGraph<Vertex, DefaultEdge> differentEdge(DirectedGraph<Vertex, DefaultEdge> g1, DirectedGraph<Vertex, DefaultEdge> g2) {
//        printGraph(g1);
//        printGraph(g2);
        DirectedGraph<Vertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);
        Set<DefaultEdge> edges = g1.edgeSet();
        for(DefaultEdge e: edges) {
            if(!g2.containsEdge(e)) {
//                System.out.println("add : " + g1.getEdgeSource(e).getId() + " -> "  + g1.getEdgeTarget(e).getId());
                ret.addVertex(g1.getEdgeSource(e));
                ret.addVertex(g1.getEdgeTarget(e));
                ret.addEdge(g1.getEdgeSource(e), g1.getEdgeTarget(e));
            }
        }
        return ret;
    }

    Comparator<Vertex> weiboVertexCompare = new Comparator<Vertex>(){
        public int compare(Vertex o1, Vertex o2) {
            if(o1.getGender() == o2.getGender()) {
                int location = Collator.getInstance(Locale.CHINA).compare(o1.getLocation(), o2.getLocation());
                //    int location = o1.getLocation().compareTo(o2.getLocation());
                if(location == 0) {
                    int description = Collator.getInstance(Locale.CHINA).compare(o1.getDescription(), o2.getDescription());
                    //        int description = o1.getDescription().compareTo(o2.getDescription());
                    if(description == 0) {
                        int userTag = Collator.getInstance(Locale.CHINA).compare(o1.getUserTag(), o2.getUserTag());
                        //            int userTag = o1.getUserTag().compareTo(o2.getUserTag());
                        return userTag;
                    } else {
                        return description;
                    }
                } else {
                    return location;
                }
            } else {
                return o1.getGender() - o2.getGender();
            }
        }
    };
}
