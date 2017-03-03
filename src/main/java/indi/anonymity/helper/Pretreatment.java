package indi.anonymity.helper;

import indi.anonymity.elements.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by emily on 17/2/20.
 * 随机删除某些结点,及其相关联的边
 */
public class Pretreatment {

    private int count;//remove count = add count;
    private Connection connection;

    public Pretreatment() {

    }
    public Pretreatment(int count) {
        this.count = count;
        this.connection = connection;
    }

    public DirectedGraph<Vertex, DefaultEdge> removeVertexForNextRound(DirectedGraph<Vertex, DefaultEdge> g, HashSet<Vertex> vertexId) {
        int total = 0;
        ArrayList<Vertex> list = new ArrayList<Vertex>(vertexId);
        while(total < count) {
            Random random = new Random();
            int cur = -1;
            while(cur == -1) {
                int index = random.nextInt(list.size());
                if(vertexId.contains(list.get(index))) {
                    Vertex v = list.get(index);
                    cur = v.getId();
                    g.removeVertex(v);
                    vertexId.remove(v);
                    total++;
                }
            }
        }
        return g;
    }
    public DirectedGraph<Vertex, DefaultEdge> addVertexForNextRound(DirectedGraph<Vertex, DefaultEdge> g, HashSet<Vertex> vertexId) {
        int total = 0;
        ReadVertex vertex = new ReadVertex(connection);
        while(total < count) {
            Random random = new Random();
            int cur = -1;
            while(cur == -1) {
                int index = random.nextInt(Vertex.TOTAL + 1);
                if(!vertexId.contains(new Vertex(index))) {
                    Vertex v = vertex.readById(index);
                    vertexId.add(v);
                    g.addVertex(v);
                    cur = index;
                    total++;
                }
            }
        }
        ReadEdge edge = new ReadEdge(connection);
        for(Vertex v: vertexId) {
            ArrayList<String> targetUrl = edge.readEdgeBySource(v.getUrlId());
            for(String t: targetUrl) {
                Vertex target = vertex.readByUserUrl(t);
                if(vertexId.contains(target)) {
                    g.addEdge(v, target);
                }
            }
        }
        return g;
    }



}
