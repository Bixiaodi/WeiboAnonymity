package indi.anonymity.helper;

import indi.anonymity.elements.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * Created by emily on 17/3/2.
 */
public class ReadVertexAndEdge {
    private int deleteCount;
    private int addCount;
    private Connection connection;


    public ReadVertexAndEdge(int deleteCount, int addCount, Connection connection) {
        this.deleteCount = deleteCount;
        this.addCount = addCount;
        this.connection = connection;
    }


    // delete some vertex(delete edge automatically) and add some vertex with correct edge
    public ArrayList<Vertex> updateGraph(ArrayList<Vertex> originalVertex, boolean init) {
        Random random = new Random();
        ReadVertex rv = new ReadVertex(connection);

        int seed = 0;
        ArrayList<Vertex> ret = new ArrayList<>();
        HashSet<String> visit = new HashSet<>();
        // Find the seed
        if (init) {
            originalVertex.add(rv.readById(random.nextInt(Vertex.TOTAL + 1)));
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
        int actualCount = init ? 1 : 0;
        for (int i = 0; i < originalVertex.size(); i++) {
            if (!visit.contains(originalVertex.get(i).getUrlId())) {
                ret.add(originalVertex.get(i));
                visit.add(originalVertex.get(i).getUrlId());
            }
        }
        ret.add(originalVertex.get(seed));

        try {
            Statement stmt = connection.createStatement();
            Queue<Vertex> q = new LinkedList<>();
            q.add(originalVertex.get(seed));

            while (!q.isEmpty()) {
                Vertex cur = q.poll();

                String sql = "SELECT * FROM user_follow WHERE userId = '" + cur.getUrlId() + "' or followUserId = '" + cur.getUrlId() + "'";
                ResultSet edgeResult = stmt.executeQuery(sql);

                while (edgeResult.next()) {
                    String source = edgeResult.getString(2);
                    String target = edgeResult.getString(3);

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
        } catch (SQLException e1) { e1.printStackTrace(); }
        return ret;
    }

    public DirectedGraph<Vertex, DefaultEdge> addEdges(ArrayList<Vertex> vertexes, int curRound) {
        ArrayList<String> vertexIds = new ArrayList<String>() {
            @Override
            public String toString() {
                String ret = "";
                for (int i = 0; i < size(); i++) {
                    if (!ret.isEmpty()) {
                        ret += ",";
                    }
                    ret += "'" + get(i) + "'";
                }
                return "(" + ret + ")";
            }
        };
        HashMap<String, Integer> map = new HashMap<>();
        DirectedGraph<Vertex, DefaultEdge> ret = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i = 0; i < vertexes.size(); i++) {
            String urlId = vertexes.get(i).getUrlId();
            vertexIds.add(urlId);
            map.put(urlId, i);
            vertexes.get(i).setRound(curRound);
            ret.addVertex(vertexes.get(i));
        }
        String sql = "SELECT * FROM user_follow uf WHERE uf.userId IN " + vertexIds +
                     "AND uf.followUserId IN " + vertexIds;
        try {
            Statement stt = connection.createStatement();
            ResultSet rs = stt.executeQuery(sql);
            while (rs.next()) {
                String from = rs.getString(2);
                String to = rs.getString(3);
                int indexFrom = map.get(from);
                int indexTo = map.get(to);
                Vertex vertexFrom = vertexes.get(indexFrom);
                Vertex vertexTo = vertexes.get(indexTo);
                ret.addEdge(vertexFrom, vertexTo);
            }
        } catch (Exception e) { e.printStackTrace(); }
//        System.out.println("Result vertexes:");
//        ArrayList<Integer> vIds = new ArrayList<>();
//        ret.vertexSet().forEach((v) -> vIds.add(v.getId()));
//        Collections.sort(vIds);
//        for (Integer i : vIds) {
//            System.out.println(i);
//        }
//        System.out.println("Result edges:");
//        for (DefaultEdge e : ret.edgeSet()) {
//            System.out.println(ret.getEdgeSource(e).getId() + " -> " + ret.getEdgeTarget(e).getId());
//        }
        return ret;
    }

}
