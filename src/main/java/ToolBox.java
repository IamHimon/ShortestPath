import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by รอ on 2017/7/21.
 */
public class ToolBox {

    public static void readExel(String path) {
        Workbook book = null;
        try {
            InputStream inputStream = new FileInputStream(path);
//            File f = new File(path);
            book = Workbook.getWorkbook(inputStream);

//            book = new HSSFWorkbook(inputStream);

            Sheet sheet = book.getSheet(0);

            int rsColumns = sheet.getColumns();
            int rsRows = sheet.getRows();

            for (int i = 0; i < rsRows; i++) {
                for (int j = 0; j < rsColumns; j++) {
                    Cell cell = sheet.getCell(j, i);
                    System.out.print(cell.getContents() + " ");
                }
                System.out.println();
            }


            book.close();

        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public static void main(String[] args) {
        System.out.println("hehe");
        String path = "src/main/data/5.2.xlsx";
        readExel(path);
//        File f = new File(path);
    }

}
