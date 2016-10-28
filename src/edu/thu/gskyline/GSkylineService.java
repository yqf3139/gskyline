package edu.thu.gskyline;

import java.util.List;
import java.util.Set;

public interface GSkylineService {
    List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k);
}
