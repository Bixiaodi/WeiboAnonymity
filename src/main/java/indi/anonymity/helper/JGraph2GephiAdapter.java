package indi.anonymity.helper;

import indi.anonymity.elements.BaseVertex;
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
public interface JGraph2GephiAdapter<T extends BaseVertex>{

    default Graph transform(org.jgrapht.DirectedGraph<T, DefaultEdge> g) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        org.gephi.graph.api.DirectedGraph directedGraph = graphModel.getDirectedGraph();
        HashMap<T, Node> map = new HashMap<>();
        HashSet<Integer> set = new HashSet<>();
        for (T v: g.vertexSet()) {
            if (!set.contains(v.getId()) && (g.inDegreeOf(v) + g.outDegreeOf(v) != 0) ) {
                Node node = graphModel.factory().newNode(String.valueOf(v.getId()));
                directedGraph.addNode(node);
                map.put(v, node);
                set.add(v.getId());
            }
        }
        for (DefaultEdge e: g.edgeSet()) {
            if(map.get(g.getEdgeSource(e)) != null && map.get(g.getEdgeTarget(e)) != null) {
                Edge edge = graphModel.factory().newEdge(
                        map.get(g.getEdgeSource(e)),
                        map.get(g.getEdgeTarget(e)));
                directedGraph.addEdge(edge);
            }
        }
        return graphModel.getDirectedGraph();
    }

}
