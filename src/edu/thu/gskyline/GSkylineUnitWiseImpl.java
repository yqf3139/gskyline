package edu.thu.gskyline;

import java.util.*;

/**
 * Created by lynn on 2016/10/29.
 */

public class GSkylineUnitWiseImpl extends GSkylineBaseImpl {

    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
        List<Set<DataPoint>> result = new LinkedList<>();
        List<DataPoint> points = new ArrayList<>();
        List<DataPoint> skyline = new ArrayList<>();

        preproccess(graph, k, points, skyline, result);
//        unit_group_reordering();
//        for each candidate group G in 1-unit groups do
        List<Set<DataPoint>> candidateGroups = new LinkedList<>();

        for (DataPoint p : points) {
            HashSet<DataPoint> s = new HashSet<>();
            s.add(p);
            candidateGroups.add(s);
            while (candidateGroups.size() > 0) {
                List<Set<DataPoint>> newCandidateGroups = new LinkedList<>();//to avoid concurrent modification exception
                for (Set<DataPoint> candidate : candidateGroups) {
                    HashSet<DataPoint> union = get_union_unit_group(candidate);
                    if (union.size() == k) {//get one result
                        result.add(union);
                        continue;
                    } else if (union.size() > k) {//super set pruning
                        continue;
                    } else {
                        List<DataPoint> tailset = get_tail_Set(candidate, points);//tail set pruning
                        for (DataPoint tailPoint : tailset) {
                            HashSet<DataPoint> newCandidate = new HashSet<>(candidate);
                            newCandidate.add(tailPoint);
                            newCandidateGroups.add(newCandidate);
                        }
                    }
                }
                candidateGroups = newCandidateGroups;
            }

        }

        return result;
    }

    protected HashSet<DataPoint> get_union_unit_group(Set<DataPoint> candidate) {
        HashSet<DataPoint> union = new HashSet<>();
        for (DataPoint p : candidate) {
            union.add(p);
            union.addAll(p.parents);
        }
        return union;
    }

    protected List<DataPoint> get_tail_Set(Set<DataPoint> set, List<DataPoint> points) {
        List<DataPoint> tailSet = new ArrayList<>();
        HashSet<DataPoint> children = new HashSet<>();
        int maxIdx = -1;
        for (DataPoint p : set) {
            int idx = p.idx;
            if (idx > maxIdx) {
                maxIdx = idx;
            }
            children.addAll(p.children);//find all children points
        }
        for (int i = maxIdx + 1; i < points.size(); i++) {
            if (!(children.contains(points.get(i)))) {
                tailSet.add(points.get(i));//remove it from tailset if it is in childrenset
            }
        }
        return tailSet;
    }
}
