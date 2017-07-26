package util;

import DAO.Floor;
import DAO.Point;
import com.csvreader.CsvReader;

import java.awt.geom.Line2D;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Pipe;
import java.util.*;

import static util.Utils.distanceBetweenTwoPoints;

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
    public static Point getNearestPointInRoad(Floor floor, Point point){
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

        public static void main (String[]args) {
//            String path = "src/main/data/5c.csv";
//            ArrayList<Point[]> allPointsPair = readCSV(path, "floor5");
//            writeRecords(allPointsPair, "src/main/data/5_data.txt");
            System.out.println(isIntersects(new Point[]{new Point(0.0, 0.0), new Point(0.9, 0.9)}, new Point[]{new Point(0.0, 2.0), new Point(2.0, 5.0)}));


        }

    }
