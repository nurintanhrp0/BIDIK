package com.bidik.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

import com.bidik.app.R;
import com.bidik.app.modal.modal_leasing;

public class adapter_leasing extends ArrayAdapter<modal_leasing> {
    private List<modal_leasing> daftarMenu;
    private Context context;
    int layout;

    public adapter_leasing(@NonNull Context context, int layout, List<modal_leasing> daftarMenu) {
        super(context, layout, daftarMenu);
        this.daftarMenu = daftarMenu;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v=convertView;
        MenuHolder holder;

        if(v==null){
            LayoutInflater vi=((Activity)context).getLayoutInflater();
            v=vi.inflate(layout, parent,false);

            holder=new MenuHolder();

            holder.nama = v.findViewById(R.id.txtNama);
            holder.detail = v.findViewById(R.id.txtDetail);

            v.setTag(holder);
        }
        else{
            holder=(MenuHolder) v.getTag();


        }

        modal_leasing daftar = daftarMenu.get(position);
        holder.nama.setText(daftar.getNama());
        holder.detail.setText("Cabang : " + daftar.getKota());
        return v;
    }

    static class MenuHolder{
        TextView nama, detail;
    }
}
