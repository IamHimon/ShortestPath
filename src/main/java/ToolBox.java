import com.csvreader.CsvReader;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by 猛 on 2017/7/21.
 */
public class ToolBox {

    public static void readCSV(String path) {
        Double X, Y;
        String Line;
        String label;
        ArrayList<Point> allPoints = new ArrayList<Point>();
         try {
             CsvReader csvReader = new CsvReader(path);

             csvReader.readHeaders();
             while (csvReader.readRecord()) {
                 if (!csvReader.get("X").equals("") &&! csvReader.get("Y").equals("") &&
                         !csvReader.get("ORIG_FID").equals("")) {
                     X = Double.parseDouble(csvReader.get("X"));
                     Y = Double.parseDouble(csvReader.get("Y"));
                     Line = csvReader.get("ORIG_FID");
                     if(csvReader.get("label").equals("")){
                         label = "C";
                     }else {
                         label = csvReader.get("label");
                     }
                     System.out.println(X);
                     System.out.println(Y);
                     System.out.println(Line);
                     System.out.println(label);
                     System.out.println();
                     Point p = new Point(label, new Double[]{X, Y});
                     allPoints.add(p);
                 }

             }
         }catch (IOException e){
             e.printStackTrace();
         }


    }
/*获取所有不重复的点*/
    public static ArrayList<Point> getAllPoint(String path){
        boolean isDup = false;
        Double X, Y;
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

        //给普通点命名，
        int commonPointCount = 0;
        for (Point point:allPoints){
            if (point.label==null){
                commonPointCount += 1;
                point.setLabel("floor5_"+"C"+String.valueOf(commonPointCount));
            }
        }

        return allPoints;
    }

    public static void main(String[] args) {
        String path = "src/main/data/5c.csv";
        ArrayList<Point> allPoints = getAllPoint(path);

        /*普通点的个数*/


//        readCSV(path);
//        File f = new File(path);
    }

}
