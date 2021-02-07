package com.bidik.app.ui.detail;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bidik.app.Bidik;
import com.bidik.app.JSONParser;
import com.bidik.app.KonfirmasiPembayaran;
import com.bidik.app.LocationTrack;
import com.bidik.app.R;
import com.bidik.app.adapter.adapter_kendaraan;
import com.bidik.app.modal.modal_kendaraan;
import com.bidik.app.modal.modal_lock;
import com.bidik.app.ui.home.HomeFragment;
import com.bidik.app.ui.unit_terhapus.UnitTerhapus;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;

public class Detail extends Fragment {

    private DetailViewModel mViewModel;
    TextView txtPlat, txtTanggal, txtModel, txtRangka, txtMesin, txtLeasing, txtOverdue, txtTagihan, txtLock, txtSimpan, txtHapus, txtLokasi, tvAlamat, txtAlamat, txtPeta;
    EditText txtCatatan;
    Intent intent;
    String sPlat, sModel, responce1, sidleasing, responce2, sLeasing, alamat, responce3, kategori, sAlamat;
    LocationTrack locationTrack;
    double latitude, longtitude, lat_unit, long_unit;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String defaultUrl, dataUrl, urlSendData;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Integer id;
    List<modal_kendaraan> list, list2;
    List<modal_lock> list3;
    ImageView gbrlock;
    String waktu_selesai, myFormat = "yyyy-MM-dd HH:mm:ss", tanggal, id_kendaraan, koor_terakhir;
    DateFormat sdf;
    Calendar myCalendar;
    Date date, date2;
    RelativeLayout blockFooter;
    LinearLayout blockKey;

    public static Detail newInstance() {
        return new Detail();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detail_fragment, container, false);

        defaultUrl = ((Bidik) getActivity().getApplication()).getUrl();
        dataUrl = ((Bidik) getActivity().getApplication()).getUrlData();
        urlSendData = defaultUrl + "savelock.html";

        sharedPreferences = getActivity().getSharedPreferences("bidik", 0);
        editor = sharedPreferences.edit();
        id = sharedPreferences.getInt("id", 0);
        kategori = sharedPreferences.getString("kategori", "");

        sPlat = getArguments().getString("plat");
        sModel = getArguments().getString("model");
        sidleasing = getArguments().getString("leasing");

        myCalendar = Calendar.getInstance();
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal = sdf.format(myCalendar.getTime());

        txtPlat = root.findViewById(R.id.txtPlat);
        txtTanggal = root.findViewById(R.id.txtTanggalMasuk);
        txtModel = root.findViewById(R.id.txtModelKendaraan);
        txtRangka = root.findViewById(R.id.txtNomorRangka);
        txtMesin = root.findViewById(R.id.txtNomorMesin);
        txtLeasing= root.findViewById(R.id.txtLeasing);
        txtOverdue = root.findViewById(R.id.txtOverDue);
        txtTagihan = root.findViewById(R.id.txtSisaTagihan);
        txtCatatan = root.findViewById(R.id.inpCatatan);
        txtLock = root.findViewById(R.id.txtLock);
        txtSimpan = root.findViewById(R.id.txtSimpan);
        txtHapus = root.findViewById(R.id.txtHapus);
        gbrlock = root.findViewById(R.id.gbrlock);
        blockFooter = root.findViewById(R.id.blockFooter);
        blockKey = root.findViewById(R.id.blokKey);
        txtLokasi = root.findViewById(R.id.txtLokasi);
        tvAlamat = root.findViewById(R.id.tvAlamat);
        txtAlamat = root.findViewById(R.id.txtAlamat);
        txtPeta = root.findViewById(R.id.txtPeta);

        blockKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Loading ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
                getLocation();
                if (txtLock.getText().toString().equals("Lock Unit")) {
                    new SendData().execute("0");
                }else {
                    new SendData().execute("1");
                }

            }
        });

        txtLokasi.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 pDialog = new ProgressDialog(getActivity());
                 pDialog.setMessage("Loading ...");
                 pDialog.setIndeterminate(false);
                 pDialog.setCancelable(true);
                 pDialog.show();
                 getLocation();
                 new SendData().execute("2");
             }
         }
        );

        blockFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sPlat);
                startActivity(Intent.createChooser(intent, "Share ke"));
            }
        });

        txtSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSimpan.getText().toString().equals("Simpan Data Saya")) {
                    savetolocal("0", txtCatatan.getText().toString(), "0", "1");
                }else {
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Loading ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    new SendData().execute("1");
                    //savetolocal("2", txtCatatan.getText().toString(), "0", "2");
                }

            }
        });

        txtHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtHapus.getText().toString().equals("Kembalikan Data")){
                    savetolocal("0", "null", "2", "0");
                }else {
                    savetolocal("0", "null", "1", "0");
                }

            }
        });

        txtPeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longtitude + "&daddr=" + lat_unit + "," + long_unit + "&mode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i(getTag(), "onKey Back listener is working!!!");
                    Fragment newFragment = new HomeFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    return true;
                } else {
                    return false;
                }
            }
        });

        getdata();
        dataselected(responce1, responce2, responce3);

        return  root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        // TODO: Use the ViewModel
    }

    private void getdata() {
        File file = new File(getContext().getFilesDir(), "data_kendaraan" + ".json");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
// This responce will have Json Format String
            responce1 = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file2 = new File(getContext().getFilesDir(), "data_leasing" + ".json");
        FileReader fileReader2 = null;
        try {
            fileReader2 = new FileReader(file2);
            BufferedReader bufferedReader = new BufferedReader(fileReader2);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
// This responce will have Json Format String
            responce2 = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file3 = new File(getContext().getFilesDir(), "data_lock" + ".json");
        FileReader fileReader3 = null;
        try {
            fileReader3 = new FileReader(file3);
            BufferedReader bufferedReader = new BufferedReader(fileReader3);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
// This responce will have Json Format String
            responce3 = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dataselected(String responce1, String responce2, String responce3) {
        try{
            JSONObject data = new JSONObject(responce2);
            JSONArray db = new JSONArray(data.getString("data"));
            if (db.length() > 0) {
                for (int j = 0; j < db.length(); j++) {
                    final JSONObject daftar = db.getJSONObject(j);

                    if (daftar.getString("id").equals(sidleasing)){
                        sLeasing = daftar.getString("nama") + " - " + daftar.getString("kota");
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            if (responce3 != null) {
                list3 = new ArrayList<>();
                JSONObject data2 = new JSONObject(responce3);
                if (data2.has("data")) {
                    JSONArray db2 = new JSONArray(data2.getString("data"));
                    if (db2.length() > 0) {
                        for (int j = 0; j < db2.length(); j++) {
                            final JSONObject daftar = db2.getJSONObject(j);

                           sAlamat = daftar.getString("alamat_terakhir");
                            waktu_selesai = daftar.getString("waktu_selesai");
                            koor_terakhir = daftar.getString("koordinat_terakhir");
                            String[] titik = koor_terakhir.split(",");
                            lat_unit = Double.parseDouble(titik[0]);
                            long_unit = Double.parseDouble(titik[1]);
                            date = sdf.parse(waktu_selesai);
                            date2 = sdf.parse(tanggal);
                            Log.d("date", String.valueOf(date));
                            Log.d("tanggal", String.valueOf(date2));
                            if (date.after(date2)) {
                                id_kendaraan = daftar.getString("id_kendaraan");
                            }
                        }
                    }
                }
            }

            list = new ArrayList<>();
            JSONObject data = new JSONObject(responce1);
            JSONArray db = new JSONArray(data.getString("data"));
            if (db.length() > 0) {
                for (int j = 0; j < db.length(); j++) {
                    final JSONObject daftar = db.getJSONObject(j);

                    if (daftar.getString("no_polisi").equals(sPlat) && daftar.getString("model_kendaraan").equals(sModel)){
                        txtPlat.setText(daftar.getString("no_polisi"));
                        txtTanggal.setText(daftar.getString("tanggal"));
                        txtModel.setText(daftar.getString("model_kendaraan"));
                        if (daftar.getString("no_rangka").equals("null")){
                            txtRangka.setText("-");
                        }else{
                            txtRangka.setText(daftar.getString("no_rangka"));
                        }
                        if (daftar.getString("no_mesin").equals("null")){
                            txtMesin.setText("-");
                        }else{
                            txtMesin.setText(daftar.getString("no_mesin"));
                        }
                        if (!daftar.getString("catatan").equals("null")){
                            txtCatatan.setText(daftar.getString("catatan"));
                        }
                        if (daftar.getInt("simpan") == 1){
                            txtSimpan.setText("Hapus Data Saya");
                        }
                        if (daftar.getInt("lock") == 1 && date.after(date2)){
                            tvAlamat.setVisibility(View.VISIBLE);
                            txtAlamat.setVisibility(View.VISIBLE);
                            txtPeta.setVisibility(View.VISIBLE);
                            txtAlamat.setText(sAlamat);
                            txtLock.setText("Unlock Unit");
                            txtSimpan.setText("Hapus Data Saya");
                            txtLokasi.setVisibility(View.VISIBLE);
                            gbrlock.setImageDrawable(getResources().getDrawable(R.drawable.unlock));
                        }
                        if(daftar.getInt("terhapus") == 1){
                            txtSimpan.setVisibility(View.GONE);
                            blockKey.setVisibility(View.GONE);
                            txtHapus.setText("Kembalikan Data");
                        }


                        txtLeasing.setText(sLeasing);
                        txtTagihan.setText(daftar.getString("sisa_tagihan"));
                        txtOverdue.setText(daftar.getString("over_due"));
                    }

                    modal_kendaraan modalKendaraan = new modal_kendaraan();
                    modalKendaraan.setId_leasing(daftar.getString("id_leasing"));
                    modalKendaraan.setTanggal(daftar.getString("tanggal"));
                    modalKendaraan.setJenis( daftar.getString("jenis"));
                    modalKendaraan.setCustomer(daftar.getString("customer"));
                    modalKendaraan.setNo_polisi(daftar.getString("no_polisi"));
                    modalKendaraan.setModel_kendaraan(daftar.getString("model_kendaraan"));
                    modalKendaraan.setWarna_kendaraan(daftar.getString("warna_kendaraan"));
                    modalKendaraan.setNo_rangka(daftar.getString("no_rangka"));
                    modalKendaraan.setNo_mesin(daftar.getString("no_mesin"));
                    modalKendaraan.setSisa_tagihan(daftar.getString("sisa_tagihan"));
                    modalKendaraan.setJatuh_tempo(daftar.getString("jatuh_tempo"));
                    modalKendaraan.setOver_due(daftar.getString("over_due"));
                    modalKendaraan.setStatus(daftar.getString("status"));
                    modalKendaraan.setLock(daftar.getString("lock"));
                    modalKendaraan.setCatatan(daftar.getString("catatan"));
                    modalKendaraan.setTerhapus(daftar.getString("terhapus"));
                    modalKendaraan.setSimpan(daftar.getString("simpan"));
                    list.add(modalKendaraan);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void getLocation(){
        locationTrack = new LocationTrack(getActivity());
        if(locationTrack.canGetLocation()) {
            latitude = locationTrack.getLatitude();
            longtitude = locationTrack.getLongitude();
            alamat = locationTrack.getAlamat();

            Log.d("My Current location", "Lat : " + locationTrack.getLatitude() + " Long : " + locationTrack.getLongitude());
        }
        else{
            locationTrack.showSettingsAlert();
        }
    }

    private class SendData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_member", id.toString()));
            params.add(new BasicNameValuePair("id_kendaraan", sPlat));
            params.add(new BasicNameValuePair("koordinat", latitude + ", " + longtitude));
            params.add(new BasicNameValuePair("alamat", alamat));
            params.add(new BasicNameValuePair("kategori", kategori));
            params.add(new BasicNameValuePair("action", strings[0]));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlSendData, "POST", params);

            return jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if ((pDialog != null) && (pDialog.isShowing()))
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getContext(), "Lock dibatasi hanya untuk 1 unit. Data lock unit hanya sudah mencapai batas!", Toast.LENGTH_LONG).show();
                    }else {
                        list2 = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(result.getString("data"));
                        for (int i =0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            modal_kendaraan modalKendaraan = new modal_kendaraan();
                            modalKendaraan.setLock(jsonObject.getString("lock"));
                            list2.add(modalKendaraan);

                        }

                        JSONObject data = new JSONObject();
                        JSONArray daftarlcl = new JSONArray();
                        if (result.has("lock")) {
                            JSONArray db = new JSONArray(result.getString("lock"));
                            if (db.length() > 0) {
                                for (int j = 0; j < db.length(); j++) {
                                    final JSONObject daftar = db.getJSONObject(j);
                                    JSONObject object = new JSONObject();

                                    object.put("id_kendaraan", daftar.getString("id_kendaraan"));
                                    object.put("waktu_selesai", daftar.getString("waktu_selesai"));
                                    object.put("koordinat_terakhir", daftar.getString("koordinat_terakhir"));
                                    object.put("alamat_terakhir", daftar.getString("alamat_terakhir"));

                                    daftarlcl.put(object);

                                }
                                data.put("data", daftarlcl);

                            }
                        }

                        String isi = data.toString();

                        FileOutputStream fOut = null;
                        try {
                            fOut = getActivity().openFileOutput("data_lock" + ".json", MODE_PRIVATE);
                            fOut.write(isi.getBytes());
                            fOut.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (result.getInt("pesan")==0){
                            savetolocal("1", "null", "0", "1");
                        }else {
                            savetolocal("2", "null", "0", "2");
                        }

                    }
                }else {
                    Toast.makeText(getContext(), "Tidak dapat mengirim data ke server!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void savetolocal(String lock, String catatan, String terhapus, String simpan){
        Log.d("catatan", catatan);
        try {
        JSONObject data = new JSONObject();
        JSONArray daftarlcl = new JSONArray();
            for (int j = 0; j < list.size(); j++) {
                modal_kendaraan modalKendaraan =list.get(j);

                JSONObject object = new JSONObject();

                object.put("id_leasing", modalKendaraan.getId_leasing());
                object.put("tanggal", modalKendaraan.getTanggal());
                object.put("jenis", modalKendaraan.getJenis());
                object.put("customer", modalKendaraan.getCustomer());
                object.put("no_polisi", modalKendaraan.getNo_polisi());
                object.put("model_kendaraan", modalKendaraan.getModel_kendaraan());
                object.put("warna_kendaraan", modalKendaraan.getWarna_kendaraan());
                object.put("no_rangka", modalKendaraan.getNo_rangka());
                object.put("no_mesin", modalKendaraan.getNo_mesin());
                object.put("sisa_tagihan", modalKendaraan.getSisa_tagihan());
                object.put("jatuh_tempo", modalKendaraan.getJatuh_tempo());
                object.put("over_due", modalKendaraan.getOver_due());
                object.put("status", modalKendaraan.getStatus());
                if (!lock.equals("0")){
                    modal_kendaraan modal2 = list2.get(j);
                    object.put("lock", modal2.getLock());
                }else {
                    object.put("lock", modalKendaraan.getLock());
                }

                if (!catatan.equals("null") && modalKendaraan.getNo_polisi().equals(sPlat)){
                    object.put("catatan", catatan);
                }else{
                    object.put("catatan", modalKendaraan.getCatatan());
                }
                if (terhapus.equals("1") && modalKendaraan.getNo_polisi().equals(sPlat)){
                    object.put("terhapus", "1");
                }else if (terhapus.equals("2") && modalKendaraan.getNo_polisi().equals(sPlat)){
                    object.put("terhapus", 0);
                }else{
                    object.put("terhapus", modalKendaraan.getTerhapus());
                }
                if (simpan.equals("1") && modalKendaraan.getNo_polisi().equals(sPlat)){
                    object.put("simpan", "1");
                }else if (simpan.equals("2") && modalKendaraan.getNo_polisi().equals(sPlat)) {
                    object.put("simpan", "0");
                }else {
                    object.put("simpan", modalKendaraan.getSimpan());
                }


                daftarlcl.put(object);

            }
            data.put("data", daftarlcl);

            savefile(data, lock, terhapus, simpan);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void savefile(JSONObject data, String lock, String terhapus, String simpan){
        String isi = data.toString();

        FileOutputStream fOut = null;
        try {
            fOut = getActivity().openFileOutput("data_kendaraan" + ".json", MODE_PRIVATE);
            fOut.write(isi.getBytes());
            fOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*if (lock.equals("1")){
            Toast.makeText(getContext(), "Berhasil Menyimpan! " ,Toast.LENGTH_SHORT).show();

        }else if (lock.equals("2")){
            Toast.makeText(getContext(), "Berhasil Unlock Unit! " ,Toast.LENGTH_SHORT).show();
            Fragment newFragment = new HomeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }*/
        Toast.makeText(getContext(), "Berhasil Melakukan Perubahan! " ,Toast.LENGTH_SHORT).show();
        if (lock.equals("0")) {
            if (terhapus.equals("1")){
                savemydata();
            }else if (terhapus.equals("2")){
                savemydata();
            }
            if (simpan.equals("1")){
                savemydata();
            }else if (simpan.equals("2")){
                savemydata();
            }
            Fragment newFragment = new HomeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }else{
            Fragment newFragment = new Detail();
            Bundle args = new Bundle();
            args.putString("plat", sPlat);
            args.putString("model", sModel);
            args.putString("leasing", sidleasing);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


    }

    public void savemydata(){
        getdata();
        dataselected(responce1, responce2, responce3);

        try {
            JSONObject data = new JSONObject();
            JSONArray daftarlcl = new JSONArray();
            for (int j = 0; j < list.size(); j++) {
                modal_kendaraan modalKendaraan = list.get(j);
                JSONObject object = new JSONObject();

                if (modalKendaraan.getTerhapus().equals("1") || modalKendaraan.getSimpan().equals("1")) {

                    object.put("id_kendaraan", modalKendaraan.getNo_polisi());
                    object.put("catatan", modalKendaraan.getCatatan());
                    object.put("simpan", modalKendaraan.getSimpan());
                    object.put("terhapus", modalKendaraan.getTerhapus());

                    daftarlcl.put(object);
                }

            }
            data.put("data", daftarlcl);

            String isi = data.toString();

            FileOutputStream fOut = null;
            try {
                fOut = getActivity().openFileOutput("data_saya" + ".json", MODE_PRIVATE);
                fOut.write(isi.getBytes());
                fOut.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
