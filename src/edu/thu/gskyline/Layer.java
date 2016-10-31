package edu.thu.gskyline;

import java.util.List;

class Layer {
    int dimension;
    List<DataPoint> points;
    DataPoint tailPoint;

    boolean dominate(DataPoint other) {
        if (dimension == 2) {
            return tailPoint.dominate(other);
        } else {
            for (DataPoint point : points) {
                if (point.dominate(other)) {
                    return true;
                }
            }
            return false;
        }
    }
}
