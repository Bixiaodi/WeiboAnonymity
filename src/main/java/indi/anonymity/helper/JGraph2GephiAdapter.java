package indi.anonymity.helper;

import indi.anonymity.elements.Vertex;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Created by emily on 17/3/5.
 */
interface JGraph2GephiAdapter {
    public Graph transform(DirectedGraph<Vertex, DefaultEdge> g);

}
