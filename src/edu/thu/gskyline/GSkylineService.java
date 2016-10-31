package edu.thu.gskyline;

import java.util.List;
import java.util.Set;

public interface GSkylineService {
    // generate k-group skyline for given graph
    List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k);
}
