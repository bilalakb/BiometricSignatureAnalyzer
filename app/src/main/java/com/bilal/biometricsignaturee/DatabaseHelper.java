package com.bilal.biometricsignaturee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "signature_data.db";
    private static final int DATABASE_VERSION = 2; // Versiyon artırıldı

    // Tablo Adı ve Kolonları
    private static final String TABLE_NAME = "signature_features";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_POINTS = "points";
    private static final String COLUMN_TIMESTAMPS = "timestamps";
    private static final String COLUMN_TOUCH_DURATION = "touchDuration";
    private static final String COLUMN_PATH_LENGTH = "path_length";
    private static final String COLUMN_AVG_VELOCITY = "avg_velocity";
    private static final String COLUMN_ACCELERATION = "acceleration";
    private static final String COLUMN_CENTROID_X = "centroid_x";
    private static final String COLUMN_CENTROID_Y = "centroid_y";
    private static final String COLUMN_CURVE_COUNT = "curve_count";
    private static final String COLUMN_HORIZONTAL_MOVEMENTS = "horizontal_movements";
    private static final String COLUMN_VERTICAL_MOVEMENTS = "vertical_movements";
    private static final String COLUMN_IMAGE = "image"; // Görsel kolon

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_POINTS + " TEXT, " +
                COLUMN_TIMESTAMPS + " TEXT, " +
                COLUMN_TOUCH_DURATION + " INTEGER, " +
                COLUMN_PATH_LENGTH + " REAL, " +
                COLUMN_AVG_VELOCITY + " REAL, " +
                COLUMN_ACCELERATION + " REAL, " +
                COLUMN_CENTROID_X + " INTEGER, " +
                COLUMN_CENTROID_Y + " INTEGER, " +
                COLUMN_CURVE_COUNT + " INTEGER, " +
                COLUMN_HORIZONTAL_MOVEMENTS + " INTEGER, " +
                COLUMN_VERTICAL_MOVEMENTS + " INTEGER, " +
                COLUMN_IMAGE + " TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_IMAGE + " TEXT");
        }
    }

    // Yeni imza verisini eklemek için metod
    public boolean addSignatureData(
            ArrayList<Point> points,
            ArrayList<Long> timestamps,
            int touchDuration,
            float pathLength,
            float avgVelocity,
            float acceleration,
            int centroidX,
            int centroidY,
            int curveCount,
            int horizontalMovements,
            int verticalMovements,
            String image) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_POINTS, pointsToString(points));
        contentValues.put(COLUMN_TIMESTAMPS, timestampsToString(timestamps));
        contentValues.put(COLUMN_TOUCH_DURATION, touchDuration);
        contentValues.put(COLUMN_PATH_LENGTH, pathLength);
        contentValues.put(COLUMN_AVG_VELOCITY, avgVelocity);
        contentValues.put(COLUMN_ACCELERATION, acceleration);
        contentValues.put(COLUMN_CENTROID_X, centroidX);
        contentValues.put(COLUMN_CENTROID_Y, centroidY);
        contentValues.put(COLUMN_CURVE_COUNT, curveCount);
        contentValues.put(COLUMN_HORIZONTAL_MOVEMENTS, horizontalMovements);
        contentValues.put(COLUMN_VERTICAL_MOVEMENTS, verticalMovements);
        contentValues.put(COLUMN_IMAGE, image);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    // Points listesini String formatına çevir
    private String pointsToString(ArrayList<Point> points) {
        StringBuilder sb = new StringBuilder();
        for (Point point : points) {
            sb.append(point.x).append(",").append(point.y).append(";");
        }
        return sb.toString();
    }

    // Timestamps listesini String formatına çevir
    private String timestampsToString(ArrayList<Long> timestamps) {
        StringBuilder sb = new StringBuilder();
        for (Long timestamp : timestamps) {
            sb.append(timestamp).append(";");
        }
        return sb.toString();
    }

    // Veritabanındaki tüm imzaları almak için metod
    public List<Signature> getAllSignatures() {
        List<Signature> signatures = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String points = cursor.getString(cursor.getColumnIndex(COLUMN_POINTS));
                String timestamps = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMPS));
                float pathLength = cursor.getFloat(cursor.getColumnIndex(COLUMN_PATH_LENGTH));
                int touchDuration = cursor.getInt(cursor.getColumnIndex(COLUMN_TOUCH_DURATION));
                float avgVelocity = cursor.getFloat(cursor.getColumnIndex(COLUMN_AVG_VELOCITY));
                float acceleration = cursor.getFloat(cursor.getColumnIndex(COLUMN_ACCELERATION));
                int centroidX = cursor.getInt(cursor.getColumnIndex(COLUMN_CENTROID_X));
                int centroidY = cursor.getInt(cursor.getColumnIndex(COLUMN_CENTROID_Y));
                int curveCount = cursor.getInt(cursor.getColumnIndex(COLUMN_CURVE_COUNT));
                int horizontalMovements = cursor.getInt(cursor.getColumnIndex(COLUMN_HORIZONTAL_MOVEMENTS));
                int verticalMovements = cursor.getInt(cursor.getColumnIndex(COLUMN_VERTICAL_MOVEMENTS));
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE));

                Signature signature = new Signature(id, points, timestamps, touchDuration, pathLength, avgVelocity, acceleration,
                        centroidX, centroidY, curveCount, horizontalMovements, verticalMovements, image);
                signatures.add(signature);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return signatures;
    }

    // Veritabanındaki bir imzayı silmek için metod
    public boolean deleteSignature(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; // Silme işlemi başarılıysa true döner
    }
}
