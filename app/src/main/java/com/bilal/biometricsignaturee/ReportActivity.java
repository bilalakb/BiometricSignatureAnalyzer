package com.bilal.biometricsignaturee;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SignatureAdapter signatureAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report); // Layout dosyasını ayarladık

        // DatabaseHelper'ı başlatıyoruz
        databaseHelper = new DatabaseHelper(this);

        // RecyclerView'i buluyoruz
        recyclerView = findViewById(R.id.recyclerViewSignatures);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Veritabanından tüm imzaları alıyoruz
        List<Signature> signatureList = databaseHelper.getAllSignatures();

        // Adapter'ı oluşturup RecyclerView'e bağlıyoruz
        signatureAdapter = new SignatureAdapter(this, signatureList, databaseHelper); // DatabaseHelper ekleniyor
        recyclerView.setAdapter(signatureAdapter);
    }
}
