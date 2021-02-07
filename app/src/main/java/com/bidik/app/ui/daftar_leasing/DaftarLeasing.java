package com.bidik.app.ui.daftar_leasing;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bidik.app.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.bidik.app.modal.modal_leasing;
import com.bidik.app.adapter.adapter_leasing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DaftarLeasing extends Fragment {

    private DaftarLeasingViewModel mViewModel;
    ListView listView;
    List<modal_leasing> list;
    adapter_leasing adapter;

    public static DaftarLeasing newInstance() {
        return new DaftarLeasing();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daftar_leasing_fragment, container, false);

        listView = root.findViewById(R.id.list_item);
        list = new ArrayList<>();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DaftarLeasingViewModel.class);
        // TODO: Use the ViewModel
        getdata();
    }

    private void getdata() {
        File file = new File(getContext().getFilesDir(),"data_leasing" + ".json");
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
            String responce = stringBuilder.toString();
            showdata(responce);
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
            if (db.length() > 0) {
                for (int j = 0; j < db.length(); j++) {
                    final JSONObject daftar = db.getJSONObject(j);

                    modal_leasing modalLeasing = new modal_leasing();
                    modalLeasing.setId(daftar.getString("id"));
                    modalLeasing.setNama(daftar.getString("nama"));
                    modalLeasing.setKota(daftar.getString("kota"));

                    list.add(modalLeasing);
                    adapter = new adapter_leasing(getActivity(), R.layout.cv_kendaraan, list);
                    listView.setAdapter(adapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
