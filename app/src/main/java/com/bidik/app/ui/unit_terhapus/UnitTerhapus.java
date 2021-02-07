package com.bidik.app.ui.unit_terhapus;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bidik.app.R;
import com.bidik.app.adapter.adapter_kendaraan;
import com.bidik.app.modal.modal_kendaraan;
import com.bidik.app.ui.detail.Detail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnitTerhapus extends Fragment {

    private UnitTerhapusViewModel mViewModel;
    ListView listView;
    String responce;
    List<modal_kendaraan> list;
    adapter_kendaraan adapter;

    public static UnitTerhapus newInstance() {
        return new UnitTerhapus();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.unit_terhapus_fragment, container, false);

        listView = root.findViewById(R.id.list_item);

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
        mViewModel = ViewModelProviders.of(this).get(UnitTerhapusViewModel.class);
        // TODO: Use the ViewModel
        getdata();
        dataselected(responce);
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

    }

    private void dataselected(String responce) {

        try {
            list = new ArrayList<>();
            JSONObject data = new JSONObject(responce);
            JSONArray db = new JSONArray(data.getString("data"));
            if (db.length() > 0) {
                for (int j = 0; j < db.length(); j++) {
                    final JSONObject daftar = db.getJSONObject(j);

                        if (daftar.getString("terhapus").equals("1") && daftar.getString("status").equals("aktif")){
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
        }


    }

}
