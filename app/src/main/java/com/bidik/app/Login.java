package com.bidik.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.bidik.app.modal.modal_kendaraan;

public class Login extends AppCompatActivity {

    ImageView btnClose;
    Button btnLogin;
    EditText inpNohp, inpPwd;
    String noHp, pwd;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String urlLogin, defaultUrl, dataUrl;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List<modal_kendaraan> list;
    Intent mServiceIntent;
    private YourService mYourService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("bidik", 0);
        editor = sharedPreferences.edit();

        defaultUrl = ((Bidik) getApplication()).getUrl();
        dataUrl = ((Bidik) getApplication()).getUrlData();
        urlLogin = defaultUrl + "dologin.html";

        btnClose = findViewById(R.id.btnClose);
        btnLogin = findViewById(R.id.btnLogin);
        inpNohp = findViewById(R.id.inpNohp);
        inpPwd = findViewById(R.id.inpPwd);

        list = new ArrayList<>();

        mYourService = new YourService();
        mServiceIntent = new Intent(getApplicationContext(), mYourService.getClass());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noHp = inpNohp.getText().toString();
                pwd = inpPwd.getText().toString();
                new DoLogin().execute();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Welcome.class);
        startActivity(intent);
    }

    private class DoLogin extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("pwd", pwd));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlLogin, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
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

                            JSONObject data = new JSONObject();
                            JSONArray daftarlcl = new JSONArray();
                        if (result.has("data")) {
                            JSONArray datadb = new JSONArray(result.getString("data"));
                            if (datadb.length() > 0) {
                                for (int j = 0; j < datadb.length(); j++) {
                                    final JSONObject daftar = datadb.getJSONObject(j);
                                    JSONObject object = new JSONObject();

                                    object.put("id_leasing", daftar.getString("id_leasing"));
                                    object.put("tanggal", daftar.getString("tanggal"));
                                    object.put("jenis", daftar.getString("jenis"));
                                    object.put("customer", daftar.getString("customer"));
                                    object.put("no_polisi", daftar.getString("no_polisi"));
                                    object.put("model_kendaraan", daftar.getString("model_kendaraan"));
                                    object.put("warna_kendaraan", daftar.getString("warna_kendaraan"));
                                    object.put("no_rangka", daftar.getString("no_rangka"));
                                    object.put("no_mesin", daftar.getString("no_mesin"));
                                    object.put("sisa_tagihan", daftar.getString("sisa_tagihan"));
                                    object.put("jatuh_tempo", daftar.getString("jatuh_tempo"));
                                    object.put("over_due", daftar.getString("over_due"));
                                    object.put("status", daftar.getString("status"));
                                    object.put("lock", daftar.getString("lock"));
                                    object.put("catatan", "null");
                                    object.put("terhapus", "0");
                                    object.put("simpan", "0");

                                    daftarlcl.put(object);

                                }

                            }
                        }
                            data.put("data", daftarlcl);
                            savetolocal(data, "data_kendaraan");


                         data = new JSONObject();
                         daftarlcl = new JSONArray();
                        if (result.has("leasing")) {
                            JSONArray db = new JSONArray(result.getString("leasing"));
                            if (db.length() > 0) {
                                for (int j = 0; j < db.length(); j++) {
                                    final JSONObject daftar = db.getJSONObject(j);
                                    JSONObject object = new JSONObject();

                                    object.put("id", daftar.getString("id"));
                                    object.put("nama", daftar.getString("nama"));
                                    object.put("alamat", daftar.getString("alamat"));
                                    object.put("kota", daftar.getString("kota"));
                                    object.put("contact_person", daftar.getString("contact_person"));
                                    object.put("no_hp", daftar.getString("no_hp"));
                                    object.put("status", daftar.getString("status"));

                                    daftarlcl.put(object);

                                }
                                data.put("data", daftarlcl);
                                savetolocal(data, "data_leasing");
                            }
                        }

                        Toast.makeText(Login.this, result.getString("pesan"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        editor.putString("noHp", result.getString("noHp"));
                        editor.putString("exp", result.getString("exp"));
                        editor.putString("kategori", result.getString("kategori"));
                        editor.putString("nama", result.getString("nama"));
                        editor.putInt("id", result.getInt("id"));
                        editor.putInt("berhenti", 0);
                        editor.putString("expe", result.getString("expe"));
                        editor.apply();

                       stopService(mServiceIntent);
                       startService(mServiceIntent);

                        startActivity(intent);
                    }
                }else {
                    //Toast.makeText(getApplicationContext(), "Ups! Menu yang kamu pilih belum tersedia di outlet ini.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void savetolocal(JSONObject data, String nama) {
        try {
            String isi = data.toString();

            FileOutputStream fOut = null;
            try  {
                fOut = openFileOutput(nama + ".json", MODE_PRIVATE);
                fOut.write(isi.getBytes());
                fOut.close();
                //display file saved message
                //Log.d("ssaved", "File saved successfully on " + getFilesDir());
                //Toast.makeText(getBaseContext(), "File saved successfully on " + getFilesDir(),
               //         Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
