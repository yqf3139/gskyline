package edu.thu.gskyline;

import javax.swing.event.ListDataEvent;
import javax.xml.crypto.Data;
import java.util.*;

/**
 * Created by lynn on 2016/10/29.
 */

public class UWise extends GSkylineBaseImpl{

    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
        List<Set<DataPoint>> result = new LinkedList<>();
        List<DataPoint> points = new ArrayList<>();
        List<DataPoint> skyline = new ArrayList<>();

        preproccess(graph, k, points, skyline, result);
        //build 1-unit group as candidate groups following reverse order of point index
//        unit_group_reordering();
        //for each candidate group G in 1-unit groups do
        List<Set<DataPoint>>  candidateGroups=new LinkedList<>();

        for (DataPoint p :points ){
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
                        List<DataPoint> tailset=get_tail_Set(candidate,points);//tail set pruning
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
    private HashSet<DataPoint> get_union_unit_group(Set<DataPoint> candidate){
        HashSet<DataPoint> union=new HashSet<>();
        for(DataPoint p:candidate){
            for(DataPoint up:get_unit_group(p)){
                union.add(up);
            }
        }
        return union;
    }
//    private boolean isEmpty_candidate_group_at_i(List<Set<DataPoint>> candidateGroups,int i){
//        int counter=0;
//        for(Set<DataPoint> s:candidateGroups){
//            if (s.size()==i){
//                counter++;
//            }
//        }
//        return counter==0;
//    }

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
            children.addAll(p.children);//find all children points
        }
        for (int i=maxIdx+1;i<points.size();i++){
            if (!(children.contains(points.get(i)))){
                tailSet.add(points.get(i));//remove it from tailset if it is in childrenset
            }
        }
        return tailSet;
    }
}
