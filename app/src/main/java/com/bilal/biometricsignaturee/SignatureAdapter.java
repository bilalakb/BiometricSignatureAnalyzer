package com.bilal.biometricsignaturee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SignatureAdapter extends RecyclerView.Adapter<SignatureAdapter.SignatureViewHolder> {

    private Context context;
    private List<Signature> signatureList;
    private DatabaseHelper databaseHelper; // Veritabanı işlemleri için

    // Constructor: Context, signatureList ve DatabaseHelper'i alıyoruz
    public SignatureAdapter(Context context, List<Signature> signatureList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.signatureList = signatureList;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public SignatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // item_signature.xml layout dosyasını bağla
        View view = LayoutInflater.from(context).inflate(R.layout.item_signature, parent, false);
        return new SignatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SignatureViewHolder holder, int position) {
        Signature signature = signatureList.get(position);

        // Signature objesindeki verileri TextView'lere bağlama
        holder.textViewId.setText("ID: " + signature.getId());
        holder.touchDurationTextView.setText("Dokunma Süresi: " + signature.getTouchDuration() + " ms");
        holder.textViewPathLength.setText("İzlenen Yol Uzunluğu: " + signature.getPathLength());
        holder.textViewAvgVelocity.setText("Ortalama Hız: " + signature.getAvgVelocity());
        holder.textViewAcceleration.setText("Hızlanma: " + signature.getAcceleration());
        holder.textViewCentroid.setText("Ağırlık Merkezi: (X: " + signature.getCentroidX() + ", Y: " + signature.getCentroidY() + ")");
        holder.textViewCurveCount.setText("Kıvrım Sayısı: " + signature.getCurveCount());
        holder.textViewHorizontalMovements.setText("Yatay Hareket Sayısı: " + signature.getHorizontalMovements());
        holder.textViewVerticalMovements.setText("Dikey Hareket Sayısı: " + signature.getVerticalMovements());
        holder.textCoordinates.setText("Koordinatlar:\n" + signature.getPoints());
        holder.textTimestamps.setText("Zaman Damgaları:\n" + signature.getTimestamps());

        // Görseli ImageView'e yükleme
        String base64Image = signature.getImage(); // Görsel verisi alınır
        if (base64Image != null && !base64Image.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64Image);
            holder.imageViewSignature.setImageBitmap(bitmap);
        } else {
            holder.imageViewSignature.setImageResource(R.drawable.placeholder_image); // Varsayılan bir görsel göster
        }

        // "Sil" butonuna tıklama işlemi
        holder.btnDelete.setOnClickListener(view -> {
            showDeleteConfirmationDialog(signature, position);
        });
    }

    @Override
    public int getItemCount() {
        return signatureList.size();
    }

    // Base64 String'i Bitmap'e dönüştüren yardımcı yöntem
    private Bitmap decodeBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // Silme işlemi için onay penceresini gösteren metod
    private void showDeleteConfirmationDialog(Signature signature, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Silme İşlemi")
                .setMessage("Bu imzayı silmek istediğinizden emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    // Veritabanından silme işlemi
                    int signatureId = signature.getId();
                    boolean isDeletedFromDb = databaseHelper.deleteSignature(signatureId); // Veritabanından sil
                    if (isDeletedFromDb) {
                        signatureList.remove(position); // Listeden sil
                        notifyItemRemoved(position); // RecyclerView'i güncelle
                        Toast.makeText(context, "İmza başarıyla silindi.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "İmza silinirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hayır", null) // Silme işlemi iptal edilir
                .show();
    }

    // SignatureViewHolder sınıfı
    public class SignatureViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewSignature;
        private TextView textViewId, touchDurationTextView, textViewPathLength, textViewAvgVelocity,
                textViewAcceleration, textViewCentroid, textViewCurveCount,
                textViewHorizontalMovements, textViewVerticalMovements, textCoordinates, textTimestamps;
        private Button btnDelete;

        public SignatureViewHolder(View itemView) {
            super(itemView);
            // itemView üzerinden gerekli View elemanlarını buluyoruz
            imageViewSignature = itemView.findViewById(R.id.imageViewSignature);
            textViewId = itemView.findViewById(R.id.textViewId);
            touchDurationTextView = itemView.findViewById(R.id.touchDurationTextView);
            textViewPathLength = itemView.findViewById(R.id.textViewPathLength);
            textViewAvgVelocity = itemView.findViewById(R.id.textViewAvgVelocity);
            textViewAcceleration = itemView.findViewById(R.id.textViewAcceleration);
            textViewCentroid = itemView.findViewById(R.id.textViewcentroidX);
            textViewCurveCount = itemView.findViewById(R.id.textViewcurveCount);
            textViewHorizontalMovements = itemView.findViewById(R.id.textViewhorizontalMovements);
            textViewVerticalMovements = itemView.findViewById(R.id.textverticalMovements);
            textCoordinates = itemView.findViewById(R.id.textCoordinates);
            textTimestamps = itemView.findViewById(R.id.textTimestamps);
            btnDelete = itemView.findViewById(R.id.btnDelete); // "Sil" butonunu bul
        }
    }
}
