package com.bilal.biometricsignaturee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SignatureDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "signatures.db";
    private static final int DATABASE_VERSION = 2; // Versiyonu artırdık.

    // Tablonun ve sütunlarının adlarını tanımlıyoruz
    public static final String TABLE_SIGNATURES = "signatures";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_TIMESTAMPS = "timestamps";
    public static final String COLUMN_TOUCH_DURATION = "touchDuration";
    public static final String COLUMN_PATH_LENGTH = "pathLength";
    public static final String COLUMN_AVG_VELOCITY = "avgVelocity";
    public static final String COLUMN_ACCELERATION = "acceleration";
    public static final String COLUMN_CENTROID_X = "centroidX";
    public static final String COLUMN_CENTROID_Y = "centroidY";
    public static final String COLUMN_CLOSEST_POINT = "closestPoint";
    public static final String COLUMN_FARTHEST_POINT = "farthestPoint";
    public static final String COLUMN_CURVE_COUNT = "curveCount";
    public static final String COLUMN_HORIZONTAL_MOVEMENTS = "horizontalMovements";
    public static final String COLUMN_VERTICAL_MOVEMENTS = "verticalMovements";
    public static final String COLUMN_IMAGE = "image"; // Yeni sütun

    // Veritabanı oluşturucu
    public SignatureDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SIGNATURES_TABLE = "CREATE TABLE " + TABLE_SIGNATURES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POINTS + " TEXT,"
                + COLUMN_TIMESTAMPS + " TEXT,"
                + COLUMN_TOUCH_DURATION + " INTEGER,"
                + COLUMN_PATH_LENGTH + " REAL,"
                + COLUMN_AVG_VELOCITY + " REAL,"
                + COLUMN_ACCELERATION + " REAL,"
                + COLUMN_CENTROID_X + " INTEGER,"
                + COLUMN_CENTROID_Y + " INTEGER,"
                + COLUMN_CLOSEST_POINT + " TEXT,"
                + COLUMN_FARTHEST_POINT + " TEXT,"
                + COLUMN_CURVE_COUNT + " INTEGER,"
                + COLUMN_HORIZONTAL_MOVEMENTS + " INTEGER,"
                + COLUMN_VERTICAL_MOVEMENTS + " INTEGER,"
                + COLUMN_IMAGE + " TEXT" // Görsel Base64 formatında saklanacak
                + ")";
        db.execSQL(CREATE_SIGNATURES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_SIGNATURES + " ADD COLUMN " + COLUMN_IMAGE + " TEXT");
        }
    }
}
