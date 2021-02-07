package com.bidik.app.ui.perbaharui_data;

import androidx.lifecycle.ViewModelProviders;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bidik.app.Bidik;
import com.bidik.app.JSONParser;
import com.bidik.app.Login;
import com.bidik.app.MainActivity;
import com.bidik.app.R;

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
import java.util.List;
import java.util.Locale;

import com.bidik.app.YourService;
import com.bidik.app.modal.*;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class PerbaharuiData extends Fragment {

    private PerbaharuiDataViewModel mViewModel;
    TextView txtJam;
    Button btnSimpan, btnPerbaharui;
    Calendar myCalendar;
    TimePickerDialog.OnTimeSetListener time;
    String myformat = "HH:mm", kategori;
    TimePickerDialog timePickerDialog;
    String[] responce, nama;
    List<modal_kendaraan> list1;
    List<modal_leasing> list2;
    List<modal_lock> list3;
    List<modal_datasaya> list4;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String defaultUrl, dataUrl, urlGetData;
    JSONObject data1, data2, data3;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Integer id_member;
    DateFormat sdf;
    Intent mServiceIntent;
    private YourService mYourService;
    String perbaharui;

    public static PerbaharuiData newInstance() {
        return new PerbaharuiData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.perbaharui_data_fragment, container, false);

        defaultUrl = ((Bidik) getActivity().getApplication()).getUrl();
        dataUrl = ((Bidik) getActivity().getApplication()).getUrlData();
        urlGetData = defaultUrl + "getalldata.html";

        sharedPreferences = getActivity().getSharedPreferences("bidik", 0);
        editor = sharedPreferences.edit();
        id_member = sharedPreferences.getInt("id", 0);
        kategori = sharedPreferences.getString("kategori", "");
        perbaharui = sharedPreferences.getString("perbaharui", "00:00");

        txtJam = root.findViewById(R.id.txtJam);
        btnSimpan = root.findViewById(R.id.btnSimpan);
        btnPerbaharui = root.findViewById(R.id.btnPerbaharui);
        txtJam.setText(perbaharui);

        sdf = new SimpleDateFormat(myformat, Locale.US);

        nama= new String[]{"data_kendaraan", "data_leasing", "data_lock", "data_saya"};

        myCalendar = Calendar.getInstance();
        mYourService = new YourService();
        mServiceIntent = new Intent(getActivity(), mYourService.getClass());

        txtJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        try {
                            txtJam.setText( sdf.format(sdf.parse(selectedHour + ":" + selectedMinute)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Pilih Jam");
                mTimePicker.show();
            }
        });

        btnPerbaharui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ubah",  txtJam.getText().toString());
                editor.putString("perbaharui", txtJam.getText().toString()).apply();
                Toast.makeText(getActivity(), "Behasil Menyimpan Perubahan...", Toast.LENGTH_SHORT).show();
                getActivity().stopService(mServiceIntent);
                    getActivity().startService(mServiceIntent);

            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PerbaharuiDataViewModel.class);
        // TODO: Use the ViewModel
    }

    private void getdata() {
        responce = new String[4];
            File file = new File(getContext().getFilesDir(), "data_saya" + ".json");
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
            if (responce[3] != null) {
                list4 = new ArrayList<>();
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
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                if (result != null){
                    if (result.getInt("error") == 1){
                        Toast.makeText(getContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
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

                            getContext().deleteFile(fileName);
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

                        if ((pDialog != null) && (pDialog.isShowing()))
                            pDialog.dismiss();
                        pDialog = null;

                        Toast.makeText(getContext(), "Data Berhasil Di Perbaharui...",
                                 Toast.LENGTH_SHORT).show();
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
                fOut = getActivity().openFileOutput(nama + ".json", MODE_PRIVATE);
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
