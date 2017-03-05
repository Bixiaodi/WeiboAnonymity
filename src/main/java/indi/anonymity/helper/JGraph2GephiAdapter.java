package indi.anonymity.helper;

import indi.anonymity.elements.Vertex;
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.jgrapht.graph.DefaultEdge;
import org.openide.util.Lookup;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by emily on 17/3/5.
 */
public interface JGraph2GephiAdapter {

    default Graph transform(org.jgrapht.DirectedGraph<Vertex, DefaultEdge> g) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        org.gephi.graph.api.DirectedGraph directedGraph = graphModel.getDirectedGraph();
        HashMap<Vertex, Node> map = new HashMap<>();
        HashSet<String> set = new HashSet<>();
        for (Vertex v: g.vertexSet()) {
            if (!set.contains(v.getUrlId())) {
                Node node = graphModel.factory().newNode(v.getUrlId());
                directedGraph.addNode(node);
                map.put(v, node);
                set.add(v.getUrlId());
            }
        }
        for (DefaultEdge e: g.edgeSet()) {
            Edge edge = graphModel.factory().newEdge(
                    map.get(g.getEdgeSource(e)),
                    map.get(g.getEdgeTarget(e)));
            directedGraph.addEdge(edge);
        }
        return graphModel.getDirectedGraph();
    }

}
