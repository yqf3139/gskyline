##Uwise+2 - 基于Uwise+算法的修改
Uwise+2方法，为Uwise+算法增加了一个Tailset pruning2方法：除了从Tailset中删除G的孩子，我们还从Tailset中删除parents个数大于k-G.size的点。（这里G仅指层点集合）
Tail Set Pruning 2: 在完成Tail Set Pruning处理后的tailset中，若点p有p.parents的个数大于k-G.size，则由于p不是candidate group G中的点的孩子节点，所以p.parents与G交集为空，所以G ∪ p.parents后形成的新的candidate group大小超过k,必然不是一个解。
```java
        int tailSize=k-set.size();
        for (DataPoint p:tailSet){
            if (p.parents.size()>tailSize){
                tailSet.remove(p);
            }
        }
```
##修改了Tailset中求差集的代码，使运行速度提高
```java
        List<DataPoint> temp=new ArrayList<>();
        temp.addAll(points);
        HashSet<DataPoint> tailSet = new HashSet<>(temp.subList(maxIdx+1,temp.size()));
        tailSet.removeAll(children);
```