package edu.thu.gskyline;

import java.util.LinkedList;
import java.util.List;

public class DataPoint {

    public int idx;
    public List<DataPoint> parents = new LinkedList<>();
    public List<DataPoint> children = new LinkedList<>();
    public double[] data;

    public boolean dominate(DataPoint other) {
        double[] a = data;
        double[] b = other.data;
        int len = a.length;
        if (len != b.length) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (a[i] > b[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(idx);
    }
}
