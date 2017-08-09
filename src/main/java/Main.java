import DAO.Dijkstra;
import DAO.Floor;
import DAO.Point;
import DAO.WeightedGraph;
import org.javatuples.Quartet;
import java.util.ArrayList;

import static util.ToolBox.*;
import static util.Utils.*;


public class Main {
    public static ArrayList<String> stair_path = new ArrayList<>();
    public static ArrayList<String> lift_path = new ArrayList<>();
    public static ArrayList<String> escalator_path = new ArrayList<>();

    public static void climbing(ArrayList<Floor> floors, String start_point, String end_point, boolean is_start_point){

        ArrayList<Quartet<String, String, ArrayList<Object>,Double>> floor_result;
        if (floors.size() == 1){
//            floor_result = getResultWithinFloor(floors.get(0), start_point, end_point);
//            System.out.println(getResultWithinFloor(floors.get(0), start_point, end_point).getValue2());
            for (Object s:getResultWithinFloor(floors.get(0), start_point, end_point).getValue2()){
                stair_path.add(s.toString());
            }
        }else {
            stair_path.add(start_point);

            if (start_point.startsWith("C")&&!hasSameOne(floors.get(0), start_point, is_start_point)){

                floor_result = getResultWithinFloor(floors.get(0), start_point);
                // 在起始楼找到与起始点最近的点
                Quartet<String, String, ArrayList<Object>,Double> nearest_stair = Quartet.with("a", "b", new ArrayList<>('a'), Double.MAX_VALUE);
                Quartet<String, String, ArrayList<Object>,Double> nearest_lift = Quartet.with("a", "b", new ArrayList<>('a'), Double.MAX_VALUE);
                Quartet<String, String, ArrayList<Object>,Double> nearest_escalator = Quartet.with("a", "b", new ArrayList<>('a'), Double.MAX_VALUE);
                for (Quartet<String, String, ArrayList<Object>,Double> q:floor_result){
                    // find the nearest Stair
                    if (q.getValue1().startsWith("S")){
                        if (q.getValue3() < nearest_stair.getValue3()){
                            nearest_stair = q;
                        }
                    }else if(q.getValue1().startsWith("L")){
                        if (q.getValue3() < nearest_lift.getValue3()){
                            nearest_lift = q;
                        }
                    }else if(q.getValue1().startsWith("E")){
                        if (q.getValue3() < nearest_escalator.getValue3()){
                            nearest_escalator = q;
                        }
                    }
                }
//                System.out.println(nearest_stair);
//                System.out.println(floors);
//            System.out.println(nearest_lift);
//            System.out.println(nearest_escalator);
                //起始层将点加入到path
                if (is_start_point){
                    for (int i=1;i<nearest_stair.getSize()-1;i++){
                        stair_path.add(nearest_stair.getValue2().get(i).toString());
                    }
                }

                String start_point_in_next_floor = getSameOne(floors.get(1), nearest_stair.getValue1());
                floors.remove(0);
//                System.out.println(start_point_in_next_floor);
//                System.out.println(floors);

                climbing(floors, start_point_in_next_floor, end_point, false);
//            climbing(floors, nearest_stair.getValue1(), nearest_escalator);
//            climbing(floors, nearest_stair.getValue1(), nearest_escalator);

            }else {
                String start_point_in_next_floor = getSameOne(floors.get(1), start_point);
//                System.out.println(start_point_in_next_floor);
//                System.out.println(floors);
                floors.remove(0);
                climbing(floors, start_point_in_next_floor, end_point, false);
            }
        }
    }

    /*只通过stair的方式的最短路径，策略是：先从出发层找到最近stair，在层间判断是不是有同一个stair，如果有直接上楼
    * 如果没有，在找到最近stair，知道到目标层，然后在目标层从stair出发规划到目标点的路径.
    * 如果是普通点在前面加一个前缀：这一层的floor_name
    * */
    public static void stair_climbing(ArrayList<Floor> floors, String start_point, String end_point, boolean is_start_point){
        ArrayList<Quartet<String, String, ArrayList<Object>,Double>> floor_result;
        if (hasSLE(floors.get(0), "S")){
            System.out.println("Can not arrived through stair,as "+ floors.get(0).getFloor_name() +" has no stair!");
            return;
        }
        if (floors.size() == 1){
            for (Object s:getResultWithinFloor(floors.get(0), start_point, end_point).getValue2()){
                if (s.toString().startsWith("C"))
                    stair_path.add(floors.get(0).getFloor_name() +"_"+ s.toString());
                else
                    stair_path.add(s.toString());
            }
        }else {
            if (start_point.startsWith("C"))
                stair_path.add(floors.get(0).getFloor_name() +"_"+ start_point);
            else
                stair_path.add(start_point);


            if (start_point.startsWith("C")&&!hasSameOne(floors.get(0), start_point, is_start_point)){
                floor_result = getResultWithinFloor(floors.get(0), start_point);
                // 在起始楼找到与起始点最近的点
                Quartet<String, String, ArrayList<Object>,Double> nearest_stair = Quartet.with("a", "b", new ArrayList<>('a'), Double.MAX_VALUE);
                for (Quartet<String, String, ArrayList<Object>,Double> q:floor_result){
                    // find the nearest Stair
                    if (q.getValue1().startsWith("S")){
                        if (q.getValue3() < nearest_stair.getValue3()){
                            nearest_stair = q;
                        }
                    }
                }

                //起始层将点加入到path
                if (is_start_point){
                    for (int i=1;i<nearest_stair.getSize()-1;i++){
                        if (nearest_stair.getValue2().get(i).toString().startsWith("C"))
                            stair_path.add(floors.get(0).getFloor_name() + "_" +nearest_stair.getValue2().get(i).toString());
                        else
                            stair_path.add(nearest_stair.getValue2().get(i).toString());
                    }
                }

                String start_point_in_next_floor = getSameOne(floors.get(1), nearest_stair.getValue1());
                floors.remove(0);
//                System.out.println(start_point_in_next_floor);
//                System.out.println(floors);

                stair_climbing(floors, start_point_in_next_floor,end_point,false);
            }else {
                String start_point_in_next_floor = getSameOne(floors.get(1), start_point);
//                System.out.println(start_point_in_next_floor);
//                System.out.println(floors);
                floors.remove(0);
                stair_climbing(floors, start_point_in_next_floor, end_point, false);
            }
        }
    }

    public static void stair_climbing2(ArrayList<Floor> floors, String start_point, String end_point, boolean is_start_point){
        ArrayList<Quartet<String, String, ArrayList<Object>,Double>> floor_result;
        if (hasSLE(floors.get(0), "S")){
            System.out.println("Can not arrived through stair,as "+ floors.get(0).getFloor_name() +" has no stair!");
            return;
        }
        if (floors.size() == 1){
            for (Object s:getResultWithinFloor(floors.get(0), start_point, end_point).getValue2()){
                stair_path.add(s.toString());
            }
        }else {
            stair_path.add(start_point);

            if (start_point.contains("C")||hasSameOne(floors.get(0), start_point, is_start_point)){
                floor_result = getResultWithinFloor(floors.get(0), start_point);
                System.out.println(floor_result);
                // 在起始楼找到与起始点最近的点
                Quartet<String, String, ArrayList<Object>,Double> nearest_stair = Quartet.with("a", "b", new ArrayList<>('a'), Double.MAX_VALUE);
                for (Quartet<String, String, ArrayList<Object>,Double> q:floor_result){
                    // find the nearest Stair
                    if (q.getValue1().startsWith("S")){
                        if (q.getValue3() < nearest_stair.getValue3()){
                            nearest_stair = q;
                        }
                    }
                }
                System.out.println(nearest_stair);

                //起始层将点加入到path
                if (is_start_point){
                    for (int i=1;i<nearest_stair.getValue2().size();i++){
                        stair_path.add(nearest_stair.getValue2().get(i).toString());
                    }
                }
                System.out.println(nearest_stair.getValue1());
                String start_point_in_next_floor = getSameOne(floors.get(1), nearest_stair.getValue1());
                floors.remove(0);
                System.out.println(start_point_in_next_floor);
//                System.out.println(floors);
                for (Floor f:floors)
                    System.out.println(f.getFloor_name());

                stair_climbing2(floors, start_point_in_next_floor, end_point, false);
            }else {
                String start_point_in_next_floor = getSameOne(floors.get(0), start_point);
                System.out.println("else:" + start_point_in_next_floor);
//                System.out.println(floors);
                for (Floor f:floors)
                    System.out.println(f.getFloor_name());
                floors.remove(0);
                stair_climbing2(floors, start_point_in_next_floor, end_point, false);
            }
        }
    }

    public static void lift_climbing(ArrayList<Floor> floors, String start_point, String end_point, boolean is_start_point) {
        ArrayList<Quartet<String, String, ArrayList<Object>, Double>> floor_result;
        if (hasSLE(floors.get(0), "L")) {
            System.out.println("Can not arrived through lift,as " + floors.get(0).getFloor_name() + " has no lift!");
            return;
        }
        if (floors.size() == 1) {
            for (Object s : getResultWithinFloor(floors.get(0), start_point, end_point).getValue2())
                lift_path.add(s.toString());
        } else {
            lift_path.add(start_point);

            if (start_point.contains("C") && !hasSameOne(floors.get(0), start_point, is_start_point)) {

                floor_result = getResultWithinFloor(floors.get(0), start_point);
                // 在起始楼找到与起始点最近的点
                Quartet<String, String, ArrayList<Object>, Double> nearest_lift = Quartet.with("a", "b", new ArrayList<>('a'), Double.MAX_VALUE);
                for (Quartet<String, String, ArrayList<Object>, Double> q : floor_result) {
                    // find the nearest Stair
                    if (q.getValue1().contains("L")) {
                        if (q.getValue3() < nearest_lift.getValue3()) {
                            nearest_lift = q;
                        }
                    }
                }

                //起始层将点加入到path
                if (is_start_point) {
                    for (int i = 1; i < nearest_lift.getValue2().size(); i++) {
                        lift_path.add(nearest_lift.getValue2().get(i).toString());
                    }

                    String start_point_in_next_floor = getSameOne(floors.get(1), nearest_lift.getValue1());
                    floors.remove(0);
//                System.out.println(start_point_in_next_floor);
//                System.out.println(floors);

                    lift_climbing(floors, start_point_in_next_floor, end_point, false);

                } else {
                    String start_point_in_next_floor = getSameOne(floors.get(1), start_point);
//                System.out.printf("else: " + start_point_in_next_floor);
                    floors.remove(0);
                    lift_climbing(floors, start_point_in_next_floor, end_point, false);
                }
            }
        }
    }

    public static void escalator_climbing(ArrayList<Floor> floors, String start_point, String end_point, boolean is_start_point){

        ArrayList<Quartet<String, String, ArrayList<Object>,Double>> floor_result;
        if (hasSLE(floors.get(0), "E")){
            System.out.println("Can not arrived through escalator, as "+ floors.get(0).getFloor_name() +" has no escalator!");
            return;
        }
        if (floors.size() == 1){
            for (Object s:getResultWithinFloor(floors.get(0), start_point, end_point).getValue2()){
//                escalator_path.add(s.toString());
                if (s.toString().startsWith("C"))
                    escalator_path.add(floors.get(0).getFloor_name() +"_"+ s.toString());
                else
                    escalator_path.add(s.toString());
            }
        }else {
//            escalator_path.add(start_point);
            if (start_point.startsWith("C"))
                escalator_path.add(floors.get(0).getFloor_name() +"_"+ start_point);
            else
                escalator_path.add(start_point);

            if (start_point.startsWith("C")&&!hasSameOne(floors.get(0), start_point, is_start_point)){

                floor_result = getResultWithinFloor(floors.get(0), start_point);
                // 在起始楼找到与起始点最近的点
                Quartet<String, String, ArrayList<Object>,Double> nearest_escalator = Quartet.with("a", "b", new ArrayList<>('a'), Double.MAX_VALUE);

                for (Quartet<String, String, ArrayList<Object>,Double> q:floor_result){
                    // find the nearest Stair
                    if (q.getValue1().startsWith("E")){
                        if (q.getValue3() < nearest_escalator.getValue3()){
                            nearest_escalator = q;
                        }
                    }
                }

                //起始层将点加入到path
                if (is_start_point){
                    for (int i=1;i<nearest_escalator.getSize()-1;i++){
//                        escalator_path.add(nearest_escalator.getValue2().get(i).toString());
                        if (nearest_escalator.getValue2().get(i).toString().startsWith("C"))
                            escalator_path.add(floors.get(0).getFloor_name() + "_" +nearest_escalator.getValue2().get(i).toString());
                        else
                            escalator_path.add(nearest_escalator.getValue2().get(i).toString());
                    }
                }

                String start_point_in_next_floor = getSameOne(floors.get(1), nearest_escalator.getValue1());
                floors.remove(0);
//                System.out.println(start_point_in_next_floor);
//                System.out.println(floors);

                escalator_climbing(floors, start_point_in_next_floor, end_point, false);

            }else {
                String start_point_in_next_floor = getSameOne(floors.get(1), start_point);
//                System.out.println("else: " + start_point_in_next_floor);
                floors.remove(0);
                escalator_climbing(floors, start_point_in_next_floor, end_point, false);
            }
        }
    }

//    public static void showPath( ArrayList<Floor> floors, Floor start, String startNode, Floor end, String endNode, String way) throws Exception {
//        int start_id;
//        int end_id;
//
//        switch (way) {
//            case "S": {
//                buildLinkedPointsPair_SLE(floors, "S");
//                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_S);
//
//                try {
//                    start_id = allFloor_point_id_map.get(start.getFloor_name() + "_" + startNode);
//                    end_id = allFloor_point_id_map.get(end.getFloor_name() + "_" + endNode);
//                    final int[] pred = Dijkstra.dijkstra(t, start_id);
//                    System.out.println("Stair path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
//                } catch (Exception e) {
//                    throw new Exception("The node is not exit!");
//                }
//                break;
//            }
//            case "L": {
//                buildLinkedPointsPair_SLE(floors, "L");
//                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_L);
//                try {
//                    start_id = allFloor_point_id_map.get(start.getFloor_name() + "_" + startNode);
//                    end_id = allFloor_point_id_map.get(end.getFloor_name() + "_" + endNode);
//                    final int[] pred = Dijkstra.dijkstra(t, start_id);
//                    System.out.println("Lift path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
//                } catch (Exception e) {
//                    throw new Exception("The node is not exit!");
//                }
//                break;
//            }
//            case "E": {
//                buildLinkedPointsPair_SLE(floors, "E");
//
//                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_E);
//                System.out.println(t.isConnected());
//                print4J(allFloor_point_id_map);
//
//                try {
//                    start_id = allFloor_point_id_map.get(start.getFloor_name() + "_" + startNode);
//                    end_id = allFloor_point_id_map.get(end.getFloor_name() + "_" + endNode);
//                    System.out.println(start_id);
//                    System.out.println(end_id);
//                    final int[] pred = Dijkstra.dijkstra(t, start_id);
//                    System.out.println("Escalator path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
//                } catch (Exception e) {
//                    throw new Exception("The node is not exit!");
//                }
//                break;
//            }
//            case "A":{
//                buildLinkedPointsPair(floors);
//                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_SLE);
//                try {
//                    start_id = allFloor_point_id_map.get(start.getFloor_name() + "_" + startNode);
//                    end_id = allFloor_point_id_map.get(end.getFloor_name() + "_" + endNode);
//                    final int[] pred = Dijkstra.dijkstra(t, start_id);
//                    System.out.println("Anyway path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
//                } catch (Exception e) {
//                    throw new Exception("The node is not exit!");
//                }
//                break;
//            }
//            default:
//                System.out.println("please input the correct mode:(S,L,E,A)");
//        }
//
//    }



    /*给定随机起始点和目的点，是模糊点。返回路径多有点的集合*/
    public static ArrayList<Point> Trace1(Floor startFloor, Point startRandomPoint, Floor endFloor, Point endRandomPoint, ArrayList<Floor> floors, String way){
        ArrayList<Point> allPointsInPath = new ArrayList<>();

        //修改起点和终点的label，加上Floor_name前缀
        startRandomPoint.setLabel(startFloor.getFloor_name()+"_"+startRandomPoint.label);
        endRandomPoint.setLabel(endFloor.getFloor_name()+"_"+endRandomPoint.label);


        //起始点在其所在层找到最近的点，然后加入到这一层
        getNearestPointOnRoadAndAddToFloor(startFloor, startRandomPoint);

        Point sp = getNearestPointInRoadPoint(startFloor, startRandomPoint);
        System.out.println("The nearest RoadPoint to the start_random_point:");
        sp.printPoint();
        try {
            startFloor.addCommonPoint(new Point[]{startRandomPoint, sp});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        startFloor.describeFloor();
        //目的地点在其所在层找到最近点，然后加入到这一层
        Point ep = getNearestPointInRoadPoint(endFloor, endRandomPoint);
        System.out.println("The nearest RoadPoint to the end_random_point:");
        ep.printPoint();
        try {
            endFloor.addCommonPoint(new Point[]{endRandomPoint, ep});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        endFloor.describeFloor();

        //然后调用showPaht方法
//        ArrayList<Floor> newFlorrs = new ArrayList<>();
//        newFlorrs.add(startFloor);
//        newFlorrs.add(endFloor);
        try {
            allPointsInPath =  showPath(floors, startRandomPoint, endRandomPoint, way);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allPointsInPath;
    }



    public static void main(String[] args) throws Exception {
        String file3 = "src/main/data/3.2_data.txt";
        String file5 = "src/main/data/5.2_data.txt";
        String file4 = "src/main/data/4.2_data.txt";
        Floor floor3 = buildFloorFromFile(3, "floor3", file3);
        Floor floor5 = buildFloorFromFile(5, "floor5", file5);
        Floor floor4 = buildFloorFromFile(4, "floor4", file4);

        ArrayList<Floor> floors = new ArrayList<>();
        floors.add(floor3);
        floors.add(floor4);
        floors.add(floor5);
        //start common free point
//        Point start = new Point("SCFP", new Double[]{1053.066,990.246}); //三楼
        Point start = new Point("SCFP", new Double[]{930.6040,995.7782}); //三楼起点
//        Point end = new Point("ECFP", new Double[]{1044.3379,999.6648}); //3楼终点

        //end common free point
//        Point end = new Point("ECFP", new Double[]{996.002,989.779}); //五楼
//        Point end = new Point("ECFP", new Double[]{1043.702,999.082}); //五楼
        Point end = new Point("ECFP", new Double[]{1045.934,1000.458}); //4楼终点

//
        ArrayList<Point> resultS = Trace(floor3, start, floor4, end, floors, "E");
        System.out.print("[");
        for (Point point:resultS) {
            System.out.print("{x:" + point.X + ",y:" + point.Y + "," + "id:\"" + point.label + "\"},");
        }
        System.out.print("]");

        //
        /*ArrayList<Point> resultL = Trace(floor3, start, floor4, end, floors, "L");
        System.out.print("[");
        System.out.print("{x:" + start.X + ",y:" + start.Y + "," + "id:\"" + start.label + "\"},");
        for (Point point:resultL) {
            System.out.print("{x:" + point.X + ",y:" + point.Y + "," + "id:\"" + point.label + "\"},");
        }
        System.out.print("{x:" + end.X + ",y:" + end.Y + "," + "id:\"" + end.label + "\"},");
        System.out.print("]");
*/



//
//        ArrayList<Point> resultE = Trace(floor4, start, floor5, end, floors, "E");
//        for (Point point:resultE)
//            System.out.print(point.label +":"+"["+point.X+","+point.Y+"] => ");




//        Point p = getNearestPointInRoad(floor5, new Point(1017.601848,995.3833048));
//        p.printPoint();


/*
        String filename1 = "src/main/data2/3.1.txt";
        String filename2 = "src/main/data2/4.1.txt";
        String filename3 = "src/main/data2/5.1.txt";

        //按顺序添加floor

        Floor floor1 = buildFloorFromFile(3, "floor3", filename1);
        //给定一个模糊点，先寻找到他最近的路网点
        Point randomP = new Point("floor3_CR", new Double[]{1017.601848,995.3833048});
        Point p = getNearestPointInRoad(floor1, randomP);
        p.printPoint();
        floor1.addCommonPoint(new Point[]{randomP, p});

        floor1.describeFloor();

        Floor floor2 = buildFloorFromFile(4, "floor4", filename2);

        Floor floor3 = buildFloorFromFile(5, "floor5", filename3);

        ArrayList<Floor> floors = new ArrayList<>();
        floors.add(floor1);
        floors.add(floor2);
        floors.add(floor3);

        showPath(floors, floor1, "C5", floor3, "C10", "A");
       showPath(floors, floor1, "C2", floor3, "C19", "A");
        showPath(floors, floor1, "C1", floor3, "C27", "L");
        showPath(floors, floor1, "C2", floor3, "C20", "S");
        showPath(floors, floor1, "C5", floor3, "C10", "E");
*/

//        buildLinkedPointsPair_SLE(floors, "S");
//        System.out.println(LinkedPointsPair_S);
//
//        buildLinkedPointsPair_SLE(floors, "L");
//        System.out.println(LinkedPointsPair_L);
////        buildLinkedPointsPair_SLE(floors, "E");
//        WeightedGraph st = buildBigGraph(floors,LinkedPointsPair_S);
//        WeightedGraph lt = buildBigGraph(floors,LinkedPointsPair_L);
//        WeightedGraph et = buildBigGraph(floors,LinkedPointsPair_E);
//        print4J(allFloor_point_id_map);
//
//        final int[] pred = Dijkstra.dijkstra(t, 60);
//
//        Dijkstra.printPath(t, pred, 60, 192);


//        print4J(floor1.getPoint_id_map());
//        print4J(floor2.getPoint_id_map());
//        print4J(floor3.getPoint_id_map());

//        stair_climbing2((ArrayList<Floor>) floors.clone(), "floor3_C8", "floor5_C23", true);
//        lift_climbing((ArrayList<Floor>) floors.clone(), "floor3_C4", "floor5_C27", true);
//        escalator_climbing((ArrayList<Floor>) floors.clone(), "floor1_C8", "floor3_C23", true);

//        System.out.println("stair path:" + stair_path);
//        System.out.println("lift path:" + lift_path);
//        System.out.println("escalator path:" + escalator_path);

    }

}

