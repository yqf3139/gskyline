package edu.thu.gskyline;

import java.util.*;

public class DirectedSkylineGraph {

    public List<Layer> layers;

    public static DirectedSkylineGraph createFrom(Dataset dataset, int k) {
        // sort the dataset by first dimension
        dataset.sortBy(0);

        List<DataPoint> points = dataset.points;

        DirectedSkylineGraph graph = new DirectedSkylineGraph();
        graph.layers = new ArrayList<>();
        if (points.size() == 0) {
            return graph;
        }

        // build layers
        Layer layer0 = new Layer();
        layer0.dimension = dataset.dimension;
        Layer layerMax = layer0;
        layer0.points = new LinkedList<>();
        layer0.points.add(points.get(0));
        layer0.tailPoint = points.get(0);

        graph.layers.add(layer0);

        for (int i = 1; i < points.size(); i++) {
            DataPoint point = points.get(i);
            if (!layer0.dominate(point)) {
                point.layerIdx = 0;
                layer0.points.add(point);
                layer0.tailPoint = point;
            } else if (layerMax.dominate(point)) {
                if (graph.layers.size() == k) {
                    continue;
                }
                Layer newLayer = new Layer();
                newLayer.dimension = dataset.dimension;
                newLayer.points = new LinkedList<>();
                graph.layers.add(newLayer);
                point.layerIdx = graph.layers.size() - 1;
                newLayer.points.add(point);
                newLayer.tailPoint = point;
                layerMax = newLayer;
            } else {
                Layer dummyLayer = new Layer();
                dummyLayer.tailPoint = point;

                int index = Collections.binarySearch(graph.layers, dummyLayer,
                        (o1, o2) -> o1.dominate(o2.tailPoint) ? -1 : 1);
                if (index < 0) {
                    index = -index - 1;
                }
                Layer targetLayer = graph.layers.get(index);
                point.layerIdx = index;
                targetLayer.points.add(point);
                targetLayer.tailPoint = point;
            }

        }

        // build dominance relationships
        buildDominance(graph);

        // build index by second dimension
        buildIndex(graph);

        return graph;
    }

    public static void buildIndex(DirectedSkylineGraph graph) {
        Dataset dataset = new Dataset();
        dataset.points = new ArrayList<>();
        for (Layer layer : graph.layers) {
            dataset.points.addAll(layer.points);
        }
        dataset.dimension = graph.layers.get(0).dimension;
        dataset.sortBy(1);
        int counter = dataset.points.size();
        for (DataPoint p : dataset.points) {
            p.idx = counter--;
        }
    }

    public static void buildDominance(DirectedSkylineGraph graph) {
        for (int i = 1; i < graph.layers.size(); i++) {
            for (DataPoint p1 : graph.layers.get(i).points) {
                for (int j = 0; j < i; j++) {
                    for (DataPoint p2 : graph.layers.get(i - 1 - j).points) {
                        if (p2.dominate(p1)) {
                            p2.children.add(p1);
                            p1.parents.add(p2);
                        }
                    }
                }
            }
        }
    }
}
