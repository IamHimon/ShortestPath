package DAO;

/**
 * Created by student on 2017/7/4.
 * 表示一点的类，属性有：x坐标，y坐标，点的名称，和坐标对
 * @X x坐标
 * @Y y坐标
 * @label 点的名称
 * @point 坐标对:{X, Y}
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

}
