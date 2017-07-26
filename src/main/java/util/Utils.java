package util;

import DAO.Dijkstra;
import DAO.Floor;
import DAO.Point;
import DAO.WeightedGraph;
import org.javatuples.Quartet;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by student on 2017/7/5.
 * 工具类
 */
public class Utils {
    //相邻两层之间的对应的两个点
    public static ArrayList<Point[]> LinkedPointsPair_SLE = new ArrayList<>();
    public static ArrayList<Point[]> LinkedPointsPair_S = new ArrayList<>();
    public static ArrayList<Point[]> LinkedPointsPair_L = new ArrayList<>();
    public static ArrayList<Point[]> LinkedPointsPair_E = new ArrayList<>();
    /*所有层点的id_map*/
    public  static HashMap<String, Integer> allFloor_point_id_map = new HashMap<>();

    //构造LinkedPointsPair,可以选择模式。
    public static void buildLinkedPointsPair_SLE(ArrayList<Floor> floors, String SLE) throws Exception{
        for (int i = 0; i < floors.size() - 1; i++) {
            if (SLE.equals("L")) {
                for (Point point : floors.get(i).getLiftPoints()) {
                    for (Point next_point : floors.get(i + 1).getLiftPoints()) {
                        if (isSameOne(point, next_point) && (floors.get(i + 1).getNum_floor() - floors.get(i).getNum_floor() == 1)) {
                            LinkedPointsPair_L.add(new Point[]{point, next_point});
                        }
                    }
                }
            }else if (SLE.equals("S")) {
                for (Point point : floors.get(i).getStairPoints()) {
                    for (Point next_point : floors.get(i + 1).getStairPoints()) {
                        if (isSameOne(point, next_point) && (floors.get(i + 1).getNum_floor() - floors.get(i).getNum_floor() == 1)) {
                            LinkedPointsPair_S.add(new Point[]{point, next_point});
                        }
                    }
                }
            }else if (SLE.equals("E")) {
                for (Point point : floors.get(i).getEscalatorPoints()) {
                    for (Point next_point : floors.get(i + 1).getEscalatorPoints()) {
                        if (isSameOne(point, next_point) && (floors.get(i + 1).getNum_floor() - floors.get(i).getNum_floor() == 1)) {
                            LinkedPointsPair_E.add(new Point[]{point, next_point});
                        }
                    }
                }
            }else {
                throw new Exception("Please input the correct attribute value,SLE should be one of UPPERCASE 'L','S','E'");
            }
        }
    }

    public static void buildLinkedPointsPair(ArrayList<Floor> floors) {
        for (int i = 0; i < floors.size() - 1; i++) {
            for (Point point:floors.get(i).getLiftPoints()){
                for (Point next_point:floors.get(i+1).getLiftPoints()){
                    if (isSameOne(point, next_point)&&(floors.get(i+1).getNum_floor() - floors.get(i).getNum_floor()==1)){
                        LinkedPointsPair_SLE.add(new Point[]{point, next_point});
                    }
                }
            }
            for (Point point:floors.get(i).getStairPoints()) {
                for (Point next_point : floors.get(i + 1).getStairPoints()) {
                    if (isSameOne(point, next_point)&&(floors.get(i+1).getNum_floor() - floors.get(i).getNum_floor()==1)) {
                        LinkedPointsPair_SLE.add(new Point[]{point, next_point});
                    }
                }
            }
            for (Point point:floors.get(i).getEscalatorPoints()) {
                for (Point next_point : floors.get(i + 1).getEscalatorPoints()) {
                    if (isSameOne(point, next_point)&&(floors.get(i+1).getNum_floor() - floors.get(i).getNum_floor()==1)) {
                        LinkedPointsPair_SLE.add(new Point[]{point, next_point});
                    }
                }
            }
        }
    }

    public static void buildallFloor_point_id_map(ArrayList<Floor> floors){
        //构造大图
        ArrayList<Point[]> allPointsPair = new ArrayList<>();
        ArrayList<Point> allPoints = new ArrayList<>();
        for (Floor floor:floors){
            allPointsPair.addAll(floor.getAllPointsPair());
            allPoints.addAll(floor.getAllPoints());
        }

        for (int i=0;i<allPoints.size();i++){
            allFloor_point_id_map.put(allPoints.get(i).getLabel(), i);
        }

    }

    public static WeightedGraph buildBigGraph(ArrayList<Floor> floors,ArrayList<Point[]> LinkedPointsPair) throws Exception {
        Double shortestEdges = getShortestEdges(floors);

        //构造大图
        ArrayList<Point[]> allPointsPair = new ArrayList<>();
        ArrayList<Point> allPoints = new ArrayList<>();
//        HashMap<String, Integer> point_id_map = new HashMap<>();
        for (Floor floor:floors){
            allPointsPair.addAll(floor.getAllPointsPair());
            allPoints.addAll(floor.getAllPoints());
        }
//        allPointsPair.addAll(LinkedPointsPair);
        //
        for (int i=0;i<allPoints.size();i++){
            allFloor_point_id_map.put(allPoints.get(i).getLabel(), i);
        }

//        System.out.println(allPoints.size());
//        print4J(allFloor_point_id_map);

        WeightedGraph t = new WeightedGraph(allFloor_point_id_map.size());
        for (Object obj:allFloor_point_id_map.keySet()){
            t.setLabel(allFloor_point_id_map.get(obj), obj);
        }

        for (int i = 0; i<allPointsPair.size();i++){
            int source = allFloor_point_id_map.get(allPointsPair.get(i)[0].getLabel());
            int target = allFloor_point_id_map.get(allPointsPair.get(i)[1].getLabel());
            Double weight = distanceBetweenTwoPoints(allPointsPair.get(i)[0], allPointsPair.get(i)[1]);

            t.addEdge(source, target, weight);
            // 无向图
            t.addEdge(target, source, weight);
        }
        //层间点单独构建
        for (int i = 0; i<LinkedPointsPair.size();i++){
            int source = allFloor_point_id_map.get(LinkedPointsPair.get(i)[0].getLabel());
            int target = allFloor_point_id_map.get(LinkedPointsPair.get(i)[1].getLabel());

            t.addEdge(source, target, shortestEdges/2);
            // 无向图
            t.addEdge(target, source, shortestEdges/2);
        }
        if (!t.isConnected())
            throw new Exception("The big grapth is not connected, Can't connected through Stair/Lift/Escalator.");

        return t;
    }

/*从文件中读所有的Point对来构建图和Floor对象，根据Point的名字，Barrier点单独存起来传入Floor对象作为成员变量，其他点来构建Graph*/
    public static Floor buildFloorFromFile(Integer num_floor, String floor_name, String filename)throws Exception{
        String regex = "^\\[.+\\#[0-9]+(\\.[0-9]+)?\\,[0-9]+(\\.[0-9]+)?\\]\\*\\[.+\\#[0-9]+(\\.[0-9]+)?\\,[0-9]+(\\.[0-9]+)?\\]$";

        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;

        String str;
        ArrayList<Point[]> allPointsPair = new ArrayList<>();
        ArrayList<Point> allPoints = new ArrayList<>();
        ArrayList<Point[]> allBarrierPointsPair = new ArrayList<>();

        ArrayList<Point> setPoints = new ArrayList<>();
        HashMap<String, Integer> point_id_map = new HashMap<>();

        try{
            inputStream = new FileInputStream(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while((str = bufferedReader.readLine()) != null)
            {
                if (Pattern.matches(regex, str)){
                    String[] line = str.split("\\*");
                    Point p1 = buildPoint(line[0], floor_name); //需要给普通点重命名
                    Point p2 = buildPoint(line[1], floor_name);
                    if (p1.label.startsWith("B")&&p2.label.startsWith("B")){
                        allBarrierPointsPair.add(new Point[]{p1, p2});
                    }else {
                        allPoints.add(p1);
                        allPoints.add(p2);
                        allPointsPair.add(new Point[]{p1, p2});
                    }
                }else {
                    throw new Exception("data: '" + str + "' is not correct format," +" Please modify it!");
                }
            }

            // 去掉重复点
            boolean isin;
            for (Point point:allPoints){
                isin = true;
                for(Point p: setPoints){
                    if (isEqual(point, p)){
                        isin = false;
                    }
                }
                if(isin){
                    setPoints.add(point);
                }
            }

            //在这边检测是不是有错误数据
            if (hasErrorPoint(setPoints)){
                throw new Exception("There are two points inconsistent!");
            }

            for (int i=0;i<setPoints.size();i++){
                point_id_map.put(setPoints.get(i).getLabel(), i);
            }
//            allPoints.clear();

        }catch (FileNotFoundException e){
            System.out.println("找不到指定文件");
        }catch (IOException e){
            System.out.println("读取文件失败");
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 读取完毕，构造WeightedGraph
//        print4J(point_id_map);


        WeightedGraph t = new WeightedGraph(point_id_map.size());
        for (Object obj:point_id_map.keySet()){
            t.setLabel(point_id_map.get(obj), obj);
        }

        for (int i = 0; i<allPointsPair.size();i++){
            int source = point_id_map.get(allPointsPair.get(i)[0].getLabel());
            int target = point_id_map.get(allPointsPair.get(i)[1].getLabel());
            Double weight = distanceBetweenTwoPoints(allPointsPair.get(i)[0], allPointsPair.get(i)[1]);

            t.addEdge(source, target, weight);
            // 无向图
            t.addEdge(target, source, weight);
        }

        if (!t.isConnected()){
            throw new Exception("The graph is not connected!");
        }

        return new Floor(num_floor, floor_name, t, setPoints, allPointsPair, point_id_map, allBarrierPointsPair);
    }

    /*
* 读文件，构建WeightedGraph。
* 文件格式：[C1#22.3,1.3]*[C2#12.3,45.6]， [],[]代表两个可以走通的点，放在一行来表示一条路线。
* 每个[]内，#之前部分表示点的类别（名称），#后面表示点的坐标。
* */
    public static WeightedGraph buildFromFile(String filename)throws Exception{
        String regex = "^\\[[A-Z].+\\#[0-9]+(\\.[0-9]+)?\\,[0-9]+(\\.[0-9]+)?\\]\\*\\[[A-Z].+\\#[0-9]+(\\.[0-9]+)?\\,[0-9]+(\\.[0-9]+)?\\]$";

        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;

        String str;
        ArrayList<Point[]> allPointsPair = new ArrayList<>();
        List<Point> allPoints = new ArrayList<>();
        List<Point> setPoints = new ArrayList<>();
        Map<String, Integer> point_id_map = new HashMap<>();

        try{
            inputStream = new FileInputStream(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while((str = bufferedReader.readLine()) != null)
            {
                if (Pattern.matches(regex, str)){
                    String[] line = str.split("\\*");
                    Point p1 = buildPoint(line[0]);
//                    p1.printPoint();
                    allPoints.add(p1);

                    Point p2 = buildPoint(line[1]);
//                    p2.printPoint();
                    allPoints.add(p2);
                    allPointsPair.add(new Point[]{p1, p2});
                }else {
                    throw new Exception("data: '" + str + "' is not correct format," +" Please modify it!");
                }
            }
//            System.out.println(allPointsPair.size());
//            System.out.println(allPoints.size());
            // 去掉重复点
            boolean isin;
            for (Point point:allPoints){
                isin = true;
                for(Point p: setPoints){
                    if (isEqual(point, p)){
                        isin = false;
                    }
                }
                if(isin){
                    setPoints.add(point);
                }
            }
//            System.out.println(setPoints.size());

            //在这边检测是不是有错误数据
            if (hasErrorPoint(setPoints)){
                throw new Exception("There are two points inconsistent!");
            }

            for (int i=0;i<setPoints.size();i++){
                point_id_map.put(setPoints.get(i).getLabel(), i);
            }
            allPoints.clear();

        }catch (FileNotFoundException e){
            System.out.println("找不到指定文件");
        }catch (IOException e){
            System.out.println("读取文件失败");
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 读取完毕，构造WeightedGraph
        print4J(point_id_map);

        //构造stairs，lifts，escalators
        List<String> stairs = new ArrayList<>();
        List<String> lifts = new ArrayList<>();
        List<String> escalators = new ArrayList<>();
        for (String obj:point_id_map.keySet()){
            if(obj.startsWith("S"))
                stairs.add(obj);
            else if (obj.startsWith("L"))
                lifts.add(obj);
            else if (obj.startsWith("E"))
                escalators.add(obj);
        }

        System.out.println(stairs.toString());
        System.out.println(lifts.toString());
        System.out.println(escalators.toString());


        WeightedGraph t = new WeightedGraph(point_id_map.size());
        for (Object obj:point_id_map.keySet()){
            t.setLabel(point_id_map.get(obj), obj);
        }

        for (int i = 0; i<allPointsPair.size();i++){
            int source = point_id_map.get(allPointsPair.get(i)[0].getLabel());
            int target = point_id_map.get(allPointsPair.get(i)[1].getLabel());
            Double weight = distanceBetweenTwoPoints(allPointsPair.get(i)[0], allPointsPair.get(i)[1]);

            t.addEdge(source, target, weight);
            // 无向图
            t.addEdge(target, source, weight);
        }
        return t;
    }


    private static Point buildPoint(String line){
        Point p = new Point();
        String[] split_lines = line.substring(line.indexOf("[")+1, line.indexOf("]")).split("#");
        p.setLabel(split_lines[0]);
        Double[] point = new Double[2];
        int i = 0;
        for (String s:split_lines[1].split(",")){
            point[i++] = Double.valueOf(s);
        }
        p.setPoint(point);
        return p;
    }

    /*给普通点重命名，比如一层C1： "C1" => "floor1_C1"*/
    private static Point buildPoint(String line,String floor_name){
        Point p = new Point();
        String[] split_lines = line.substring(line.indexOf("[")+1, line.indexOf("]")).split("#");
        if (split_lines[0].startsWith("C"))
            p.setLabel(floor_name+"_"+split_lines[0]);
        else
            p.setLabel(split_lines[0]);

        Double[] point = new Double[2];
        int i = 0;
        for (String s:split_lines[1].split(",")){
            point[i++] = Double.valueOf(s);
        }
        p.setPoint(point);
        p.setX(point[0]);
        p.setY(point[1]);

        return p;
    }

    /*判断两个Point是不是同一个点，名字相同，坐标相同*/
    public static boolean isEqual(Point p1, Point p2){
        return (p1.getLabel().equals(p2.getLabel())) && (p1.getPoint()[0].equals(p2.getPoint()[0]))
                && (p1.getPoint()[1].equals(p2.getPoint()[1]));
    }

    public static Double distanceBetweenTwoPoints(Point p1, Point p2){
        double _x = Math.abs(p1.getY() - p2.getX());
        double _y = Math.abs(p1.getY() - p2.getX());
        return Math.sqrt(_x*_x + _y*_y);
    }

    public static void print4J(Map map){
        Iterator iter = map.entrySet().iterator();
        System.out.print("{ ");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            System.out.print(key + ":" + val + ", ");
        }
        System.out.println("}");
    }
//    private static void print4J(Object obj){
//        if (obj instanceof Map){
//            Iterator iter = obj.entrySet().iterator();
//            System.out.print("{ ");
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                Object key = entry.getKey();
//                Object val = entry.getValue();
//                System.out.print(key + ":" + val + ", ");
//            }
//            System.out.println("}");
//        }
//    }

    //检测是不是有错误数据，比如lable相同，但是坐标不同,或者坐标相同但是label不同
    private static boolean hasErrorPoint( List<Point> setPoints){
        boolean hasError = false;
        for (Point anchorPoint:setPoints){
            for (Point testPoint:setPoints){
                if (testPoint.getLabel().equals(anchorPoint.getLabel()) &&
                        ((!testPoint.getPoint()[0].equals(anchorPoint.getPoint()[0])) ||
                                (!testPoint.getPoint()[1].equals(anchorPoint.getPoint()[1])))){
                    System.out.println("Error:");
                    anchorPoint.printPoint();
                    testPoint.printPoint();
                    hasError = true;
                }
                if(((testPoint.getPoint()[0].equals(anchorPoint.getPoint()[0])) &&
                        (testPoint.getPoint()[1].equals(anchorPoint.getPoint()[1]))) &&
                        (!testPoint.getLabel().equals(anchorPoint.getLabel()))){
                    System.out.println("Error:");
                    anchorPoint.printPoint();
                    testPoint.printPoint();
                    hasError = true;
                }

            }
        }
        return hasError;
    }

    /*每层返回一个元祖(String start,String end,ArrayList path, Double distance)*/
    public static ArrayList<Quartet<String, String, ArrayList<Object>,Double>> getResultWithinFloor(Floor floor){
        ArrayList<Quartet<String, String, ArrayList<Object>,Double>> result = new ArrayList<>();
        WeightedGraph graph = floor.getGraph();
        HashMap<String, Integer> point_id_map = floor.getPoint_id_map();
        for (String common_point:floor.getCommon_points()){
            Integer start_point_id = point_id_map.get(common_point);
            final int[] pred = Dijkstra.dijkstra(graph, start_point_id);
            //stairs
            for (String stair:floor.getStairs()){
                Quartet Q = Dijkstra.getResultTuple(graph, point_id_map, pred, common_point, stair);
                result.add(Q);
            }
            //lifts
            for (String lift:floor.getLifts()){
                Quartet Q = Dijkstra.getResultTuple(graph, point_id_map, pred, common_point, lift);
                result.add(Q);
            }
            //escalator
            for (String escalator:floor.getEscalators()){
                Quartet Q = Dijkstra.getResultTuple(graph, point_id_map,pred, common_point, escalator);
                result.add(Q);
            }
        }
        return result;
    }

    public static ArrayList<Quartet<String, String, ArrayList<Object>,Double>> getResultWithinFloor(Floor floor, String start_point_name){
        ArrayList<Quartet<String, String, ArrayList<Object>,Double>> result = new ArrayList<>();
        WeightedGraph graph = floor.getGraph();
        HashMap<String, Integer> point_id_map = floor.getPoint_id_map();
        Integer start_point_id = point_id_map.get(start_point_name);
        final int[] pred = Dijkstra.dijkstra(graph, start_point_id);
        //stairs
        for (String stair:floor.getStairs()){
            Quartet Q = Dijkstra.getResultTuple(graph, point_id_map, pred, start_point_name, stair);
            result.add(Q);
        }
        //lifts
        for (String lift:floor.getLifts()){
            Quartet Q = Dijkstra.getResultTuple(graph, point_id_map, pred, start_point_name, lift);
            result.add(Q);
        }
        //escalator
        for (String escalator:floor.getEscalators()){
            Quartet Q = Dijkstra.getResultTuple(graph, point_id_map, pred, start_point_name, escalator);
            result.add(Q);
        }
        return result;
    }

    public static Quartet<String, String, ArrayList<Object>,Double> getResultWithinFloor(Floor floor, String start_point, String end_point){
        WeightedGraph graph = floor.getGraph();
        HashMap<String, Integer> point_id_map = floor.getPoint_id_map();
        Integer start_point_id = point_id_map.get(start_point);
        final int[] pred = Dijkstra.dijkstra(graph, start_point_id);
        Quartet Q = Dijkstra.getResultTuple(graph, point_id_map, pred, start_point, end_point);

        return Q;
    }


    /*判断两层是不是同一个电梯/扶梯/楼梯,比如： S1a与 S2a是相同一个电梯*/
    public static boolean isSameOne(Quartet<String, String, ArrayList<Object>,Double> a, Quartet<String, String, ArrayList<Object>,Double> b){
        return (a.getValue1().charAt(0) == b.getValue1().charAt(0)) &&
                (a.getValue1().charAt(a.getValue1().length()-1) == b.getValue1().charAt(b.getValue1().length()-1));
    }

    public static boolean isSameOne(Point a, Point b){
        return (a.getLabel().charAt(0)==b.getLabel().charAt(0)&&
                (a.getLabel().charAt(a.getLabel().length()-1)==b.getLabel().charAt(b.getLabel().length()-1)));
    }

    /*判断某一层有没有同一个电梯/扶梯/楼梯*/
    public static boolean hasSameOne(ArrayList<Quartet<String, String, ArrayList<Object>,Double>> floor_result, Quartet<String, String, ArrayList<Object>,Double> point){
        boolean HASONE = false;
        for (Quartet<String, String, ArrayList<Object>,Double> q:floor_result){
            if (isSameOne(q, point))
                HASONE = true;
        }
        return HASONE;
    }

    /*判断这一层Floor，有没有跟start_point的同一个电梯/扶梯/楼梯
    *如果是初始点则直接返回false
    * 判断开头和最后一位："L1a"与 "L2a"就是相同的点。
    * */
    public static boolean hasSameOne(Floor floor, String start_point, boolean is_start_point){
        if (is_start_point){
            return false;
        }else {
            for (String point:floor.getPoint_id_map().keySet()){
                if ((point.charAt(0)==start_point.charAt(0))&&(point.charAt(point.length()-1)==start_point.charAt(start_point.length()-1))){
                    return true;
                }
            }
        }
        return false;
    }


    public static String getSameOne(Floor floor, String start_point){
        String SAMEONE = "";
        for (String point:floor.getAll_points()){
            if ((point.charAt(0)==start_point.charAt(0))&&(point.charAt(point.length()-1)==start_point.charAt(start_point.length()-1))){
//                System.out.println(point);
                return point;
            }
        }
        return SAMEONE;
    }

    /*
    *判断某一层是不是有电梯/扶梯/楼梯
    默认aim_way=S,L,E,分别代表stair，lift，escalator*/
    public static boolean hasSLE(Floor floor, String aim_way) {
        if (aim_way.equals("E")) {
            return (floor.getEscalators().size() == 0);
        }
        if (aim_way.equals("S")) {
            return (floor.getStairs().size() == 0);
        }
        return aim_way.equals("L") && (floor.getLifts().size() == 0);
    }

    /*获得所有Floor中的最短路径*/
    public static Double getShortestEdges(ArrayList<Floor> floors){
        Double result = Double.MAX_VALUE;
        for (Floor floor:floors) {
            Double[][] edges = floor.getGraph().edges;
            int vexs = floor.getGraph().vexs;
            for (int i=0;i<vexs;i++){
                for (int j=0;j<vexs;j++){
                    if ((result > edges[i][j])&&(edges[i][j]!=0))
                        result = edges[i][j];
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String filename = "src/data/test_points1";
        String filename2 = "C:\\Users\\student\\Desktop\\xiaoyu\\数据\\12.txt";
//        Map<String, Integer> point_id_map = readNodesFromFile(filename);

        WeightedGraph t = buildFromFile(filename);
//        t.print();
        if (t.isConnected()){
            final int[] pred = Dijkstra.dijkstra(t, 0);
            System.out.println(Arrays.toString(pred));
            for (int n = 0; n < t.vexs; n++) {
                Dijkstra.printPath(t, pred, 0, n);
            }
        }else {
            System.out.println(" The graph is not connected!");
        }

    }


}
