package com.bidik.app.ui.data_saya;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bidik.app.R;
import com.bidik.app.adapter.adapter_kendaraan;
import com.bidik.app.modal.modal_kendaraan;
import com.bidik.app.modal.modal_lock;
import com.bidik.app.ui.detail.Detail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

public class DataSaya extends Fragment {

    private DataSayaViewModel mViewModel;
    ListView listView;
    List<modal_kendaraan> list;
    List<modal_lock> list2;
    adapter_kendaraan adapter;
    String responce, responce2, waktu_selesai, myFormat = "yyyy-MM-dd HH:mm:ss", tanggal;
    DateFormat sdf;
    Calendar myCalendar;
    Date date, date2;

    public static DataSaya newInstance() {
        return new DataSaya();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.data_saya_fragment, container, false);

        listView = root.findViewById(R.id.list_item);

        myCalendar = Calendar.getInstance();
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal = sdf.format(myCalendar.getTime());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                modal_kendaraan modalKendaraan = (modal_kendaraan) parent.getItemAtPosition(position);
                Fragment newFragment = new Detail();
                Bundle args = new Bundle();
                args.putString("plat", modalKendaraan.getNo_polisi());
                args.putString("model", modalKendaraan.getModel_kendaraan());
                args.putString("leasing", modalKendaraan.getId_leasing());
                newFragment.setArguments(args);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();


            }
        });

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DataSayaViewModel.class);
        // TODO: Use the ViewModel
        getdata();
        dataselected();
    }

    private void getdata() {
        File file = new File(getContext().getFilesDir(),"data_kendaraan" + ".json");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
// This responce will have Json Format String
            responce = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file2 = new File(getContext().getFilesDir(),"data_lock" + ".json");
        FileReader fileReader2 = null;
        try {
            fileReader2 = new FileReader(file2);
            BufferedReader bufferedReader = new BufferedReader(fileReader2);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
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

    }

    private void dataselected() {

        try {
            if (responce2 != null) {
                list2 = new ArrayList<>();
                JSONObject data2 = new JSONObject(responce2);
                if (data2.has("data")) {
                    JSONArray db2 = new JSONArray(data2.getString("data"));
                    if (db2.length() > 0) {
                        for (int j = 0; j < db2.length(); j++) {
                            final JSONObject daftar = db2.getJSONObject(j);

                            waktu_selesai = daftar.getString("waktu_selesai");
                            date = sdf.parse(waktu_selesai);
                            date2 = sdf.parse(tanggal);
                            Log.d("date", String.valueOf(date));
                            Log.d("tanggal", String.valueOf(date2));
                        }
                    }
                }
            }

            list = new ArrayList<>();
            JSONObject data = new JSONObject(responce);
            JSONArray db = new JSONArray(data.getString("data"));
            if (db.length() > 0) {
                for (int j = 0; j < db.length(); j++) {
                    final JSONObject daftar = db.getJSONObject(j);

                    if (daftar.getString("status").equals("aktif") && daftar.getString("terhapus").equals("0") && ((daftar.getString("lock").equals("1") && date.after(date2)) || daftar.getString("simpan").equals("1"))){
                        modal_kendaraan modalKendaraan = new modal_kendaraan();
                        modalKendaraan.setNo_polisi(daftar.getString("no_polisi"));
                        modalKendaraan.setModel_kendaraan(daftar.getString("model_kendaraan"));
                        modalKendaraan.setWarna_kendaraan(daftar.getString("warna_kendaraan"));
                        modalKendaraan.setNo_rangka(daftar.getString("no_rangka"));
                        modalKendaraan.setNo_mesin(daftar.getString("no_mesin"));
                        modalKendaraan.setId_leasing(daftar.getString("id_leasing"));
                        modalKendaraan.setLock(daftar.getString("lock"));
                        modalKendaraan.setTerhapus(daftar.getString("terhapus"));
                        list.add(modalKendaraan);
                    }

                    adapter = new adapter_kendaraan(getActivity(), R.layout.cv_kendaraan, list);
                    listView.setAdapter(adapter);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


}
