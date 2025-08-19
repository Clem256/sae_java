/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae.java.modele;

/**
 *
 * @author Mathieu Corne
 */
public class Point {
    private double x;
    private double y;

    public Point(double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "("+x+";"+y+")";
    }
    
    public static boolean estEgal(Point A, Point B) {
        return (A.getX() == B.getX() && A.getY() == B.getY());
    }
}
