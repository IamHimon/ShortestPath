package DAO;

/**
 * Created by student on 2017/7/4.
 */
public class Point {
    public Double X;
    public Double Y;
    public String label = null;
    private Double[] point = null;

    public Point() {
    }

    public Point(Double x, Double y) {
        this.X = x;
        this.Y = y;
    }

    public Point(String label, Double[] point) {
        this.label = label;
        this.point = point;
        if (point.length==2) {
            this.X = point[0];
            this.Y = point[1];
        }
    }

    public Double getX() {
        return X;
    }

    public void setX(Double x) {
        X = x;
    }

    public Double getY() {
        return Y;
    }

    public void setY(Double y) {
        Y = y;
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
            System.out.println("DAO.Point must be a pair of values!");
            this.point = null;
        }else{
            this.point = point;
        }
    }

    public void printPoint(){
        System.out.println(this.label + ": [" + this.X + "," + this.Y + "]");
    }

    public boolean isNull(){
        return (this.label==null)&&(this.point ==null);
    }

    public static void main(String[] args) {

    }
}
