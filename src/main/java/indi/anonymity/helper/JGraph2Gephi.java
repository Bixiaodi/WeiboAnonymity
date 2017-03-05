package indi.anonymity.helper;

import indi.anonymity.elements.Vertex;
import org.apache.camel.impl.DefaultUnitOfWork;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.openide.util.Lookup;
import java.util.HashMap;
import java.util.HashSet;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.project.api.ProjectController;


/**
 * Created by emily on 17/3/5.
 */
public class JGraph2Gephi implements JGraph2GephiAdapter {

    @Override
    public Graph transform(org.jgrapht.DirectedGraph<Vertex, DefaultEdge> g) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        org.gephi.graph.api.DirectedGraph directedGraph = graphModel.getDirectedGraph();
        HashMap<Vertex, Node> map = new HashMap<>();
        HashSet<String> set = new HashSet<String>();
        for(Vertex v: g.vertexSet()) {
            if(!set.contains(v.getUrlId())) {
                Node node = graphModel.factory().newNode(v.getUrlId());
                directedGraph.addNode(node);
                map.put(v, node);
                set.add(v.getUrlId());
            }
        }
        for(DefaultEdge e: g.edgeSet()) {
            Edge edge = graphModel.factory().newEdge(map.get(g.getEdgeSource(e)), map.get(g.getEdgeTarget(e)));
            directedGraph.addEdge(edge);

        }
        org.gephi.graph.api.DirectedGraph graph = graphModel.getDirectedGraph();
        return  graph;
    }
}
