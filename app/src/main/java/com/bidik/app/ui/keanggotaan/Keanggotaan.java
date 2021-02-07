package com.bidik.app.ui.keanggotaan;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bidik.app.Bidik;
import com.bidik.app.JSONParser;
import com.bidik.app.KonfirmasiPembayaran;
import com.bidik.app.R;
import com.bidik.app.Welcome;
import com.bidik.app.YourService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Keanggotaan extends Fragment {

    private KeanggotaanViewModel mViewModel;
    TextView txtNama, txtNohp, txtExp;
    Button btnPerpanjang, btnLogout;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    String defaultUrl, dataUrl, urlGetData;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String noHp, nama, exp;
    Intent mServiceIntent;
    private YourService mYourService;

    public static Keanggotaan newInstance() {
        return new Keanggotaan();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.keanggotaan_fragment, container, false);

        sharedPreferences = getActivity().getSharedPreferences("bidik", 0);
        editor = sharedPreferences.edit();
        noHp = sharedPreferences.getString("noHp", "");
        nama = sharedPreferences.getString("nama", "");
        exp = sharedPreferences.getString("exp", "");

        defaultUrl = ((Bidik) getActivity().getApplication()).getUrl();
        dataUrl = ((Bidik) getActivity().getApplication()).getUrlData();
        urlGetData = defaultUrl + "getpembayaran.html";

        txtNama = root.findViewById(R.id.txtNama);
        txtNohp = root.findViewById(R.id.txtNohp);
        txtExp = root.findViewById(R.id.txtExp);
        btnPerpanjang = root.findViewById(R.id.btnPerpanjang);
        btnLogout = root.findViewById(R.id.btnLogoutout);

        txtNama.setText(nama);
        txtNohp.setText(noHp);
        txtExp.setText(exp);

        mYourService = new YourService();
        mServiceIntent = new Intent(getActivity(), mYourService.getClass());

        btnPerpanjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KonfirmasiPembayaran.class);
                intent.putExtra("dari", 1);
                getContext().startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Welcome.class);
                editor.clear().apply();
                editor.putInt("berhenti", 1).apply();
                getActivity().stopService(mServiceIntent);
                getContext().startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(KeanggotaanViewModel.class);
        // TODO: Use the ViewModel


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
            pDialog = new ProgressDialog(getContext());
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
                        Toast.makeText(getActivity().getApplicationContext(), "Tidak dapat mengambil data dari server", Toast.LENGTH_LONG).show();
                    }else {
                        txtNama.setText(result.getString("nama"));
                        txtNohp.setText(result.getString("nohp"));
                        txtExp.setText(result.getString("exp"));
                    }
                }else {
                    //Toast.makeText(getApplicationContext(), "Ups! Menu yang kamu pilih belum tersedia di outlet ini.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}
