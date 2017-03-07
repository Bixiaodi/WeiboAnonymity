package indi.anonymity.algorithm;

import indi.anonymity.elements.Vertex;
import indi.anonymity.experiment.BasicComputation;
import indi.anonymity.helper.JGraph2GephiAdapter;
import org.gephi.graph.api.Graph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.Collator;
import java.util.*;

/**
 * Created by emily on 17/3/6.
 */
public class VertexDynamicAnonymity implements DynamicAnonymity<Vertex>, JGraph2GephiAdapter {

    private HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adjacentEdge;
    private HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> adjacentEdgeDec;
    private HashMap<Integer, HashSet<Vertex>> adjacentVertex;
    private HashMap<Integer, HashSet<Vertex>> adjacentVertexDec;
    private Connection connection;
    private int round;
    private int updateCount;
    private String dataFileName;

    public VertexDynamicAnonymity(Connection connection, int round, int updateCount, String dataFileName) {
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

    public HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> getAdjacentEdge() {
        return adjacentEdge;
    }

    public HashMap<Integer, DirectedGraph<Vertex, DefaultEdge>> getAdjacentEdgeDec() {
        return adjacentEdgeDec;
    }

    public HashMap<Integer, HashSet<Vertex>> getAdjacentVertex() {
        return adjacentVertex;
    }

    public HashMap<Integer, HashSet<Vertex>> getAdjacentVertexDec() {
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

    public DirectedGraph<Vertex, DefaultEdge> nextRound(ArrayList<Vertex> oldVertex, int curRound) {
        return new GraphUpdate(updateCount, updateCount, connection).updateGraph(oldVertex, curRound);
    }

    public void doExperiment(ArrayList<DirectedGraph<Vertex, DefaultEdge>> originalGraph, int curRound) throws IOException {

        BufferedWriter output = new BufferedWriter(new FileWriter(dataFileName, true));
        output.write("in round " + curRound + "\n");
        System.out.println("in round " + curRound);
        for(int i = 0; i < originalGraph.size(); i++) {
            Graph g = transform(originalGraph.get(i));
            double ace = BasicComputation.computeClusterCoefficient(g);
            double apl = BasicComputation.averagePathLength(g);
            double[] distance = BasicComputation.distance(g);
            System.out.println("average cluster coefficient : " + ace);
            System.out.println("average path length : " + apl);
            System.out.println("betweenness = " + distance[0] + " closeness = " + distance[1] + " harmonicCloseness = " + distance[2] + " eccentricity = " + distance[3]);
            output.write("average cluster coefficient : " + ace + "\n");
            output.write("average path length : " + apl + "\n");
            output.write("betweenness = " + distance[0] + " closeness = " + distance[1] + " harmonicCloseness = " + distance[2] + " eccentricity = " + distance[3]  + "\n");
        }
        output.close();
    }

    @Override
    //取出结点-排序-遍历是否满足三个条件。
    public ArrayList<DirectedGraph<Vertex, DefaultEdge>> iteration(DirectedGraph<Vertex, DefaultEdge> cur,
                                                               DirectedGraph<Vertex, DefaultEdge> supplementGraph,
                                                               int n,
                                                               int k) {
        ArrayList<Vertex> vertexes = new ArrayList<>(cur.vertexSet());
        ArrayList<ArrayList<Vertex>> group = new ArrayList<>();
        ArrayList<DirectedGraph<Vertex, DefaultEdge>> ret = new ArrayList<>();
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

    @Override
    public void execute(DirectedGraph<Vertex, DefaultEdge> original, int k, int round) throws IOException  {
        ArrayList<DirectedGraph<Vertex, DefaultEdge>> graphs = new ArrayList<>();
        for (int i = 0; i < round; i++) {
            if (i == 0) {
                //直接匿名,没有supplement graph
                graphs.add(original);
                recordAnonymityResult(i, graphs);
            } else {
                DirectedGraph<Vertex, DefaultEdge> cur = nextRound(new ArrayList<>(graphs.get(i - 1).vertexSet()), i);
                graphs.add(cur);
                getAdjacentVertex().put(i, differentVertex(graphs.get(i - 1), graphs.get(i)));
                getAdjacentVertexDec().put(i, differentVertex(graphs.get(i), graphs.get(i - 1)));
                getAdjacentEdge().put(i, differentEdge(graphs.get(i - 1), graphs.get(i)));
                getAdjacentEdgeDec().put(i, differentEdge(graphs.get(i), graphs.get(i - 1)));
                DirectedGraph<Vertex, DefaultEdge> supplementGraph = generateSupplementGraph(graphs.get(i), i);
                // 执行匿名过程,迭代
                !!!!!!!!!!!
                ArrayList<DirectedGraph<Vertex, DefaultEdge>> anonymousGraphs = iteration(original, supplementGraph, i, k);
                recordAnonymityResult(i, anonymousGraphs);
                doExperiment(anonymousGraphs, i);
            }
        }
        recordOriginalGraph(graphs);
        doExperiment(graphs, -1);
    }
}
