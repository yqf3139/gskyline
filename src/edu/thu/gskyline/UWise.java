package edu.thu.gskyline;

import javax.swing.event.ListDataEvent;
import javax.xml.crypto.Data;
import java.util.*;

/**
 * Created by lynn on 2016/10/29.
 */
//class UnitGroup{
//    DataPoint point;
//    Set<DataPoint> unitGroup;
//}
public class UWise implements GSkylineService {
    // use brute force & Theorem 2
    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
//        System.out.println("hi uwise");
        List<Set<DataPoint>> result = new LinkedList<>();
        List<DataPoint> points = new ArrayList<>();
        List<DataPoint> skyline = new ArrayList<>();

        preproccess(graph, k, points, skyline, result);
        //build 1-unit group as candidate groups following reverse order of point index
        unit_group_reordering();
        //for each candidate group G in 1-unit groups do
        List<Set<DataPoint>>  candidateGroups=new LinkedList<>();

        for (DataPoint p :points ){
            HashSet<DataPoint> s = new HashSet<>();s.add(p);
            candidateGroups.add(s);
//            int i=2;
            while(candidateGroups.size()>0){
                for(Set<DataPoint> candidate :candidateGroups){
                    HashSet<DataPoint> union=get_union_unit_group(candidate);
                    if(union.size()==k){//get one result
                        result.add(union);
                        continue;
                    }else if(union.size()>k){//super set pruning
                        continue;
                    }else{
                        List<DataPoint> tailset=get_tail_Set(candidate,points);//tail set pruning
                        for (DataPoint tailPoint : tailset){
                            HashSet<DataPoint> newCandidate=new HashSet<>();
                            newCandidate= (HashSet<DataPoint>) candidate;
                            newCandidate.add(tailPoint);
                        }
                    }
                    candidateGroups.remove(candidate);
                }
            }

        }

        return result;
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
    private boolean isEmpty_candidate_group_at_i(List<Set<DataPoint>> candidateGroups,int i){
        int counter=0;
        for(Set<DataPoint> s:candidateGroups){
            if (s.size()==i){
                counter++;
            }
        }
        return counter==0;
    }
    private void unit_group_reordering(){
        System.out.println("temp ugroup reorder");
    }

    private List<DataPoint> get_unit_group(DataPoint p){
        List<DataPoint> unitGroup=p.parents;
        unitGroup.add(p);
        return unitGroup;
    }

    private List<DataPoint> get_tail_Set(Set<DataPoint> set, List<DataPoint> points){
        List<DataPoint> tailSet=new ArrayList<>();
        HashSet<DataPoint> children=new HashSet<>();
        int maxIdx=-1;
        for (DataPoint p:set){
            if(p.idx>maxIdx){
                maxIdx=p.idx;
            }
            children.addAll(p.children);
        }
        for (DataPoint p:points){
            if (p.idx>maxIdx && !(children.contains(p))){
                tailSet.add(p);
            }
        }
        return tailSet;
    }
    private void preproccess(
            DirectedSkylineGraph graph, int k,
            List<DataPoint> points, List<DataPoint> skyline, List<Set<DataPoint>> result) {
        for (Layer l : graph.layers) {
            for (DataPoint p : l.points) {
                if (p.layerIdx == 0) {
                    skyline.add(p);
                }
                // pre processing from Chap 5
                if (p.parents.size() > k - 1) {
                    continue;
                } else if (p.parents.size() == k - 1) {
                    HashSet<DataPoint> s = new HashSet<>(p.parents);
                    s.add(p);
                    result.add(s);
                    continue;
                }
                points.add(p);
            }
        }
        System.out.println("points size : " + points.size());
    }
}
