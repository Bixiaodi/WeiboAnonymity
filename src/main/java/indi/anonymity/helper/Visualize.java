package indi.anonymity.helper;

/**
 * Created by emily on 17/2/21.
 */
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;

import indi.anonymity.elements.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;

public class Visualize extends JApplet{
    private static final Color DEFAULT_BG_COLOR = Color.decode("#F6FBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

    private JGraphModelAdapter<String, DefaultEdge> jgAdapter;
    private String file;

    public Visualize(String file) {
        this.file = file;
    }


    @Override
    public void init()
    {
        // create a JGraphT graph
        ListenableGraph<String, DefaultEdge> g =
                new ListenableDirectedMultigraph<>(DefaultEdge.class);

        // create a visualization using JGraph, via an adapter
        jgAdapter = new JGraphModelAdapter<>(g);

        JGraph jgraph = new JGraph(jgAdapter);

        adjustDisplaySettings(jgraph);
        getContentPane().add(jgraph);
        resize(DEFAULT_SIZE);

        File input = new File(file);
        Scanner scanner = null;
        try {
            scanner = new Scanner(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int vcount = scanner.nextInt();
        for(int i = 0; i < vcount; i++) {
            String v = String.valueOf(scanner.nextInt());
            g.addVertex(v);
            positionVertexAt(v, i * 20, i * 30);
        }
        int ecount = scanner.nextInt();
        for(int i = 0; i < ecount; i++) {
            g.addEdge(String.valueOf(scanner.nextInt()), String.valueOf(scanner.nextInt()));
        }

        // that's all there is to it!...
    }

    private void adjustDisplaySettings(JGraph jg)
    {
        jg.setPreferredSize(DEFAULT_SIZE);

        Color c = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter("bgcolor");
        } catch (Exception e) { }

        if (colorStr != null) {
            c = Color.decode(colorStr);
        }
        jg.setBackground(c);
    }

    @SuppressWarnings("unchecked")
    private void positionVertexAt(Object vertex, int x, int y)
    {
        DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        Rectangle2D bounds = GraphConstants.getBounds(attr);

        Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight());

        GraphConstants.setBounds(attr, newBounds);

        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, attr);
        jgAdapter.edit(cellAttr, null, null, null);
    }

    /**
     * a listenable directed multigraph that allows loops and parallel edges.
     */
    private static class ListenableDirectedMultigraph<V, E>
            extends DefaultListenableGraph<V, E>
            implements DirectedGraph<V, E>  {
        ListenableDirectedMultigraph(Class<E> edgeClass)
        {
            super(new DirectedMultigraph<>(edgeClass));
        }
    }

    public static void main(String[] args)  {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j <= i; j++) {
                Visualize applet = new Visualize(i + "-" + j + ".txt");
                applet.init();
                JFrame frame = new JFrame();
                frame.getContentPane().add(applet);
                frame.setTitle(i + "-" + j);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        }
    }

}
