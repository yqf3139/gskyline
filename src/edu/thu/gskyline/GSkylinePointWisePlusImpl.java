package edu.thu.gskyline;

import java.util.*;
import java.util.stream.Collectors;

class PDataNode {
    Dataset gkl;
    Dataset tailset;
}

public class GSkylinePointWisePlusImpl implements GSkylineService {

    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
        List<PDataNode> res = getGSkylineP(graph, k);
        List<Set<DataPoint>> ans = res.stream()
                .map(node -> new HashSet<>(node.gkl.points)).
                        collect(Collectors.toCollection(LinkedList::new));
        return ans;
    }

    //pwise+
    public List<PDataNode> getGSkylinePplus(DirectedSkylineGraph graph, int k){
    	int lidx = -1;
    	for(Layer l:graph.layers){
    		lidx++;
    		int pidx = -1;
    		for(DataPoint p:l.points){
    			pidx++;
    			p.layeridx = pidx;
    			p.layernum = lidx;
    		}
    	}
    	
    	PDataNode root = new PDataNode();
    	root.tailset = new Dataset();
    	root.tailset.points = new ArrayList<>();
        for (DataPoint p : graph.layers.get(0).points) {
        	root.tailset.points.add(p);
        }
    	
    	List<PDataNode> tepGkl = new ArrayList<>();
    	tepGkl.add(root);
    	for(int i=1;i<=k;i++){
    		List<PDataNode> Gkl = new ArrayList<>();
    		for(PDataNode yu:tepGkl){
    			Gkl.add(yu);
    		}
    		while(tepGkl.size() != 0)
    			tepGkl.remove(0);
    		for (PDataNode s : Gkl){
    			HashSet<DataPoint> cs = new HashSet<>();
    			if (s.gkl != null){
    				for(DataPoint p : s.gkl.points){
    					if(p.children.size() == 0)
    						continue;
    					for(int x=0;x<p.children.size();x++)
    						cs.add(p.children.get(x));
    				}
    			}
    			List<DataPoint> tailset = new ArrayList<>();
    			for(DataPoint pp : s.tailset.points){
    				if(s.gkl == null){
    					tailset.add(pp);
    					continue;
    				}
    				else if(s.gkl.points.get(s.gkl.points.size()-1).layeridx < pp.layeridx && s.gkl.points.get(s.gkl.points.size()-1).layernum == pp.layernum){
    					tailset.add(pp);
    					continue;
    				}
    				else if(s.gkl.points.get(s.gkl.points.size()-1).layernum + 1 == pp.layernum && cs.contains(pp)){
    					tailset.add(pp);
    					continue;
    				}
    				else
    					continue;
    			}
    			s.tailset.points = tailset;
    			for(DataPoint pr : s.tailset.points){
    				PDataNode ad = new PDataNode();
    				ad.gkl = new Dataset();
    				ad.gkl.points = new ArrayList<>();
    				HashSet<DataPoint> ps = new HashSet<>();
    				if(s.gkl != null){	
    					for(DataPoint tep :s.gkl.points){
    						ad.gkl.points.add(tep);	
    						ps.add(tep);
    					}
    				}
    				boolean ou = false;
    				for(int x = 0;x<pr.parents.size();x++){
    					if(!ps.contains(pr.parents.get(x))){
    						ou = true;
    						break;
    					}
    				}//judge parents are in G or not
    				if(ou == false){
    					ad.gkl.points.add(pr);//初始化gkl
    					
    					ad.tailset = new Dataset();
    					ad.tailset.points = new ArrayList<>();
    					for (int l = pr.layeridx+1;l<graph.layers.get(pr.layernum).points.size();l++) {
    						ad.tailset.points.add(graph.layers.get(pr.layernum).points.get(l));
    				    }
    					if(pr.layernum < k-1){
    						for(DataPoint p : graph.layers.get(pr.layernum+1).points){
    							ad.tailset.points.add(p);
    						}
    					}//初始化tailset									
    					tepGkl.add(ad);
    				}//筛选掉父节点不在G里面的GSkyline
    			}
    		}
    	}
    	return tepGkl;
    }
}