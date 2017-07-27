package DAO;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by student on 2017/7/4.
 * 用邻接表来表示一个无向图，labels表示某个顶点名字，邻接表大小为：vertex.size *　vertex.size
 */
public class WeightedGraph {
    public int vexs;    //number of nodes
    public Double[][] edges;  // adjacency matrix
    public Object[] labels;    //label of node

    public WeightedGraph(int n) {
        edges = new Double[n][n];
        myFill(edges, 0.0);
        labels = new Object[n];
        vexs = n;
    }


    public int size() {
        return labels.length;
    }

    public void setLabel(int vertex, Object label) {
        labels[vertex] = label;
    }

    public Object getLabel(int vertex) {
        return labels[vertex];
    }

    public void addEdge(int source, int target, Double w) {
        edges[source][target] = w;
    }

    public boolean isEdge(int source, int target) {
        return edges[source][target] > 0;
    }

    public void removeEdge(int source, int target) {
        edges[source][target] = 0.0;
    }

    public Double getWeight(int source, int target) {
        return edges[source][target];
    }

    /*
    * 当定义int[n][m]初始值为0
    * edges类型定义为double[n][m]时初始值为null，所以要逐一初始化。
    * */
    private static void myFill(Object[][] a, Object val) {
        for (int i = 0, len = a.length; i < len; i++){
            for (int j = 0; j<a[i].length;j++){
                a[i][j] = val;
            }
        }
    }

    /*
    * 求某个顶点的邻居顶点,有向图
    * */
    public int[] neighbors_directed(int vertex) {
        int count = 0;
        for (int i = 0; i < edges[vertex].length; i++) {
            if (edges[vertex][i] > 0) count++;
        }
        final int[] answer = new int[count];
        count = 0;
        for (int i = 0; i < edges[vertex].length; i++) {
            if (edges[vertex][i] > 0) answer[count++] = i;
        }
        return answer;
    }
    /*
       * 求某个顶点的邻居顶点,无向图
       * */
    int[] neighbors_undirected(int vertex) {
        int count = 0;
        for (int i = 0; i < edges[vertex].length; i++) {
            if ((edges[vertex][i] > 0.0) || (edges[i][vertex] > 0.0)) count++;
        }
        final int[] answer = new int[count];
        count = 0;
        for (int i = 0; i < edges[vertex].length; i++) {
            if ((edges[vertex][i] > 0.0) || (edges[i][vertex] > 0.0)) answer[count++] = i;
        }
        return answer;
    }

    public void print() {
        for (int j = 0; j < edges.length; j++) {
            System.out.print(labels[j] + ": ");
            for (int i = 0; i < edges[j].length; i++) {
                if (edges[j][i] > 0) System.out.print(labels[i] + ":" + edges[j][i] + " ");
            }
            System.out.println();
        }
    }

     /*获取当前顶点K的第一个邻接顶点位置*/
     public int GetFirst(int k){
         int i;
         if(k<0||k>vexs-1){
             System.out.println("参数k值超出范围");
             return -1;
         }
         for(i=0;i<vexs;i++){
             if(this.getWeight(k, i)!=0.0)
                 return i;
         }
         return -1;
     }

     /*获取当前顶点K的第t个邻接顶点下一个邻接顶点的位置*/
     public int GetNext(int k,int t){
         int i;
         if(k<0||k>vexs-1||t<0||t>vexs-1){
             System.out.println("参数k或t值超出范围");
             return -1;
         }
         for(i=t+1;i<vexs;i++){
             if(this.getWeight(k, i)!=0.0)
                 return i;
         }
         return -1;
     }

     /*递归方式深度优先遍历图的邻接矩阵*/
     public int[] DFSVGraph(int k,int visited[]){
         int u;
         visited[k] = 1;
         u = GetFirst(k);
         while (u != -1){
             if (visited[u] == 0){
                 DFSVGraph(u, visited);
             }
             u = GetNext(k, u);
         }
         return visited;
     }

     /*判断图是不是连通图，只有连通图才能用Dijkstra来求最短路径*/
     public boolean isConnected(){
         boolean isCon = true;
         ArrayList<Object> isolatedPoints = new ArrayList<>();
         int[] visit = this.DFSVGraph(0, new int[this.vexs]);
//         System.out.println(Arrays.toString(visit));
         for(int i=0;i<visit.length;i++){
             if (visit[i]==0){
                 isCon = false;
                 isolatedPoints.add(this.labels[i]);
             }
         }
         //print isolated points
         if (!isolatedPoints.isEmpty())
//             System.out.println(Arrays.toString(visit));
            System.out.println(isolatedPoints);

         return isCon;
     }

/*give a path,and get the distance form the first node to the last node*/
     double pathDistance(ArrayList<Integer> path){
         double distance = 0.0;
         for(int i=0;i<path.size()-1;i++){
             distance += this.getWeight(path.get(i), path.get(i+1));
         }
         return distance;

     }

    public static void main1(String args[]) {
        final WeightedGraph t = new WeightedGraph(8);
        t.setLabel(0, "v0");
        t.setLabel(1, "v1");
        t.setLabel(2, "v2");
        t.setLabel(3, "v3");
        t.setLabel(4, "v4");
        t.setLabel(5, "v5");
        t.setLabel(6, "v6");
        t.setLabel(7, "v7");
        t.addEdge(0, 1, 2.0);
        t.addEdge(0, 5, 9.0);
        t.addEdge(1, 2, 8.0);
        t.addEdge(1, 3, 15.0);
        t.addEdge(1, 5, 6.0);
        t.addEdge(2, 3, 1.0);
        t.addEdge(4, 3, 3.0);
        t.addEdge(4, 2, 7.0);
        t.addEdge(5, 4, 3.0);
        t.addEdge(5, 6, 3.0);
        t.addEdge(6, 7, 5.0);
        t.addEdge(7, 4, 12.0);

        t.print();
        int [] vist = t.DFSVGraph(0,new int [t.vexs]);
        System.out.println(Arrays.toString(vist));
        System.out.println(t.isConnected());

        final int[] pred = Dijkstra.dijkstra(t, 0);
        for (int n = 0; n < 6; n++) {
            Dijkstra.printPath(t, pred, 0, n);
        }
    }

}
