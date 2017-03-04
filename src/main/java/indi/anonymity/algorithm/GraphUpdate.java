package indi.anonymity.algorithm;

import indi.anonymity.elements.Vertex;
import indi.anonymity.helper.ReadEdge;
import indi.anonymity.helper.ReadVertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.sql.Connection;
import java.util.*;

/**
 * Created by emily on 17/3/2.
 */

public class GraphUpdate {

    private int deleteCount;
    private int addCount;
    private Connection connection;

    public GraphUpdate(int deleteCount, int addCount, Connection connection) {
        this.deleteCount = deleteCount;
        this.addCount = addCount;
        this.connection = connection;
    }

    // delete some vertex(delete edge automatically) and add some vertex with correct edge
    public DirectedGraph<Vertex, DefaultEdge> updateGraph(ArrayList<Vertex> originalVertex,
                                                          int round) {
        return addEdges(updateVertex(originalVertex), round);
    }

    private ArrayList<Vertex> updateVertex(ArrayList<Vertex> originalVertex) {
        Random random = new Random();
        ReadVertex rv = new ReadVertex(connection);
        ReadEdge re = new ReadEdge(connection);

        int seed;
        boolean initial = originalVertex.isEmpty();
        ArrayList<Vertex> ret = new ArrayList<>();
        HashSet<String> visit = new HashSet<>();

        // Find the seed
        if (initial) {
            originalVertex.add(rv.readRandomly());
            seed = 0;
        } else {
            seed = random.nextInt(originalVertex.size());
        }

        // Select which nodes are going to be deleted
        int actualDeleteCount = 0;
        while (actualDeleteCount < deleteCount) {
            int cur = random.nextInt(originalVertex.size());
            if (cur == seed) {
                continue;
            }
            actualDeleteCount++;
            visit.add(originalVertex.get(cur).getUrlId());
        }
        visit.add(originalVertex.get(seed).getUrlId());

        // Add the nodes remained
        int actualCount = initial ? 1 : 0;
        for (Vertex v: originalVertex) {
            if (!visit.contains(v.getUrlId())) {
                ret.add(v);
                visit.add(v.getUrlId());
            }
        }
        ret.add(originalVertex.get(seed));

        Queue<Vertex> q = new LinkedList<>();
        q.add(originalVertex.get(seed));
        while (!q.isEmpty()) {
            Vertex cur = q.poll();
            HashMap<String, String> edges = re.readEdgeByUrlId(cur.getUrlId());
            for (String source: edges.keySet()) {
                String target = edges.get(source);
                boolean isSource = source.equals(cur.getUrlId());
                String newVertexUrl = isSource ? target : source;
                Vertex newVertex = rv.readByUserUrl(newVertexUrl);
                if (!visit.contains(newVertex.getUrlId())) {
                    visit.add(newVertex.getUrlId());
                    actualCount++;
                    ret.add(newVertex);
                    q.add(newVertex);
                }
                if (actualCount >= addCount) break;
            }
            if (actualCount >= addCount) break;
        }
        while(actualCount < addCount) {
            Vertex v = rv.readRandomly();
            actualCount += ret.add(v) ? 1 : 0;
        }
        return ret;
    }

    private DirectedGraph<Vertex, DefaultEdge> addEdges(ArrayList<Vertex> vertexes, int curRound) {
        ArrayList<String> vertexIds = new ArrayList<String>() {
            @Override
            public String toString() {
                String ret = "";
                for (String s: this) {
                    if (!ret.isEmpty()) {
                        ret += ",";
                    }
                    ret += "'" + s + "'";
                }
                return "(" + ret + ")";
            }
        };
        ReadEdge re = new ReadEdge(connection);
        HashMap<String, Integer> map = new HashMap<>();
        DirectedGraph<Vertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i = 0; i < vertexes.size(); i++) {
            String urlId = vertexes.get(i).getUrlId();
            vertexIds.add(urlId);
            map.put(urlId, i);
            vertexes.get(i).setRound(curRound);
            ret.addVertex(vertexes.get(i));
        }

        HashMap<String, String> edges = re.readEdgeWithinUserId(vertexIds.toString());
        for (String source : edges.keySet()) {
            String target = edges.get(source);
            Vertex vertexSource = vertexes.get(map.get(source));
            Vertex vertexTarget = vertexes.get(map.get(target));
            ret.addEdge(vertexSource, vertexTarget);
        }
        return ret;
    }

}
