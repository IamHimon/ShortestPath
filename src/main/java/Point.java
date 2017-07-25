/**
 * Created by student on 2017/7/4.
 */
public class Point {
    public Double X;
    public Double Y;
    public String label = null;
    private Double[] point = null;


    public Point(Double x, Double y) {
        X = x;
        Y = y;
    }

    public Point(String label, Double[] point) {
        this.label = label;
        this.point = point;
        if (point.length==2) {
            this.X = point[0];
            this.Y = point[1];
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double[] getPoint() {
        return point;
    }

    public void setPoint(Double[] point) {
        if (point.length != 2) {
            System.out.println("Point must be a pair of values!");
            this.point = null;
        }else{
            this.point = point;
        }
    }

    public void printPoint(){
        System.out.println(this.getLabel() + ": [" + this.getPoint()[0] + "," + this.getPoint()[1] + "]");
    }

    public boolean isNull(){
        return (this.label==null)&&(this.point ==null);
    }

    public static void main(String[] args) {

    }
}