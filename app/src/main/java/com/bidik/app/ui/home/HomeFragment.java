package com.bidik.app.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bidik.app.KonfirmasiPembayaran;
import com.bidik.app.YourService;
import com.bidik.app.adapter.adapter_kendaraan;
import com.bidik.app.adapter.adapter_leasing;
import com.bidik.app.modal.modal_kendaraan;

import com.bidik.app.R;
import com.bidik.app.modal.modal_leasing;
import com.bidik.app.modal.modal_lock;
import com.bidik.app.ui.daftar_leasing.DaftarLeasingViewModel;
import com.bidik.app.ui.detail.Detail;
import com.bidik.app.ui.keanggotaan.Keanggotaan;

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

public class HomeFragment extends Fragment {

    AutoCompleteTextView txtCari;
    EditText inpCari;
    String[] plat, model;
    String[] filter = {"No. Polisi", "No. Rangka", "No. Mesin"};
    List<modal_kendaraan> list;
    List<modal_lock> list2;
    adapter_kendaraan adapter;
    ListView listView;
    private HomeViewModel mViewModel;
    Spinner sp_filter;
    String sFilter, sItem, responce, responce2, id_kendaraan="0";
    Intent intent;
    String waktu_selesai, myFormat = "yyyy-MM-dd HH:mm:ss", tanggal, exp, expe, format = "yyyy-MM-dd", now;
    DateFormat sdf, sjf;
    Calendar myCalendar;
    Date date, date2;
    SharedPreferences sharedPreferences;
    Integer berhenti;
    SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("bidik", 0);
        editor = sharedPreferences.edit();
        berhenti = sharedPreferences.getInt("berhenti", 0);
        exp = sharedPreferences.getString("expe", "");
        Log.d("berhenti", berhenti.toString());

        txtCari = root.findViewById(R.id.txtCari);
        listView = root.findViewById(R.id.list_item);
        sp_filter = root.findViewById(R.id.sp_filter);
        txtCari.setThreshold(1);
        txtCari.setMaxLines(1);
        txtCari.setEllipsize(TextUtils.TruncateAt.END);
        inpCari = root.findViewById(R.id.inpCari);
        inpCari.setMaxLines(1);
        list = new ArrayList<>();

        myCalendar = Calendar.getInstance();
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        sjf = new SimpleDateFormat(format, Locale.US);
        tanggal = sdf.format(myCalendar.getTime());
        now = sjf.format(myCalendar.getTime());

        try {
            if (sjf.parse(now).after(sjf.parse(exp))){
                showwarning();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        inpCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                list = new ArrayList<>();
                sItem = inpCari.getText().toString();
                getdata();
                dataselected();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, filter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_filter.setAdapter(adapter);

        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getfilter();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtCari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list = new ArrayList<>();
                sItem = txtCari.getText().toString();
                Log.d("item", sItem);
                hideKeyboard();
                getdata();
                dataselected();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                hideKeyboard();
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

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i(getTag(), "onKey Back listener is working!!!");
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    // String cameback="CameBack";
                    getActivity().finish();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getActivity().finishAffinity();
                    }
                    System.exit(0);
                    return true;
                } else {
                    return false;
                }
            }
        });

        getdata();
        showdata(responce);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel

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

    private void showdata(String responce) {

        try {
            JSONObject data = new JSONObject(responce);
            JSONArray db = new JSONArray(data.getString("data"));
            int i=0;
            if (db.length() > 0) {
                for (int j = 0; j < db.length(); j++) {
                    final JSONObject daftar = db.getJSONObject(j);

                        modal_kendaraan modalKendaraan = new modal_kendaraan();
                        modalKendaraan.setNo_polisi(daftar.getString("no_polisi"));
                        modalKendaraan.setModel_kendaraan(daftar.getString("model_kendaraan"));
                        modalKendaraan.setWarna_kendaraan(daftar.getString("warna_kendaraan"));
                        modalKendaraan.setNo_rangka(daftar.getString("no_rangka"));
                        modalKendaraan.setNo_mesin(daftar.getString("no_mesin"));
                        modalKendaraan.setTerhapus(daftar.getString("terhapus"));
                        modalKendaraan.setStatus(daftar.getString("status"));
                        list.add(modalKendaraan);

                    if (daftar.getString("terhapus").equals("0") && daftar.getString("status").equals("aktif")) {
                        i=i+1;
                    }

                }

            }
            plat = new String[i];
            model = new String[i];
            int m=0;
            for (int k=0; k<list.size();k++){
                modal_kendaraan modalKendaraan = list.get(k);
                if (modalKendaraan.getTerhapus().equals("0") && modalKendaraan.getStatus().equals("aktif")) {
                    plat[m]=modalKendaraan.getNo_polisi();
                    model[m]=modalKendaraan.getModel_kendaraan().toLowerCase();
                    m=m+1;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getfilter() {
        sFilter = sp_filter.getSelectedItem().toString();

        if (sFilter.equals("No. Polisi")) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, plat);
            txtCari.setAdapter(adapter1);
        }else {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, model);
            txtCari.setAdapter(adapter1);
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
                            if (date.after(date2)) {
                                id_kendaraan = daftar.getString("id_kendaraan");
                            }
                        }
                    }
                }
            }


            JSONObject data = new JSONObject(responce);
            JSONArray db = new JSONArray(data.getString("data"));
            if (db.length() > 0) {
                for (int j = 0; j < db.length(); j++) {
                    final JSONObject daftar = db.getJSONObject(j);

                    if (daftar.getString("terhapus").equals("0")&& daftar.getString("status").equals("aktif")) {
                        modal_kendaraan modalKendaraan = new modal_kendaraan();
                        modalKendaraan.setNo_polisi(daftar.getString("no_polisi"));
                        modalKendaraan.setModel_kendaraan(daftar.getString("model_kendaraan"));
                        modalKendaraan.setWarna_kendaraan(daftar.getString("warna_kendaraan"));
                        modalKendaraan.setNo_rangka(daftar.getString("no_rangka"));
                        modalKendaraan.setNo_mesin(daftar.getString("no_mesin"));
                        modalKendaraan.setId_leasing(daftar.getString("id_leasing"));
                        if (id_kendaraan.equals(daftar.getString("no_polisi"))) {
                            if (date.before(date2)) {
                                modalKendaraan.setLock("0");
                            } else {
                                modalKendaraan.setLock(daftar.getString("lock"));
                            }
                        } else {
                            modalKendaraan.setLock(daftar.getString("lock"));
                        }
                        modalKendaraan.setTerhapus(daftar.getString("terhapus"));
                        if (sFilter.equals("No. Polisi")) {
                            if (containsIgnorecase(daftar.getString("no_polisi"), sItem)) {
                                list.add(modalKendaraan);
                            }
                        } else if (sFilter.equals("No. Rangka")) {
                            if (containsIgnorecase(daftar.getString("no_rangka"), sItem) && !daftar.getString("no_mesin").equals("NULL")) {
                                list.add(modalKendaraan);
                            }
                        } else {
                            if (containsIgnorecase(daftar.getString("no_mesin"), sItem) && !daftar.getString("no_mesin").equals("NULL")) {
                                list.add(modalKendaraan);
                            }
                        }
                        adapter = new adapter_kendaraan(getActivity(), R.layout.cv_kendaraan, list);
                        listView.setAdapter(adapter);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputmanager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception var2) {
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    public void showwarning(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.DialogPutih);
        final LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_warning, null);
        dialog.setView(dialogView);
        Button icOk = dialogView.findViewById(R.id.btnOk);
        TextView isi = dialogView.findViewById(R.id.isi);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.setCancelable(false);

        icOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
               Intent intent = new Intent(getActivity(), KonfirmasiPembayaran.class);
               intent.putExtra("dari", 2);
               startActivity(intent);
            }
        });

        alertDialog.show();
    }

    public static boolean containsIgnorecase( String str, String sub){
        return  str.toLowerCase().contains(sub.toLowerCase());
    }


}
