package com.bilal.biometricsignaturee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SignatureView signatureView;
    private Button btnSilme, btnAnalyze, btnReportPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // İmza Görünümünü (SignatureView) ve Butonları tanımlıyoruz
        signatureView = new SignatureView(this);
        ConstraintLayout layout = findViewById(R.id.signature_pad);
        layout.addView(signatureView);

        btnSilme = findViewById(R.id.btn_silme);
        btnAnalyze = findViewById(R.id.btn_analyze);
        btnReportPage = findViewById(R.id.btnReportPage);

        // Temizleme Butonuna Tıklanınca çizim temizlenir
        btnSilme.setOnClickListener(v -> signatureView.clear());

        // Analiz Butonuna Tıklanınca imza verileri ve Bitmap aktarılır
        btnAnalyze.setOnClickListener(v -> {
            ArrayList<Point> points = new ArrayList<>(signatureView.getPoints());
            ArrayList<Long> timestamps = new ArrayList<>(signatureView.getTimestamps());

            // İmzayı Bitmap olarak al
            Bitmap signatureBitmap = signatureView.getSignatureBitmap();

            // Bitmap'i ByteArray'e çevir
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapBytes = stream.toByteArray();

            // Intent ile AnalyzeActivity'ye gönder
            Intent intent = new Intent(MainActivity.this, AnalyzeActivity.class);
            intent.putExtra("points", points);
            intent.putExtra("timestamps", timestamps);
            intent.putExtra("signatureBitmap", bitmapBytes); // Bitmap verisi eklendi
            startActivity(intent);
        });

        btnReportPage.setOnClickListener(v -> {
            // Rapor Sayfası Activity'sini başlatma
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });
    }

    public class SignatureView extends View {

        private Paint paint;
        private Path path;
        private List<Point> points;
        private List<Long> timestamps; // Zaman damgalarını kaydetmek için

        public SignatureView(Context context) {
            super(context);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(8f);
            paint.setColor(Color.BLACK);
            path = new Path();
            points = new ArrayList<>();
            timestamps = new ArrayList<>();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            long timestamp = System.currentTimeMillis();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    points.add(new Point((int) x, (int) y));
                    timestamps.add(timestamp);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    points.add(new Point((int) x, (int) y));
                    timestamps.add(timestamp);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }

        public void clear() {
            path.reset();
            points.clear();
            timestamps.clear();
            invalidate();
        }

        // Getter metodları
        public List<Point> getPoints() {
            return points;
        }

        public List<Long> getTimestamps() {
            return timestamps;
        }

        // İmza Görünümünü Bitmap olarak döndüren metod
        public Bitmap getSignatureBitmap() {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE); // Arka plan rengi beyaz olarak ayarlanıyor
            draw(canvas);
            return bitmap;
        }
    }
}
