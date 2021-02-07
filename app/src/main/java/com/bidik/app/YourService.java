package com.bidik.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bidik.app.modal.modal_datasaya;
import com.bidik.app.modal.modal_kendaraan;
import com.bidik.app.modal.modal_leasing;
import com.bidik.app.modal.modal_lock;
import com.bidik.app.ui.perbaharui_data.PerbaharuiData;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class YourService extends Service {
    public int counter=0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String perbaharui;
    String myFormat = "HH:mm", jam;
    DateFormat sdf;
    Calendar myCalendar;
    private YourService mYourService;
    Intent mServiceIntent;
    Integer berhenti, id_member;
    String[] responce, nama;
    List<modal_kendaraan> list1;
    List<modal_leasing> list2;
    List<modal_lock> list3;
    List<modal_datasaya> list4;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String defaultUrl, dataUrl, urlGetData, kategori;
    JSONObject data1, data2, data3;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("bidik", 0);
        editor = sharedPreferences.edit();
        perbaharui = sharedPreferences.getString("perbaharui", "00:00");
        berhenti = sharedPreferences.getInt("berhenti", 0);
        id_member = sharedPreferences.getInt("id", 0);
        kategori = sharedPreferences.getString("kategori", "");

        defaultUrl = ((Bidik) getApplication()).getUrl();
        dataUrl = ((Bidik) getApplication()).getUrlData();
        urlGetData = defaultUrl + "getalldata.html";

        nama= new String[]{"data_kendaraan", "data_leasing", "data_lock", "data_saya"};

    }


    public void startMyOwnForeground()
    {
        //Log.d("disini", "disini");

        getdata();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (berhenti == 0) {
            getjam();
            if (jam.equals(perbaharui)) {
                getdata();
            } else {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("restartservice");
                broadcastIntent.setClass(this, Restarter.class);
                this.sendBroadcast(broadcastIntent);
            }
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("stop", "onCreate() , service stopped...");
    }



    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  "+ (counter++));



            }
        };
        Log.d("jam", jam);
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getjam(){
        myCalendar = Calendar.getInstance();
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        jam = sdf.format(myCalendar.getTime());
        Log.d("jam", jam);
        //Log.d("perbaharui", perbaharui);

    }

    private void getdata() {
        responce = new String[4];
        File file = new File(getFilesDir(), "data_saya" + ".json");
        FileReader fileReader = null;
        try {
            if (file.exists()) {
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                responce[3] = stringBuilder.toString();
            }

            datalocaled();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void datalocaled() {
        try{
            list4 = new ArrayList<>();
            if (responce[3] != null) {
                JSONObject data = new JSONObject(responce[3]);
                JSONArray db = new JSONArray(data.getString("data"));
                if (db.length() > 0) {
                    for (int j = 0; j < db.length(); j++) {
                        final JSONObject daftar = db.getJSONObject(j);

                        modal_datasaya modalDatasaya = new modal_datasaya();
                        modalDatasaya.setId_kendaraan(daftar.getString("id_kendaraan"));
                        modalDatasaya.setCatatan(daftar.getString("catatan"));
                        modalDatasaya.setSimpan(daftar.getString("simpan"));
                        modalDatasaya.setTerhapus(daftar.getString("terhapus"));
                        Log.d("simpan", daftar.getString("simpan"));
                        list4.add(modalDatasaya);
                    }
                }
            }
            new DataServer().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private class DataServer extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_member", id_member.toString()));
            params.add(new BasicNameValuePair("kategori", kategori));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlGetData, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                    }else {
                        data1 = new JSONObject();
                        JSONArray daftarlcl = new JSONArray();
                        if (result.has("lock")) {
                            JSONArray datadb = new JSONArray(result.getString("lock"));
                            if (datadb.length() > 0) {
                                for (int j = 0; j < datadb.length(); j++) {
                                    final JSONObject daftar = datadb.getJSONObject(j);
                                    JSONObject object = new JSONObject();

                                    object.put("id_kendaraan", daftar.getString("id_kendaraan"));
                                    object.put("waktu_selesai", daftar.getString("waktu_selesai"));
                                    object.put("koordinat_terakhir", daftar.getString("koordinat_terakhir"));
                                    object.put("alamat_terakhir", daftar.getString("alamat_terakhir"));

                                    daftarlcl.put(object);

                                }
                                data1.put("data", daftarlcl);
                                responce[2] = data1.toString();
                                savetolocal(data1, nama[2]);
                            }
                        }else {
                            String fileName = "data_lock.json";

                            deleteFile(fileName);
                        }

                        data2 = new JSONObject();
                        JSONArray daftarlcle = new JSONArray();
                        JSONArray db= new JSONArray(result.getString("leasing"));
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

                                daftarlcle.put(object);

                            }
                            data2.put("data", daftarlcle);
                            savetolocal(data2, nama[1]);
                        }

                        data3 = new JSONObject();
                        JSONArray daftarlcla = new JSONArray();
                        JSONArray datadb3= new JSONArray(result.getString("data"));
                        if (datadb3.length() > 0) {
                            for (int j = 0; j < datadb3.length(); j++) {
                                final JSONObject daftar = datadb3.getJSONObject(j);
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

                                if (list4 != null) {
                                    for (int a = 0; a < list4.size(); a++) {
                                        modal_datasaya modalDatasaya = list4.get(a);
                                        if (modalDatasaya.getId_kendaraan().equals(daftar.getString("no_polisi")) && daftar.getString("status").equals("aktif")) {
                                            object.put("catatan", modalDatasaya.getCatatan());
                                            object.put("terhapus", modalDatasaya.getTerhapus());
                                            object.put("simpan", modalDatasaya.getSimpan());
                                            break;
                                        } else {
                                            object.put("catatan", "null");
                                            object.put("terhapus", "0");
                                            object.put("simpan", "0");
                                        }
                                    }
                                }else {
                                    object.put("catatan", "null");
                                    object.put("terhapus", "0");
                                    object.put("simpan", "0");
                                }

                                daftarlcla.put(object);

                            }
                            data3.put("data", daftarlcla);
                            savetolocal(data3, nama[0]);

                        }


                        Log.d("Save", "save");
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("restartservice");
                        broadcastIntent.setClass(getApplication(), Restarter.class);
                        getApplication().sendBroadcast(broadcastIntent);
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

            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
