package com.bilal.biometricsignaturee;

import android.graphics.Point;

import java.util.ArrayList;

public class Signature {

    private int id;
    private ArrayList<Point> points;
    private ArrayList<Long> timestamps;
    private int touchDuration;
    private float pathLength;
    private float avgVelocity;
    private float acceleration;
    private int centroidX;
    private int centroidY;
    private int curveCount;
    private int horizontalMovements;
    private int verticalMovements;
    private String image; // Yeni özellik: Görsel verisi Base64 formatında saklanacak

    public Signature(int id, String points, String timestamps, int touchDuration, float pathLength, float avgVelocity, float acceleration,
                     int centroidX, int centroidY, int curveCount, int horizontalMovements, int verticalMovements, String image) {
        this.id = id;
        this.points = stringToPoints(points);
        this.timestamps = stringToTimestamps(timestamps);
        this.touchDuration = touchDuration;
        this.pathLength = pathLength;
        this.avgVelocity = avgVelocity;
        this.acceleration = acceleration;
        this.centroidX = centroidX;
        this.centroidY = centroidY;
        this.curveCount = curveCount;
        this.horizontalMovements = horizontalMovements;
        this.verticalMovements = verticalMovements;
        this.image = image; // Yeni özellik atanıyor
    }

    private ArrayList<Point> stringToPoints(String pointsString) {
        ArrayList<Point> pointList = new ArrayList<>();
        String[] pointsArray = pointsString.split(";");
        for (String pointStr : pointsArray) {
            String[] coordinates = pointStr.split(",");
            if (coordinates.length == 2) {
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                pointList.add(new Point(x, y));
            }
        }
        return pointList;
    }

    private ArrayList<Long> stringToTimestamps(String timestampsString) {
        ArrayList<Long> timestampList = new ArrayList<>();
        String[] timestampsArray = timestampsString.split(";");
        for (String timestamp : timestampsArray) {
            if (!timestamp.isEmpty()) {
                timestampList.add(Long.parseLong(timestamp));
            }
        }
        return timestampList;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public int getTouchDuration() {
        return touchDuration;
    }

    public ArrayList<Long> getTimestamps() {
        return timestamps;
    }

    public float getPathLength() {
        return pathLength;
    }

    public float getAvgVelocity() {
        return avgVelocity;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public int getCentroidX() {
        return centroidX;
    }

    public int getCentroidY() {
        return centroidY;
    }

    public int getCurveCount() {
        return curveCount;
    }

    public int getHorizontalMovements() {
        return horizontalMovements;
    }

    public int getVerticalMovements() {
        return verticalMovements;
    }

    public String getImage() {
        return image; // Görsel verisi döndürülüyor
    }

    public void setImage(String image) {
        this.image = image; // Görsel verisi ayarlanıyor
    }
}
