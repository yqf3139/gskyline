package edu.thu.gskyline;

import java.util.*;
import java.util.stream.Collectors;

import static edu.thu.gskyline.Combinations.combine;

public class GSkylineBaseImpl implements GSkylineService {

    // use brute force & Theorem 2
    @Override
    public List<Set<DataPoint>> getGSkyline(DirectedSkylineGraph graph, int k) {
        List<Set<DataPoint>> result = new LinkedList<>();
        List<DataPoint> points = new ArrayList<>();
        List<DataPoint> skyline = new ArrayList<>();

        long start = System.currentTimeMillis();
        preproccess(graph, k, points, skyline, result);
        long end = System.currentTimeMillis();
//        System.out.printf("%d,%d\n", k, end - start);

        // brute force method is too slow for such input
        if (points.size() > 80) return null;

        int[] tmp = new int[points.size()];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = i;
        }
        // get all the C-n-k combinations
        List<int[]> res = combine(tmp, tmp.length, k);

        // turn combinations into point sets and filter out impossible points
        List<Set<DataPoint>> prunedSets = res.stream().map(combination -> {
            Set<DataPoint> pointSet = new HashSet<>();
            for (int i = 0; i < combination.length; i++) {
                pointSet.add(points.get(combination[i]));
            }
            return pointSet;
        }).filter(pointSet -> {
            for (DataPoint p : pointSet) {
                if (!pointSet.containsAll(p.parents)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        List<Set<DataPoint>> toBeComparedSets = new LinkedList<>(prunedSets);
        toBeComparedSets.addAll(result);

        // compare set to all possible answer sets
        for (Set<DataPoint> set1 : prunedSets) {
            boolean survived = true;
            DataPoint[] arr1 = set1.toArray(new DataPoint[set1.size()]);
            DataPoint[] arr2 = new DataPoint[set1.size()];
            for (Set<DataPoint> set2 : toBeComparedSets) {
                if (set1 == set2) continue;

                arr2 = set2.toArray(arr2);
                if (groupDominate(arr2, arr1)) {
                    survived = false;
                    break;
                }
            }
            if (survived) result.add(set1);
        }
        return result;
    }

    protected void preproccess(
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
    }

    protected boolean groupDominate(DataPoint[] arr2, DataPoint[] arr1) {
        List<Integer> indexs = new LinkedList<>();
        for (int i = 0; i < arr1.length; i++) {
            indexs.add(i);
        }
        indexs = new ArrayList<>(indexs);
        boolean failedToDominate = false;
        do {
            for (int i = 0; i < arr1.length; i++) {
                if (!arr2[indexs.get(i)].dominate(arr1[i])) {
                    failedToDominate = true;
                    break;
                }
            }
            if (failedToDominate) break;
        } while (nextPerm(indexs));

        return !failedToDominate;
    }

    public static boolean nextPerm(List<Integer> a) {
        int i = a.size() - 2;
        while (i >= 0 && a.get(i) >= a.get(i + 1))
            i--;

        if (i < 0)
            return false;

        int j = a.size() - 1;
        while (a.get(i) >= a.get(j))
            j--;

        Collections.swap(a, i, j);
        Collections.reverse(a.subList(i + 1, a.size()));
        return true;
    }

}

