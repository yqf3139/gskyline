package edu.thu.gskyline;

import javax.swing.event.ListDataEvent;
import javax.xml.crypto.Data;
import java.util.*;

/**
 * Created by lynn on 2016/10/30.
 */

public class UWisePlus extends GSkylineBaseImpl {

    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
        List<Set<DataPoint>> result = new LinkedList<>();
        List<DataPoint> points = new ArrayList<>();
        List<DataPoint> skyline = new ArrayList<>();

        preproccess(graph, k, points, skyline, result);
        //build 1-unit group as candidate groups following reverse order of point index
        List<DataPoint> reversePoints=get_reverse_points(points);
        //for each candidate group G in 1-unit groups do
        List<Set<DataPoint>>  candidateGroups=new LinkedList<>();
        for (DataPoint p :reversePoints ){//dif from uwise:reverse-order iterator
            //subset pruning
            int gLast=get_last_deepest_candidate_group(p,points);
            if(gLast<k){
                continue;
            }else if (gLast==k){
                Set<DataPoint> candidate=new HashSet<>(points.subList(0,points.indexOf(p)+1));
                result.add(candidate);
                continue;
            }
            HashSet<DataPoint> s = new HashSet<>();s.add(p);
            candidateGroups.add(s);
//            int i=2;
            while(candidateGroups.size()>0){
                List<Set<DataPoint>>  newCandidateGroups=new LinkedList<>();//to avoid concurrent modification exception
                for(Set<DataPoint> candidate :candidateGroups){
                    HashSet<DataPoint> union=get_union_unit_group(candidate);
                    if(union.size()==k){//get one result
                        result.add(union);
                        continue;
                    }else if(union.size()>k){//super set pruning
                        continue;
                    }else{
                        List<DataPoint> tailset=get_tail_Set(candidate,reversePoints);//tail set pruning
                        for (DataPoint tailPoint : tailset){
                            HashSet<DataPoint> newCandidate=new HashSet<>(candidate);
                            newCandidate.add(tailPoint);
                            newCandidateGroups.add(newCandidate);
                        }
                    }
                }
                candidateGroups=newCandidateGroups;
            }

        }

        return result;
    }

    private List<DataPoint> get_reverse_points(List<DataPoint> points){//unit group reordering
        List<DataPoint> reversePoints=new ArrayList<DataPoint>();
        for (int i=points.size()-1;i>=0;i--){
            reversePoints.add(points.get(i));
        }
        return  reversePoints;
    }
    private int get_last_deepest_candidate_group(DataPoint point,List<DataPoint> points){
        return points.indexOf(point)+1;
    }
    private HashSet<DataPoint> get_union_unit_group(Set<DataPoint> candidate){
        HashSet<DataPoint> union=new HashSet<>();
        for(DataPoint p:candidate){
            for(DataPoint up:get_unit_group(p)){
                union.add(up);
            }
        }
        return union;
    }

    private List<DataPoint> get_unit_group(DataPoint p){
        List<DataPoint> unitGroup=new ArrayList<DataPoint>(p.parents);
        unitGroup.add(p);
        return unitGroup;
    }

    private List<DataPoint> get_tail_Set(Set<DataPoint> set, List<DataPoint> points){
        List<DataPoint> tailSet=new ArrayList<>();
        HashSet<DataPoint> children=new HashSet<>();
        int maxIdx=-1;
        for (DataPoint p:set){
            int idx=points.indexOf(p);
            if(idx>maxIdx){
                maxIdx=idx;
            }
            children.addAll(p.parents);//for reverse order
        }
        for (int i=maxIdx+1;i<points.size();i++){
            if (!(children.contains(points.get(i)))){
                tailSet.add(points.get(i));
            }
        }
        return tailSet;
    }
}
