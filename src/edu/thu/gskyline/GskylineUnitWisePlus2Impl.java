package edu.thu.gskyline;

import java.util.*;

/**
 * Created by lynn on 2016/11/13.
 */

public class GskylineUnitWisePlus2Impl extends GSkylineUnitWiseImpl {

    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
        List<Set<DataPoint>> result = new LinkedList<>();
        List<DataPoint> points = new ArrayList<>();
        List<DataPoint> skyline = new ArrayList<>();

        preproccess(graph, k, points, skyline, result);
        //build 1-unit group as candidate groups following reverse order of point index
        List<DataPoint> reversePoints = new LinkedList<>(points);
        Collections.reverse(reversePoints);
        //for each candidate group G in 1-unit groups do
        List<Set<DataPoint>> candidateGroups = new LinkedList<>();
        for (DataPoint p : reversePoints) {//dif from uwise:reverse-order iterator
            //subset pruning
            int gLast = get_last_deepest_candidate_group(p, points);
            if (gLast < k) {
                continue;
            } else if (gLast == k) {
                Set<DataPoint> candidate = new HashSet<>(points.subList(0, p.idx + 1));
                result.add(candidate);
                continue;
            }
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
                        HashSet<DataPoint> tailset = get_tail_Set2(candidate, reversePoints,k);//tail set pruning
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

    private int get_last_deepest_candidate_group(DataPoint point, List<DataPoint> points) {
        return point.idx + 1;
    }

    protected HashSet<DataPoint> get_tail_Set2(Set<DataPoint> set, List<DataPoint> points,int k) {
        HashSet<DataPoint> children = new HashSet<>();
        int maxIdx = -1;
        int reversePointsSize=points.size();
        for (DataPoint p : set) {
            int idx = reversePointsSize-1-p.idx;
            if (idx > maxIdx) {
                maxIdx = idx;
            }
            children.addAll(p.parents);//for reverse order
        }
        List<DataPoint> temp=new ArrayList<>();
        temp.addAll(points);
        HashSet<DataPoint> tailSet = new HashSet<>(temp.subList(maxIdx+1,temp.size()));
        tailSet.removeAll(children);

        int tailSize=k-set.size();
        for (DataPoint p:tailSet){
            if (p.parents.size()>tailSize){
                tailSet.remove(p);
            }
        }
        return tailSet;
    }
}
