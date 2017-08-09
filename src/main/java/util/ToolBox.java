package util;

import DAO.Dijkstra;
import DAO.Floor;
import DAO.Point;
import DAO.WeightedGraph;
import com.csvreader.CsvReader;
import java.awt.geom.Line2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import static util.Utils.*;

/**
 * Created by 猛 on 2017/7/21.
 */
public class ToolBox {

    /*
    *将每行（每一对点写入文件）
    allPointsPair: [[Point p1, Point p2],[Point p4, Point p3],[],,,]*/
    public static void writeRecords(ArrayList<Point[]> allPointsPair, String wrightPath){
        try {
            final FileWriter fileWriter = new FileWriter(wrightPath);
            allPointsPair.stream().filter(pp -> pp.length==2).forEach(pp -> {

//                System.out.println(pp[0].label);
//                System.out.println(pp[1].label);
                // [C24#865.2242453,988.8116969]*[C25#877.217764,988.8116969]

                String record = "["+pp[0].label+"#"+pp[0].X+","+pp[0].Y+"]"+"*"+"["+pp[1].label+"#"+pp[1].X+","+pp[1].Y+"]";
                System.out.println(record);
                try {
                    fileWriter.write(record);
                    fileWriter.write('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            fileWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /*读csv文件，每行是一个Point，根据Line_label来判断两行的点是不是构成一条直线（一对Point），
    *根据getAllPoint返回的结果来对应每个Point*/
    public static ArrayList<Point[]> readCSV(String path, String floor_name) {
        Double X, Y;
        Double line_label;
        ArrayList<Double[]> allPointsRecord = new ArrayList<Double[]>();
         try {
             CsvReader csvReader = new CsvReader(path);

             csvReader.readHeaders();
             while (csvReader.readRecord()) {
                 if (!csvReader.get("X").equals("") &&! csvReader.get("Y").equals("") &&
                         !csvReader.get("ORIG_FID").equals("")) {
                     X = Double.parseDouble(csvReader.get("X"));
                     Y = Double.parseDouble(csvReader.get("Y"));
                     line_label = Double.parseDouble(csvReader.get("ORIG_FID"));
                     allPointsRecord.add(new Double[]{X, Y, line_label});
                 }
             }
         }catch (IOException e){
             e.printStackTrace();
         }


        ArrayList<Point> allPoints = getAllPoint(path, floor_name);
        ArrayList<Point[]> allPointsPair = new ArrayList<>();

        Point px = new Point();
        Point py = new Point();

        int i=0;
        while (i<allPointsRecord.size()){
//            System.out.println(Arrays.toString(allPointsRecord.get(i)));
            if (allPointsRecord.get(i+1)[2].equals(allPointsRecord.get(i)[2])){
//                System.out.println(Arrays.toString(allPointsRecord.get(i + 1)));
                for (Point p:allPoints){
                    if (p.X.equals(allPointsRecord.get(i)[0])&&p.Y.equals(allPointsRecord.get(i)[1])){
                        px = p;
                    }
                    if (p.X.equals(allPointsRecord.get(i+1)[0])&&p.Y.equals(allPointsRecord.get(i+1)[1])){
                        py= p;
                    }
                }
//                px.printPoint();
//                py.printPoint();
                allPointsPair.add(new Point[]{px, py});
                i += 1;
            }
            i += 1;
//            System.out.println("----------------");
        }

//        System.out.println(allPointsPair.size());
        return allPointsPair;
    }


/*获取所有不重复的点,并且给普通点命名（加上floor_name,从1开始）*/
    public static ArrayList<Point> getAllPoint(String path, String floor_name){
        boolean isDup = false;
        Double X, Y;
        String line_label;
        ArrayList<Point> allPoints = new ArrayList<Point>();

        try {
            CsvReader csvReader = new CsvReader(path);
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
                if (!csvReader.get("X").equals("") &&! csvReader.get("Y").equals("")) {
                    X = Double.parseDouble(csvReader.get("X"));
                    Y = Double.parseDouble(csvReader.get("Y"));

                    for (Point point:allPoints){
                        if (X.equals(point.X) &&Y.equals(point.Y)){
//                            System.out.println("["+point.X+","+point.Y+"]");
                            isDup = true;
                        }
                    }
//                    if (isDup)
//                        System.out.println("["+X+","+Y+"]");

                    if (!isDup){
                        if (csvReader.get("label").equals("")){
                            Point p = new Point(X, Y);
                            allPoints.add(p);
                        }else {
                            Point p = new Point(csvReader.get("label"), new Double[]{X, Y});
                            allPoints.add(p);
                        }
                    }
                }
                isDup = false;
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        //给普通点和障碍物点命名，
        Queue<Point> pointQueue = new LinkedList<Point>();
        int commonPointCount = 0;
        int barrierPointCount = 0;
        for (Point point:allPoints){
            if (point.label==null){
//                commonPointCount += 1;
//                point.setLabel(floor_name+ "_"+"C"+String.valueOf(commonPointCount));
                point.setLabel("C"+String.valueOf(++commonPointCount));
            }
            if (point.label.equals("B")){
                point.setLabel("B"+String.valueOf(++barrierPointCount));
            }
        }
        return allPoints;
    }

    public static void printPointsPair(HashMap<Point, Point> map){
        Iterator iter = map.entrySet().iterator();
//        System.out.print("{ ");
        while (iter.hasNext()) {
            HashMap.Entry entry = (Map.Entry) iter.next();
            Point key = (Point) entry.getKey();
            Point val = (Point) entry.getValue();
            key.printPoint();
            val.printPoint();
            System.out.println("---------");
        }
//        System.out.println("}");
    }


    public static boolean isIntersects(Point[] pair1, Point[] pair2) {
        if (pair1.length==2&&pair2.length==2){
            Line2D line1 = new Line2D.Double(pair1[0].X, pair1[0].Y, pair1[1].X, pair1[1].Y);
            Line2D line2 = new Line2D.Double(pair2[0].X, pair2[0].Y, pair2[1].X, pair2[1].Y);
            return line1.intersectsLine(line2);
        }else {
            System.out.println("Please input two pairs points!");
            return false;
        }
    }

    /*给定任意一点，在这一层找到离他最近的路上的点（也就是Floor构建Graph中的点），需要考虑障碍物的存在*/
    public static Point getNearestPointInRoadPoint(Floor floor, Point point){
        Double shortestDistance = Double.MAX_VALUE;
        ArrayList<Point> allPoints = floor.getAllPoints();
        ArrayList<Point[]> allBarrierPointsPair = floor.getAllBarrierPointsPair();
        boolean hasBarrier;
        int nearestPointIndex = 0;
        for (int i=0;i< allPoints.size();i++){
            hasBarrier = false;
            //判断allPoints.get(i)与point之间是否有障碍物
            for(Point[] pp:allBarrierPointsPair){
                if (isIntersects(new Point[]{allPoints.get(i), point}, pp)) {
                    hasBarrier = true;
                    break;
                }
            }
            Double distance = distanceBetweenTwoPoints(allPoints.get(i), point);
            if (!hasBarrier && distance < shortestDistance){
                shortestDistance = distance;
                nearestPointIndex = i;
            }
        }
//        System.out.println(nearestPointIndex);
        return allPoints.get(nearestPointIndex);
    }

    /*找到路网上最近的点（绕开障碍物），然后将这一点加入到Floor中，重构Graph*/
    public static Point getNearestPointOnRoadAndAddToFloor(Floor floor, Point point){
        Double shortestDistance = Double.MAX_VALUE;
        ArrayList<Point[]> allPointsPair = floor.getAllPointsPair();
        ArrayList<Point[]> allBarrierPointsPair = floor.getAllBarrierPointsPair();
        boolean hasBarrier;
        int nearestPointIndex = 0;
        ArrayList<Point> allClosedPoints = new ArrayList<>();

        for (int i=0;i<allPointsPair.size();i++){
            hasBarrier = false;
            try {
                Point closedPoint = getClosestPointOnSegment(allPointsPair.get(i), point);
                closedPoint.setLabel(floor.getFloor_name()+"_CCP");//name common closed point
                allClosedPoints.add(closedPoint);
                for(Point[] pp:allBarrierPointsPair){
                    if (isIntersects(new Point[]{closedPoint, point}, pp)) {
                        hasBarrier = true;
                        break;
                    }
                }
                Double distance = distanceBetweenTwoPoints(closedPoint, point);
//                System.out.println("i:"+i+",distance:"+distance+",hasBarrier:"+hasBarrier);
                if (!hasBarrier && distance < shortestDistance){
                    shortestDistance = distance;
                    nearestPointIndex = i;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        System.out.println("nearestPointIndex:"+nearestPointIndex);
//        System.out.println("shortestDistance:"+shortestDistance);
        System.out.println("The segment:");
        allPointsPair.get(nearestPointIndex)[0].printPoint();
        allPointsPair.get(nearestPointIndex)[1].printPoint();
        Point closedPoint = allClosedPoints.get(nearestPointIndex);
        try {
            floor.addCommonPoint(allPointsPair.get(nearestPointIndex),closedPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return closedPoint;
    }


/*返回线段上到某一点最近的点，传入一个线段pointsPair*/
    public static Point getClosestPointOnSegment(Point[] pointsPair, Point p) throws Exception {
        if (pointsPair.length!=2)
            throw new Exception("Please input a pair of points!");
        return getClosestPointOnSegment(pointsPair[0].X, pointsPair[0].Y, pointsPair[1].X, pointsPair[1].Y, p.X, p.Y);

    }

    /**
     * Returns closest point on segment to point
     *
     * @param sx1
     *            segment x coord 1
     * @param sy1
     *            segment y coord 1
     * @param sx2
     *            segment x coord 2
     * @param sy2
     *            segment y coord 2
     * @param px
     *            point x coord
     * @param py
     *            point y coord
     * @return closets point on segment to point
     */
    public static Point getClosestPointOnSegment(Double sx1, Double sy1, Double sx2, Double sy2, Double px, Double py)
    {
        Double xDelta = sx2 - sx1;
        Double yDelta = sy2 - sy1;

        if ((xDelta == 0.0) && (yDelta == 0.0))
        {
            throw new IllegalArgumentException("Segment start equals segment end");
        }

        Double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final Point closestPoint;
        if (u < 0.0)
        {
            closestPoint = new Point(sx1, sy1);
        }
        else if (u > 1.0)
        {
            closestPoint = new Point(sx2, sy2);
        }
        else
        {
            closestPoint = new Point(sx1 + u * xDelta,sy1 + u * yDelta);
        }

        return closestPoint;
    }

    /*根据不同的方式，构建bigGraph， 返回路径上的所有点*/
    public static ArrayList<Point> showPath( ArrayList<Floor> floors, Point startPoint, Point endPoint, String way) throws Exception {
        int start_id;
        int end_id;
        ArrayList<String> path = new ArrayList<>();

        switch (way) {
            case "S": {
                buildLinkedPointsPair_SLE(floors, "S");
                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_S);

                try {
                    start_id = allFloor_point_id_map.get(startPoint.getLabel());
                    end_id = allFloor_point_id_map.get(endPoint.getLabel());
                    final int[] pred = Dijkstra.dijkstra(t, start_id);
                    System.out.println("Stair path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
                    path = Dijkstra.getPointsInPath(t, pred, start_id, end_id);
                } catch (Exception e) {
                    throw new Exception("The node is not exit!");
                }
                break;
            }
            case "L": {
                buildLinkedPointsPair_SLE(floors, "L");
                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_L);
                try {
                    start_id = allFloor_point_id_map.get(startPoint.getLabel());
                    end_id = allFloor_point_id_map.get(endPoint.getLabel());
                    final int[] pred = Dijkstra.dijkstra(t, start_id);
                    System.out.println("Lift path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
                    path = Dijkstra.getPointsInPath(t, pred, start_id, end_id);
                } catch (Exception e) {
                    throw new Exception("The node is not exit!");
                }
                break;
            }
            case "E": {
                buildLinkedPointsPair_SLE(floors, "E");

                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_E);
//                System.out.println(t.isConnected());
//                print4J(allFloor_point_id_map);

                try {
                    start_id = allFloor_point_id_map.get(startPoint.getLabel());
                    end_id = allFloor_point_id_map.get(endPoint.getLabel());
                    final int[] pred = Dijkstra.dijkstra(t, start_id);
                    System.out.println("Escalator path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
                    path = Dijkstra.getPointsInPath(t, pred, start_id, end_id);
                } catch (Exception e) {
                    throw new Exception("The node is not exit!");
                }
                break;
            }
            case "A":{
                buildLinkedPointsPair(floors);
                WeightedGraph t = buildBigGraph(floors, LinkedPointsPair_SLE);
                try {
                    start_id = allFloor_point_id_map.get(startPoint.getLabel());
                    end_id = allFloor_point_id_map.get(endPoint.getLabel());
                    final int[] pred = Dijkstra.dijkstra(t, start_id);
                    System.out.println("Anyway path:");
//                    Dijkstra.printPath(t, pred, start_id, end_id);
                    path = Dijkstra.getPointsInPath(t, pred, start_id, end_id);

                } catch (Exception e) {
                    throw new Exception("The node is not exit!");
                }
                break;
            }
            default:
                System.out.println("please input the correct mode:(S,L,E,A)");
        }

        ArrayList<Point> allPoints = new ArrayList<>();
        for (Floor floor:floors){
            allPoints.addAll(floor.getAllPoints());
        }

        ArrayList<Point> allPointsInPath = new ArrayList<>();
        for (String p:path){
            for (Point point:allPoints)
                if (point.label.equals(p))
                    allPointsInPath.add(point);
        }
        return allPointsInPath;
    }


    /*给定随机起始点和目的点，是模糊点。返回路径多有点的集合*/
    public static ArrayList<Point> Trace(Floor startFloor, Point startRandomPoint, Floor endFloor, Point endRandomPoint, ArrayList<Floor> floors, String way) throws Exception {

        /*检测Floor的存放顺序对不对*/
        for (int i=0;i<floors.size()-1;i++){
            if(floors.get(i+1).getNum_floor() - floors.get(i).getNum_floor() != 1)
                throw new Exception("Please stow the floors with correct order!");
        }

        /*只选取起始层到目标层之间的floor，其他层都去掉*/
        int startFloorIndex = 0;
        int endFloorIndex = 0;
        for (int i=0;i<floors.size();i++){
            if (floors.get(i).getFloor_name().equals(startFloor.getFloor_name())) {
                startFloorIndex = i;
                continue;
            }
            if(floors.get(i).getFloor_name().equals(endFloor.getFloor_name())){
                endFloorIndex = i;
                break;
            }
        }
//        System.out.println("startFloorIndex:"+startFloorIndex);
//        System.out.println("endFloorIndex:"+endFloorIndex);

        for (int j=0;j<floors.size();j++){
            if (j<startFloorIndex||j>endFloorIndex){
                floors.remove(j);
            }
        }

        ArrayList<Point> allPointsInPath;
        //修改起点和终点的label，加上Floor_name前缀
        startRandomPoint.setLabel(startFloor.getFloor_name()+"_"+startRandomPoint.label);
        endRandomPoint.setLabel(endFloor.getFloor_name()+"_"+endRandomPoint.label);

        //起始点在其所在层找到最近的点，然后加入到这一层
        Point startCCP = getNearestPointOnRoadAndAddToFloor(startFloor, startRandomPoint);
        startCCP.printPoint();

        //目的地点在其所在层找到最近点，然后加入到这一层
        Point endCCP = getNearestPointOnRoadAndAddToFloor(endFloor, endRandomPoint);
        endCCP.printPoint();

        allPointsInPath =  showPath(floors, startCCP, endCCP, way);
        //起点和终点也加入到result中，
        allPointsInPath.add(0, startRandomPoint);
        allPointsInPath.add(endRandomPoint);
        return allPointsInPath;
    }



    public static void main (String[]args) {
//            String path = "src/main/data/5c.csv";
//            String path2 = "src/main/data/5.2.csv";
//            ArrayList<Point[]> allPointsPair = readCSV(path2, "floor5");
//            writeRecords(allPointsPair, "src/main/data/5.2_data.txt");

//            String path4 = "src/main/data/4.2.csv";
//            ArrayList<Point[]> allPointsPair = readCSV(path4, "floor4");
//            writeRecords(allPointsPair, "src/main/data/4.2_data.txt");

//            String path3 = "src/main/data/3.2.csv";
//            ArrayList<Point[]> allPointsPair = readCSV(path3, "floor3");
//            writeRecords(allPointsPair, "src/main/data/3.2_data.txt");


//            System.out.println(isIntersects(new Point[]{new Point(0.0, 0.0), new Point(0.9, 0.9)}, new Point[]{new Point(0.0, 2.0), new Point(2.0, 5.0)}));

//            Double dist = shortestDistance(1.0,0.0,1.0,2.0,2.6,0.5);
//            System.out.println(dist);
//            double dist2 = pointToLine(1.0,0.0,1.0,2.0,2.6,0.5);
//            System.out.println(dist2);
//            [C63#1044.397804,992.7476449]*[C47#1044.397804,988.8116969]
//            Point p = null;
//            try {
//                p = getClosestPointOnSegment(new Point[]{new Point(1044.397804,988.8116969), new Point(1044.397804,992.7476449)}, new Point(1053.066,990.246));
//                p.printPoint();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }



            try {
//                String file3 = "src/main/data/3.2_data.txt";
//                Floor floor3 = buildFloorFromFile(3, "floor3", file3);

                String file5 = "src/main/data/5.2_data.txt";
                Floor floor5 = buildFloorFromFile(5, "floor5", file5);
//                Point start = new Point("CF", new Double[]{1053.066,990.246});
//                Point start = new Point("CF", new Double[]{1047.066,990.246});
                Point end = new Point("ECF", new Double[]{996.002,989.779});

                getNearestPointOnRoadAndAddToFloor(floor5, end);

//                floor3.describeFloor();
            } catch (Exception e) {
                e.printStackTrace();
            }


//


        }

    }
