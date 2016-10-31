package edu.thu.gskyline;

import java.util.*;
import java.util.stream.Collectors;

import static edu.thu.gskyline.Combinations.combine;

public class GSkylinePointWiseImpl implements GSkylineService {

    //pwise
    public List<PDataNode> getGSkylineP(DirectedSkylineGraph graph, int k) {
        PDataNode root = new PDataNode();
        root.tailset = new Dataset();
        root.tailset.points = new ArrayList<>();
        for (Layer l : graph.layers) {
            for (DataPoint p : l.points) {
                root.tailset.points.add(p);
            }
        }

        List<PDataNode> tepGkl = new ArrayList<>();
        tepGkl.add(root);
        for (int i = 1; i <= k; i++) {
            List<PDataNode> Gkl = new ArrayList<>();
            for (PDataNode yu : tepGkl) {
                Gkl.add(yu);
            }
            while (tepGkl.size() != 0)
                tepGkl.remove(0);
            for (PDataNode s : Gkl) {
                HashSet<DataPoint> cs = new HashSet<>();
                if (s.gkl != null) {
                    for (DataPoint p : s.gkl.points) {
                        if (p.children.size() == 0)
                            continue;
                        for (int x = 0; x < p.children.size(); x++)
                            cs.add(p.children.get(x));
                    }
                }
                List<DataPoint> tailset = new ArrayList<>();
                for (DataPoint pp : s.tailset.points) {
                    int small;
                    boolean sk = false;
                    if (s.gkl == null) {
                        small = -1;
                    } else
                        small = s.gkl.points.get(s.gkl.points.size() - 1).idx;
                    int layernum = -1;
                    if (i >= 2) {
                        int endmark = 0;
                        for (Layer l : graph.layers) {
                            layernum++;
                            for (DataPoint p : l.points) {
                                if (small == p.idx) {
                                    endmark = 1;
                                    break;
                                }
                            }
                            if (endmark == 1)
                                break;
                        }

                        for (DataPoint pskl : graph.layers.get(layernum).points) {
                            if (pskl.idx <= small)
                                continue;
                            else if (pp.idx == pskl.idx) {
                                sk = true;
                                break;
                            }
                        }
                    } else
                        sk = false;
                    boolean ppjudge = false;
                    if ((!cs.contains(pp)) && sk == false) {
                        for (int j = 0; j < s.tailset.points.size(); j++) {
                            if (s.tailset.points.get(j) == pp) {
                                //s.tailset.points.remove(j);
                                ppjudge = true;
                                break;
                            }
                        }
                    }
                    if (ppjudge == false) {
                        boolean wthin = false;
                        for (int m = 1; m < i + 1; m++) {
                            for (DataPoint pt : graph.layers.get(m - 1).points) {
                                if (pp.idx == pt.idx) {
                                    boolean mr = false;
                                    for (DataPoint sam : s.gkl.points) {
                                        if (pp.idx == sam.idx) {
                                            mr = true;
                                            break;
                                        }
                                    }
                                    if (mr == false) {
                                        if (((m - 1) == layernum && pp.idx < small) || (m - 1 < layernum)) {
                                            wthin = false;
                                            break;
                                        }
                                        wthin = true;
                                        break;
                                    }

                                }
                            }
                            if (wthin == true)
                                break;
                        }
                        if (wthin == false) {
                            for (int j = 0; j < s.tailset.points.size(); j++) {
                                if (s.tailset.points.get(j) == pp) {
                                    //s.tailset.points.remove(j);
                                    ppjudge = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (ppjudge == false || (i == 1))
                        tailset.add(pp);
                }
                s.tailset.points = tailset;
                for (DataPoint pr : s.tailset.points) {
                    PDataNode ad = new PDataNode();
                    ad.tailset = new Dataset();
                    ad.tailset.points = new ArrayList<>();
                    for (Layer l : graph.layers) {
                        for (DataPoint p : l.points) {
                            ad.tailset.points.add(p);
                        }
                    }
                    ad.gkl = new Dataset();
                    ad.gkl.points = new ArrayList<>();
                    if (s.gkl != null) {
                        for (DataPoint tep : s.gkl.points)
                            ad.gkl.points.add(tep);
                    }
                    ad.gkl.points.add(pr);
                    //Gkl.add(ad);
                    int inorout = 0;
                    for (DataPoint z : ad.gkl.points) {
                        if (z.parents.size() == 0) {
                            //tepGkl.add(ad);
                            inorout = 1;
                            continue;
                        }
                        for (DataPoint zp : z.parents) {
                            inorout = 0;
                            for (DataPoint za : ad.gkl.points) {
                                if (zp.idx == za.idx) {
                                    inorout = 1;
                                    break;
                                }
                            }
                            if (inorout == 0)
                                break;
                        }
                        if (inorout == 0)
                            break;
                    }
                    if (inorout == 1) {
                        //Gkl.add(ad);
                        tepGkl.add(ad);
                    }
                }
                //Gkl.remove(0);
            }
        }
        return tepGkl;
    }

    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
        List<PDataNode> res = getGSkylineP(graph, k);
        List<Set<DataPoint>> ans = res.stream()
                .map(node -> new HashSet<>(node.gkl.points)).
                collect(Collectors.toCollection(LinkedList::new));
        return ans;
    }
}