package com.bidik.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bidik.app.ui.home.HomeFragment;
import com.bidik.app.ui.keanggotaan.Keanggotaan;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class KonfirmasiPembayaran extends AppCompatActivity {

    ImageView btnClose;
    Button btnKonfirmasi;
    TextView txtHarga, txtNorek, txtMetode, txtNama, txtTitle;
    String harga, norek, atasNama, metode, wa;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String defaultUrl, dataUrl, urlGetData;
    DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    Integer dari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pembayaran);

        defaultUrl = ((Bidik) getApplication()).getUrl();
        dataUrl = ((Bidik) getApplication()).getUrlData();
        urlGetData = defaultUrl + "getpembayaran.html";

        Intent intent = getIntent();
        dari = intent.getIntExtra("dari", 0);

        btnClose = findViewById(R.id.btnClose);
        btnKonfirmasi = findViewById(R.id.btnKonnfirmasi);
        txtHarga = findViewById(R.id.txtHarga);
        txtNorek = findViewById(R.id.norek);
        txtMetode = findViewById(R.id.metodepembayaran);
        txtNama = findViewById(R.id.atasNama);
        txtTitle = findViewById(R.id.txtsubTitle);

        if (dari != 0){
            txtTitle.setText("Untuk memperpanjang keanggotan anda, silahkan lakukan pembayaran ke nomor rekening dibawah ini :");
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String message = "Konfirmasi Pembayaran ";

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(
                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                                        wa, message))));
            }
        });

        new GetData().execute();
    }

    private class GetData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", ""));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetData, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(KonfirmasiPembayaran.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if ((pDialog != null) && (pDialog.isShowing()))
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                    }else {
                        harga = "Rp " + decimalFormat.format(result.getInt("harga"));
                        norek = result.getString("norek");
                        atasNama = result.getString("atasnama");
                        metode = result.getString("metode");
                        wa = result.getString("wa");

                        txtHarga.setText(harga);
                        txtNorek.setText(norek);
                        txtNama.setText(atasNama);
                        txtMetode.setText(metode);
                    }
                }else {
                    //Toast.makeText(getApplicationContext(), "Ups! Menu yang kamu pilih belum tersedia di outlet ini.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void shareNorek(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(norek);
        Toast.makeText(this, "Disalin ke clipboard", Toast.LENGTH_SHORT).show();

    }

    public void salinHarga(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(harga);
        Toast.makeText(this, "Disalin ke clipboard", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        if (dari == 0){
            super.onBackPressed();
        }else if (dari ==1){
            super.onBackPressed();
        }else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
