package edu.thu.gskyline;

import java.util.LinkedList;
import java.util.List;

// from the web
class Combinations {

    private static List<int[]> arrays;

    public static List<int[]> combine(int a[], int n, int m) {
        arrays = new LinkedList<>();
        //p[x]=y 取到的第x个元素，是a中的第y个元素
        int index;
        int[] p = new int[m];

        index = 0;
        p[index] = 0;//取第一个元素
        while (true) {
            if (p[index] >= n) {//取到底了，回退
                if (index == 0) {//各种情况取完了，不能再回退了
                    break;
                }
                index--;//回退到前一个
                p[index]++;//替换元素
            } else if (index == m - 1) {//取够了，输出
                int[] tmp = new int[p.length];
                System.arraycopy(p, 0, tmp, 0, p.length);
                arrays.add(tmp);
                p[index]++; //替换元素
            } else {//多取一个元素
                index++;
                p[index] = p[index - 1] + 1;
            }
        }
        return arrays;
    }

}
