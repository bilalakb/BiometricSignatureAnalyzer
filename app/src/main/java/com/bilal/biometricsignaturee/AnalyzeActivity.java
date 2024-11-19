package com.bilal.biometricsignaturee;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AnalyzeActivity extends AppCompatActivity {

    private TextView dynamicFeaturesText;
    private TextView dynamicFeatureValues;
    private ImageView signatureImageView; // İmza görüntüsünü göstermek için ImageView

    private ArrayList<Point> points;
    private ArrayList<Long> timestamps;

    // DatabaseHelper nesnesi
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        // Görsel bileşenleri tanımla
        dynamicFeaturesText = findViewById(R.id.dynamicFeaturesText);
        dynamicFeatureValues = findViewById(R.id.dynamicFeatureValues);
        signatureImageView = findViewById(R.id.signatureImageView); // XML'deki ImageView'i tanımla

        if (dynamicFeaturesText == null || dynamicFeatureValues == null || signatureImageView == null) {
            Log.e("AnalyzeActivity", "TextView veya ImageView bulunamadı!");
            Toast.makeText(this, "Bileşen bulunamadı, lütfen ID'leri kontrol edin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // MainActivity'den gelen verileri al
        points = (ArrayList<Point>) getIntent().getSerializableExtra("points");
        timestamps = (ArrayList<Long>) getIntent().getSerializableExtra("timestamps");
        byte[] bitmapBytes = getIntent().getByteArrayExtra("signatureBitmap"); // Bitmap byte dizisini al

        // Bitmap'i işle ve ImageView'de göster
        if (bitmapBytes != null) {
            Bitmap signatureBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            signatureImageView.setImageBitmap(signatureBitmap); // Bitmap'i ImageView'de göster
        } else {
            Log.e("AnalyzeActivity", "İmza Bitmap'i alınamadı.");
        }

        if (points == null || timestamps == null || points.isEmpty() || timestamps.isEmpty()) {
            Log.e("AnalyzeActivity", "Geçersiz veri: points veya timestamps eksik veya boş.");
            Toast.makeText(this, "Veri alınamadı, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // DatabaseHelper başlat
        databaseHelper = new DatabaseHelper(this);

        // Dinamik özellikleri göster
        showDynamicFeatures();
    }

    private void saveSignatureData(int touchDuration, float pathLength, float avgVelocity, float acceleration,
                                   Point centroid, int curveCount, int horizontalMovements, int verticalMovements, Bitmap signatureBitmap) {

        // Bitmap'i Base64 formatına çevir
        String image = null;
        if (signatureBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            image = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
        }

        // Veritabanına kaydetme
        boolean isInserted = databaseHelper.addSignatureData(
                points,
                timestamps,
                touchDuration,
                pathLength,
                avgVelocity,
                acceleration,
                centroid.x,
                centroid.y,
                curveCount,
                horizontalMovements,
                verticalMovements,
                image  // Görsel verisi burada gönderiliyor
        );

        if (isInserted) {
            Toast.makeText(this, "İmza veritabanına başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "İmza kaydedilirken hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    // Dinamik özellikleri hesaplayıp gösteren fonksiyon
    private void showDynamicFeatures() {
        StringBuilder features = new StringBuilder("Dinamik Özellikler:\n\n");

        // 1. Dokunma Süresi
        long touchDuration = timestamps.get(timestamps.size() - 1) - timestamps.get(0);
        features.append("🔹 Dokunma Süresi: ").append(touchDuration).append(" ms\n\n");

        // 2. İzlenen Yol Uzunluğu ve Ortalama Hız
        float pathLength = 0f, totalVelocity = 0f;
        for (int i = 1; i < points.size(); i++) {
            float deltaX = points.get(i).x - points.get(i - 1).x;
            float deltaY = points.get(i).y - points.get(i - 1).y;
            pathLength += Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            long timeDelta = timestamps.get(i) - timestamps.get(i - 1);
            if (timeDelta > 0) {
                float velocity = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY) / timeDelta;
                totalVelocity += velocity;
            }
        }

        features.append("🔹 İzlenen Yol Uzunluğu: ").append(String.format("%.2f", pathLength)).append(" px\n\n");
        float avgVelocity = totalVelocity / (points.size() - 1);
        features.append("🔹 Ortalama Hız: ").append(String.format("%.6f", avgVelocity)).append(" px/ms\n\n");

        // 3. Hızlanma
        float acceleration = 0f;
        if (timestamps.size() >= 2) {
            long totalTime = timestamps.get(timestamps.size() - 1) - timestamps.get(0);
            acceleration = avgVelocity / totalTime;
            features.append("🔹 Hızlanma: ").append(String.format("%.6f", acceleration)).append(" px/ms²\n\n");
        }

        // 4. Ağırlık Merkezi
        Point centroid = calculateCentroid(points);
        features.append("🔹 Ağırlık Merkezi: (X: ").append(centroid.x).append(", Y: ").append(centroid.y).append(")\n\n");

        // 5. Kıvrım Sayısı
        int curveCount = calculateCurveCount(points);
        features.append("🔹 Kıvrım Sayısı: ").append(curveCount).append("\n\n");

        // 6. Yatay ve Dikey Hareket Sayısı
        int[] movementCounts = calculateHorizontalVerticalMovements(points);
        int horizontalMovements = movementCounts[0];
        int verticalMovements = movementCounts[1];
        features.append("🔹 Yatay Hareket Sayısı: ").append(horizontalMovements).append("\n\n");
        features.append("🔹 Dikey Hareket Sayısı: ").append(verticalMovements).append("\n\n");

        // 7. X, Y Koordinatları ve Zaman Damgaları
        features.append("🔹 Koordinatlar ve Zaman Damgaları:\n");
        for (int i = 0; i < points.size(); i++) {
            features.append("  - Koordinat (X: ").append(points.get(i).x).append(", Y: ").append(points.get(i).y)
                    .append(") Zaman: ").append(timestamps.get(i)).append(" ms\n");
        }

        // Dinamik Özellikleri Kaydet
        byte[] bitmapBytes = getIntent().getByteArrayExtra("signatureBitmap");
        Bitmap signatureBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        saveSignatureData((int) touchDuration, pathLength, avgVelocity, acceleration, centroid, curveCount, horizontalMovements, verticalMovements, signatureBitmap);

        // Dinamik Özellikleri Göster
        dynamicFeatureValues.setText(features.toString());
    }

    // Ağırlık Merkezi Hesaplama
    private Point calculateCentroid(ArrayList<Point> points) {
        int sumX = 0, sumY = 0;
        for (Point point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        return new Point(sumX / points.size(), sumY / points.size());
    }

    // Kıvrım Sayısı Hesaplama
    private int calculateCurveCount(ArrayList<Point> points) {
        int curveCount = 0;
        for (int i = 1; i < points.size() - 1; i++) {
            int dx1 = points.get(i).x - points.get(i - 1).x;
            int dy1 = points.get(i).y - points.get(i - 1).y;
            int dx2 = points.get(i + 1).x - points.get(i).x;
            int dy2 = points.get(i + 1).y - points.get(i).y;
            if (dx1 * dy2 != dy1 * dx2) { // Eğim değişimi varsa kıvrım sayısını artır
                curveCount++;
            }
        }
        return curveCount;
    }

    // Yatay ve Dikey Hareket Sayısı Hesaplama
    private int[] calculateHorizontalVerticalMovements(ArrayList<Point> points) {
        int horizontalMovements = 0;
        int verticalMovements = 0;

        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).x != points.get(i - 1).x) {
                horizontalMovements++;
            }
            if (points.get(i).y != points.get(i - 1).y) {
                verticalMovements++;
            }
        }

        return new int[]{horizontalMovements, verticalMovements};
    }
}
