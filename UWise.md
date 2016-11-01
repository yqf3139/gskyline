##Unit group wise algorithm - UWise
PWise算法以点为单位扩充叶节点，但是由于G-Skyline group中的一个点不能被其他group中的点dominate，所以也就是说某个节点的unit group(点及它的父亲节点)一定也在同一个group中；所以得到UWise算法，即以unit group为单位扩充叶节点。

为了提高效率，有两个剪枝的过程：

>1. Superset pruning
>2. Tailset pruning

####1. Superset pruning
**去掉点个数超过k的candidate group，不再计算它的子树。
```java
    HashSet<DataPoint> union=get_union_unit_group(candidate);
    if(union.size()==k){//get one result
        result.add(union);
        continue;
    }else if(union.size()>k){//superset pruning
        continue;
```

####2. Tailset pruning
当前unit groups中的点的子节点，不再加入tailset。
```java
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
```
##Unit group wise algorithm - UWise+
UWise+在UWise基础上增加两个新的剪枝方法：
> 1. Unit Group Reordering
> 2. Subset pruning
####1. Unit Group Reordering
因为Superset pruning对削减candidate group很有效，所以希望重新排列unit group来增强Superset pruning的效果，即先处理大的candidate group；所以按照点的检索倒序排列，即把高层的skyline layer的点排在前面。

此时我们还需要修改tailset，因为原来的子节点在重新排序后变成了父节点。
```java
    private List<DataPoint> get_reverse_points(List<DataPoint> points){//unit group reordering
        List<DataPoint> reversePoints=new ArrayList<DataPoint>();
        for (int i=points.size()-1;i>=0;i--){
            reversePoints.add(points.get(i));
        }
        return  reversePoints;
    }
```
####2. Subset pruning
如果一个candidate group G<sub>i</sub>加上所有的tailset元素，即得到它子树中最深的子节点G<sub>i</sub><sup>last</sup>，G<sub>i</sub><sup>last</sup>的元素个数都不能达到k，则剪掉G<sub>i</sub>，不去计算它的子树。
```java
    int gLast=get_last_deepest_candidate_group(p,points);
    if(gLast<k){
        continue;
    }else if (gLast==k){
        Set<DataPoint> candidate=new HashSet<>();candidate.add(p);
        result.add(get_union_unit_group(candidate));
        continue;
    }
```