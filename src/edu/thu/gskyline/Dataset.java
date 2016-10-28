package edu.thu.gskyline;

import java.util.Collections;
import java.util.List;

public class Dataset {
    public String category;
    public int dimension;
    public List<DataPoint> points;

    public void sortBy(int dim) {
        if (dim < 0 || dim > dimension - 1) {
            return;
        }
        Collections.sort(points,
                (o1, o2) -> Double.compare(o1.data[dim], o2.data[dim]));
    }
}
