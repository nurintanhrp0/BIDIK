package com.bidik.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {

    TextView txtLogin, txtDaftar, txtBayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        txtLogin = findViewById(R.id.login);
        txtDaftar = findViewById(R.id.daftar);
        txtBayar = findViewById(R.id.pembyaran);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        txtDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registrasi.class);
                startActivity(intent);
            }
        });

        txtBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KonfirmasiPembayaran.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        //keluar dari aplikasi
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        System.exit(0);
    }
}
