package indi.anonymity.algorithm;

import indi.anonymity.elements.BaseVertex;
import indi.anonymity.elements.Vertex;
import indi.anonymity.experiment.BasicComputation;
import indi.anonymity.helper.JGraph2GephiAdapter;
import indi.anonymity.helper.ReadEmail;
import org.gephi.graph.api.Graph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by emily on 17/3/6.
 */
public class BaseVertexDynamicAnonymity implements DynamicAnonymity<BaseVertex>, JGraph2GephiAdapter{

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

    public BaseVertexDynamicAnonymity(Connection connection, int round, int updateCount,
                                      String dataFileName, int from, int to, int costThreshold, int timeWindow) {
        this.connection = connection;
        this.round = round;
        this.updateCount = updateCount;
        adjacentEdge = new HashMap<>();
        adjacentVertex = new HashMap<>();
        adjacentEdgeDec = new HashMap<>();
        adjacentVertexDec = new HashMap<>();
        this.dataFileName = dataFileName;
        this.from = from;
        this.to = to;
        this.costThreshold = costThreshold;
        this.timeWindow = timeWindow;
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
        ArrayList<BaseVertex> vertexes = new ArrayList<>(cur.vertexSet());
        ArrayList<ArrayList<BaseVertex>> group = new ArrayList<>();
        ArrayList<DirectedGraph<BaseVertex, DefaultEdge>> ret = new ArrayList<>();
        HashMap<Integer, Integer> id2group = new HashMap<>();
        for (int i = 0, len = vertexes.size(); i < len; i++) {
            BaseVertex curv = vertexes.get(i);
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
        DirectedGraph<BaseVertex, DefaultEdge> deleteEdge = differentEdge(supplementGraph, cur);
        DirectedGraph<BaseVertex, DefaultEdge> anonymousGraph = copyGraph(supplementGraph);
        anonymousGraph.removeAllEdges(deleteEdge.edgeSet());
        ret.add(anonymousGraph);

        for (int i = n; i > 0; i--) {
            DirectedGraph<BaseVertex, DefaultEdge> current = copyGraph(ret.get(ret.size() - 1));
//            current.removeAllVertices(adjacentVertexDec.get(i));
            current.removeAllEdges(adjacentEdgeDec.get(i).edgeSet());
            ret.add(current);
        }
        return ret;
    }

    @Override
    public void doExperiment(ArrayList<DirectedGraph<BaseVertex, DefaultEdge>> originalGraph, int curRound) throws IOException {

        BufferedWriter output = new BufferedWriter(new FileWriter(dataFileName, true));
        output.write("in round " + curRound + "\n");
        System.out.println("in round " + curRound);
        for(int i = 0; i < originalGraph.size(); i++) {
            Graph g = transform(originalGraph.get(i));
            //6个系数
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
    public void execute(DirectedGraph<BaseVertex, DefaultEdge> original, int k, int round) throws IOException {
        // graphs list
        //每一个round读一次图,判断cost够不够
        //每次来一轮新的图,看图的cost是不是够,如果不够就不匿名,够了就匿名
        ArrayList<DirectedGraph<BaseVertex, DefaultEdge>> graphs = new ArrayList<>();
        DirectedGraph<BaseVertex, DefaultEdge> current = original;
        DirectedGraph<BaseVertex, DefaultEdge> next = null;
        //i-设定更新几轮,j实际更新几轮
        for (int i = 0, j = 0; i < round; i++) {
            if(i == 0) {
                graphs.add(original);
                recordAnonymityResult(j, graphs);
            } else {
                to += timeWindow;
                cost += getAnonymityCost(current);
                next = nextRound(from, to);
                if(cost >= costThreshold) {
                    cost = 0;
                    graphs.add(next);
                    j++;
                    //结点都一样,不需要删除结点了
//                    getAdjacentVertex().put(j, differentVertex(graphs.get(j - 1), graphs.get(j)));
//                    getAdjacentVertexDec().put(j, differentVertex(graphs.get(j), graphs.get(j - 1)));
                    getAdjacentEdge().put(j, differentEdge(graphs.get(j - 1), graphs.get(j)));
                    getAdjacentEdgeDec().put(j, differentEdge(graphs.get(j), graphs.get(j - 1)));
                    ArrayList<DirectedGraph<BaseVertex, DefaultEdge>> anonymousGraphs = iteration(next, generateSupplementGraph(next, j), j, k);
                    current = copyGraph(next);
                    next = null;
                    recordAnonymityResult(j, anonymousGraphs);
                    recordOriginalGraph(anonymousGraphs);
                    doExperiment(anonymousGraphs, j);
                }
            }
        }
//        if (next != null) {
//            // push
//        }
        recordOriginalGraph(graphs);
        doExperiment(graphs, -1);

    }
}
