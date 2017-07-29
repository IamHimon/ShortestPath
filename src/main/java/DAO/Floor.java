package DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import static util.Utils.distanceBetweenTwoPoints;
import static util.Utils.isEqual;
import static util.Utils.print4J;


/**
 * Created by student on 2017/7/4.
 * 代表楼层的类
 * 所有可行点组成一个WeightedGraph。
 * 然后电梯，楼梯，扶梯分别构成数组。
 * 障碍物点单独分类。
 */
public class Floor {
    private Integer num_floor; //层数
    private String floor_name;
    private WeightedGraph graph;
    private ArrayList<String> common_points;
    private ArrayList<String> stairs;
    private ArrayList<String> lifts;
    private ArrayList<String> escalators;
    private ArrayList<String> barriers;
    private HashMap<String, Integer> point_id_map;
    private ArrayList<Point[]> allPointsPair;
    private ArrayList<Point[]> allBarrierPointsPair;
    private ArrayList<Point> allPoints; //不重复

    public Floor(Integer num_floor, String floor_name, WeightedGraph graph, ArrayList<Point> allPoints, ArrayList<Point[]> allPointsPair,
                 HashMap<String, Integer> point_id_map, ArrayList<Point[]> allBarrierPointsPair) {
        this.num_floor = num_floor;
        this.floor_name = floor_name;
        this.graph = graph;
        this.allPoints = allPoints;
        this.allPointsPair = allPointsPair;
        this.point_id_map = point_id_map;
        this.allBarrierPointsPair = allBarrierPointsPair;
        ArrayList<String> common_points = new ArrayList<>();
        ArrayList<String> stairs = new ArrayList<>();
        ArrayList<String> lifts = new ArrayList<>();
        ArrayList<String> escalators = new ArrayList<>();
        ArrayList<String> barriers = new ArrayList<>();
        for (String obj : point_id_map.keySet()) {
            if (obj.startsWith("S"))
                stairs.add(obj);
            else if (obj.startsWith("L"))
                lifts.add(obj);
            else if (obj.startsWith("E"))
                escalators.add(obj);
            else if (obj.contains("C"))
                common_points.add(obj);
        }
        for (Point p : this.getBarrierPoints()) {
            barriers.add(p.label);
        }

        this.common_points = common_points;
        this.stairs = stairs;
        this.lifts = lifts;
        this.escalators = escalators;
        this.barriers = barriers;

    }

    public Floor(Integer num_floor, String floor_name, WeightedGraph graph, ArrayList<String> common_points, ArrayList<String> stairs,
                 ArrayList<String> lifts, ArrayList<String> escalators, ArrayList<String> barriers, HashMap<String, Integer> point_id_map,
                 ArrayList<Point[]> allPointsPair, ArrayList<Point> allPoints) {
        this.num_floor = num_floor;
        this.floor_name = floor_name;
        this.graph = graph;
        this.common_points = common_points;
        this.stairs = stairs;
        this.lifts = lifts;
        this.escalators = escalators;
        this.barriers = barriers;
        this.point_id_map = point_id_map;
        this.allPointsPair = allPointsPair;
        this.allPoints = allPoints;
    }

    public Integer getNum_floor() {
        return num_floor;
    }

    public void setNum_floor(Integer num_floor) {
        this.num_floor = num_floor;
    }

    public String getFloor_name() {
        return floor_name;
    }

    public void setFloor_name(String floor_name) {
        this.floor_name = floor_name;
    }

    public WeightedGraph getGraph() {
        return graph;
    }

    public void setGraph(WeightedGraph graph) {
        this.graph = graph;
    }

    public ArrayList<String> getCommon_points() {
        return common_points;
    }

    public void setCommon_points(ArrayList<String> common_points) {
        this.common_points = common_points;
    }

    public ArrayList<String> getStairs() {
        return stairs;
    }

    public void setStairs(ArrayList<String> stairs) {
        this.stairs = stairs;
    }

    public ArrayList<String> getLifts() {
        return lifts;
    }

    public void setLifts(ArrayList<String> lifts) {
        this.lifts = lifts;
    }

    public ArrayList<String> getEscalators() {
        return escalators;
    }

    public void setEscalators(ArrayList<String> escalators) {
        this.escalators = escalators;
    }

    public HashMap<String, Integer> getPoint_id_map() {
        return point_id_map;
    }

    public void setPoint_id_map(HashMap<String, Integer> point_id_map) {
        this.point_id_map = point_id_map;
    }

    public ArrayList<String> getAll_points() {
        ArrayList<String> all = new ArrayList<>();
        all.addAll(this.common_points);
        all.addAll(this.lifts);
        all.addAll(this.stairs);
        all.addAll(this.escalators);
        return all;
    }


    public ArrayList<Point[]> getAllPointsPair() {
        return allPointsPair;
    }

    public void setAllPointsPair(ArrayList<Point[]> allPointsPair) {
        this.allPointsPair = allPointsPair;
    }

    public ArrayList<Point> getAllPoints() {
        return this.allPoints;
    }

    public void setAllPoints(ArrayList<Point> allPoints) {
        this.allPoints = allPoints;
    }

    public ArrayList<Point[]> getAllBarrierPointsPair() {
        return allBarrierPointsPair;
    }

    public void describeFloor() {
        System.out.println("Floor: " + this.floor_name);
        System.out.println("points ID map:");
        print4J(this.point_id_map);
        System.out.println("WeightedGraph:");
        graph.print();
        System.out.println("allPoint:");
        for (Point p:allPoints){
            System.out.print(p.label+",");
        }
        System.out.println();
        System.out.println("common_points");
        System.out.println(common_points);
        System.out.println("stairs:");
        System.out.println(stairs.toString());
        System.out.println("lifts:");
        System.out.println(lifts.toString());
        System.out.println("escalators:");
        System.out.println(escalators.toString());
        System.out.println("barriers:");
        System.out.println(barriers.toString());
    }

    public void printPointsPairs() {
        for (Point[] pair : this.allPointsPair) {
            System.out.println(pair[0].getLabel() + " -> " + pair[1].getLabel());
        }
    }

    public ArrayList<Point> getLiftPoints() {
        ArrayList<Point> liftPoints = new ArrayList<>();
        for (Point point : this.allPoints) {
            if (point.getLabel().startsWith("L")) {
                liftPoints.add(point);
            }
        }
        return liftPoints;
    }

    public ArrayList<Point> getStairPoints() {
        ArrayList<Point> stairPoints = new ArrayList<>();
        for (Point point : this.allPoints) {
            if (point.getLabel().startsWith("S")) {
                stairPoints.add(point);
            }
        }
        return stairPoints;
    }

    public ArrayList<Point> getEscalatorPoints() {
        ArrayList<Point> escalatorPoints = new ArrayList<>();
        for (Point point : this.allPoints) {
            if (point.getLabel().startsWith("E")) {
                escalatorPoints.add(point);
            }
        }
        return escalatorPoints;
    }

    public ArrayList<Point> getBarrierPoints() {
        ArrayList<Point> setBarrierPoints = new ArrayList<>();

        boolean isin;
        for (Point[] pp : allBarrierPointsPair) {
            for (Point point : pp) {
                isin = true;
                for (Point p : setBarrierPoints) {
                    if (isEqual(point, p)) {
                        isin = false;
                    }
                }
                if (isin) {
                    setBarrierPoints.add(point);
                }
            }
        }
        return setBarrierPoints;
    }

    public Point getPointByLabel(String label) {
        for (Point p : this.allPoints) {
            if (p.label.equals(label))
                return p;
        }
        return null;
    }

    /*
    *定义：模糊点，任意给出的一点，不知道他是在路网上还是在其他任何地方的点。
    * 路网点：来定义一条可行路的两个点，是最初的数据源，最初构建Floor的成员变量。
    *加入一点，传入这点与找到的最近（考虑障碍物）的路网点的pointPair（第一个是模糊点，第二个是路网点），
    *需要更新Floor的变量有allPoints，allPointsPair，point_id_map,Graph*/
    public void addCommonPoint(Point[] pointPair) throws Exception {
        if (pointPair.length != 2)
            throw new Exception("When add commonPoint to Floor, Please input a pair of point!");
        this.common_points.add(pointPair[0].label);
        this.allPoints.add(pointPair[0]);
        this.allPointsPair.add(pointPair);
        this.point_id_map.put(pointPair[0].label, point_id_map.size());

        //还需要将这个点加入到Graph，也就是重建。
        WeightedGraph t = new WeightedGraph(this.point_id_map.size());
        for (Object obj : this.point_id_map.keySet()) {
            t.setLabel(this.point_id_map.get(obj), obj);
        }

        for (int i = 0; i < allPointsPair.size(); i++) {
            int source = this.point_id_map.get(this.allPointsPair.get(i)[0].getLabel());
            int target = this.point_id_map.get(this.allPointsPair.get(i)[1].getLabel());
            Double weight = distanceBetweenTwoPoints(this.allPointsPair.get(i)[0], this.allPointsPair.get(i)[1]);

            t.addEdge(source, target, weight);
            // 无向图
            t.addEdge(target, source, weight);
        }

        this.graph = t;

        if (!t.isConnected()) {
            throw new Exception("The graph is not connected!");
        }

    }


    /*传入一个路网线段（一个pairPoint），和一个找到的距离起点最近的closedPoint，这个closedPoint肯定在线段上，然后将这个点加入到Floor的变量中，
    * 如果closedPoint在这个线段中间，一个pairPoint变成两个；如果跟端点重合则改变那个端点的label为closedPoint的label。
    * 并更新allPoints，allPointsPair，point_id_map,Graph
    * */
    public void addCommonPoint(Point[] pointPair, Point closedPoint) throws Exception {
        if (pointPair.length != 2) {
            throw new Exception("When add closedPoint to Floor, Please input a pair of point!\"");
        }

        //更新allPointsPair
        if (pointPair[0].getX().equals(closedPoint.getX())&&pointPair[0].getY().equals(closedPoint.getY())) {
            closedPoint.setLabel(pointPair[0].getLabel());
        } else if (pointPair[1].getX().equals(closedPoint.getX())&&pointPair[1].getY().equals(closedPoint.getY())) {
            closedPoint.setLabel(pointPair[1].getLabel());
        } else {
            Point[] pp1 = new Point[]{closedPoint, pointPair[0]};
            Point[] pp2 = new Point[]{closedPoint, pointPair[1]};

            //在allPointsPair中删除这个pointPair，然后加入这两个进去
            for (Point[] pp : allPointsPair) {
                if (isEqual(pp[0], pointPair[0]) && isEqual(pp[1], pointPair[1])) {
                    allPointsPair.remove(pp);
                    break;
                }
            }
            allPointsPair.add(pp1);
            allPointsPair.add(pp2);

            this.common_points.add(closedPoint.label);
            this.allPoints.add(closedPoint);
            this.point_id_map.put(closedPoint.label, point_id_map.size());

            //还需要将这个点加入到Graph，也就是重建。
            WeightedGraph t = new WeightedGraph(this.point_id_map.size());
            for (Object obj : this.point_id_map.keySet()) {
                t.setLabel(this.point_id_map.get(obj), obj);
            }

            for (int i = 0; i < allPointsPair.size(); i++) {
                int source = this.point_id_map.get(this.allPointsPair.get(i)[0].getLabel());
                int target = this.point_id_map.get(this.allPointsPair.get(i)[1].getLabel());
                Double weight = distanceBetweenTwoPoints(this.allPointsPair.get(i)[0], this.allPointsPair.get(i)[1]);

                t.addEdge(source, target, weight);
                // 无向图
                t.addEdge(target, source, weight);
            }
            this.graph = t;
            if (!t.isConnected()) {
                throw new Exception("The graph is not connected!");
            }
        }
//        this.describeFloor();
    }


}
