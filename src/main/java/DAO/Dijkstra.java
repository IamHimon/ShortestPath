package DAO;

import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by student on 2017/7/4.
 */

public class Dijkstra {

    // Dijkstra's algorithm to find shortest path from s to all other nodes
    public static int[] dijkstra(WeightedGraph G, int s) {
        final Double[] dist = new Double[G.size()];  // shortest known distance from "s"
        final int[] pred = new int[G.size()];  // preceeding node in path
        final boolean[] visited = new boolean[G.size()]; // all false initially

        for (int i = 0; i < dist.length; i++) {
            dist[i] = Double.MAX_VALUE;
        }
        dist[s] = 0.0;

        for (int i = 0; i < dist.length; i++) {
            final int next = minVertex(dist, visited);
//            System.out.println("next: " + next);
            visited[next] = true;

            // The shortest path to next is dist[next] and via pred[next].

//            final int[] n = G.neighbors_directed(next);//有向图中间点的所有邻居点（待选点）
            final int[] n = G.neighbors_undirected(next); //无向图中间点的所有邻居点（待选点）
//            System.out.println("n: " + Arrays.toString(n));

            for (final int v : n) {
                final Double d = dist[next] + G.getWeight(next, v);
//                System.out.println("d: " + d);
                /*
                * d: 通过中间点（next），此待选邻居点到起点的距离
                * dist[v]：不通过中间点（next），此待选邻居点到起点的距离
                * 如果通过中间点的距离比原来距离（不通过中间点）小，则更新此待选邻居点距离值和pred。
                * */
                if (dist[v] > d) {
                    dist[v] = d;
                    pred[v] = next;
                }
            }

//            System.out.println("pred: " + Arrays.toString(pred));
//            System.out.println("dist: " + Arrays.toString(dist));
//            System.out.println("visited: " + Arrays.toString(visited));
        }
        return pred;  // (ignore pred[s]==0!)
    }

    /*
    *在所有没有访问过的邻居点中找到最小距离的那个点。
    * */
    private static int minVertex(Double[] dist, boolean[] v) {
//        System.out.println("in minVertex:");
//        System.out.println(Arrays.toString(dist));
//        System.out.println(Arrays.toString(v));
        Double x = Double.MAX_VALUE;
        int y = -1;   // graph not connected, or no unvisited vertices
        for (int i = 0; i < dist.length; i++) {
//            System.out.println(v[i]);
//            System.out.println(dist[i]);
//            System.out.println("---------");
            if (!v[i] && dist[i] < x) {
                y = i;
                x = dist[i];
            }
        }
        return y;
    }

    public static void printPath(WeightedGraph G, int[] pred, int s, int e) {
        final ArrayList path = new ArrayList();
        final ArrayList<Integer> pathID = new ArrayList();
        int x = e;
        while (x != s) {
            path.add(0, G.getLabel(x));
            pathID.add(0, x);
            x = pred[x];
        }
        path.add(0, G.getLabel(s));
        pathID.add(0, s);
        System.out.println(path);
        System.out.println(pathID);
//        System.out.println(G.pathDistance(pathID));
    }

    public static Quartet getResultTuple(WeightedGraph G, HashMap<String, Integer> point_id_map,int[] pred, String start, String end){
        int s = point_id_map.get(start);
        int e = point_id_map.get(end);
        final ArrayList path = new ArrayList();
        final ArrayList<Integer> pathID = new ArrayList();
        int x = e;
        while (x != s) {
            path.add(0, G.getLabel(x));
            pathID.add(0, x);
            x = pred[x];
        }
        path.add(0, G.getLabel(s));
        pathID.add(0, s);

        return Quartet.with(start, end, path, G.pathDistance(pathID));
    }
}