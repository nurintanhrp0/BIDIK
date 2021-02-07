package com.bidik.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bidik.app.R;
import com.bidik.app.modal.modal_kendaraan;

import java.util.List;

public class adapter_kendaraan extends ArrayAdapter<modal_kendaraan> {
    private List<modal_kendaraan> daftarMenu;
    private Context context;
    int layout;

    public adapter_kendaraan(@NonNull Context context, int layout, List<modal_kendaraan> daftarMenu) {
        super(context, layout, daftarMenu);
        this.daftarMenu = daftarMenu;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        MenuHolder holder;

        if (v == null) {
            LayoutInflater vi = ((Activity) context).getLayoutInflater();
            v = vi.inflate(layout, parent, false);

            holder = new MenuHolder();

            holder.nama = v.findViewById(R.id.txtNama);
            holder.detail = v.findViewById(R.id.txtDetail);
            holder.gembok = v.findViewById(R.id.gembok);

            v.setTag(holder);
        } else {
            holder = (MenuHolder) v.getTag();


        }

        modal_kendaraan daftar = daftarMenu.get(position);
        holder.nama.setText(daftar.getNo_polisi());
        if (daftar.getWarna_kendaraan().equals("null")) {
            if (daftar.getNo_rangka().equals("null")) {
                if (daftar.getNo_mesin().equals("null")) {
                    holder.detail.setText(daftar.getModel_kendaraan());
                } else {
                    holder.detail.setText(daftar.getModel_kendaraan() + " " + daftar.getNo_mesin());
                }

            } else {
                if (daftar.getNo_mesin().equals("null")) {
                    holder.detail.setText(daftar.getModel_kendaraan() + " " + daftar.getNo_rangka() );
                } else {
                    holder.detail.setText(daftar.getModel_kendaraan() +  " " + daftar.getNo_rangka() + " " + daftar.getNo_mesin());
                }
            }
    }
       else {
            if (daftar.getNo_rangka().equals("null")){
                if (daftar.getNo_mesin().equals("null")){
                    holder.detail.setText(daftar.getModel_kendaraan() + " " + daftar.getWarna_kendaraan());
                }else {
                    holder.detail.setText(daftar.getModel_kendaraan() + " " + daftar.getWarna_kendaraan() + " " + daftar.getNo_mesin());
                }
            }else {
                if (daftar.getNo_mesin().equals("null")){
                    holder.detail.setText(daftar.getModel_kendaraan() + " " + daftar.getWarna_kendaraan()  + " " + daftar.getNo_rangka() );
                }else {
                    holder.detail.setText(daftar.getModel_kendaraan() + " " + daftar.getWarna_kendaraan() + " " + daftar.getNo_rangka()  + " " + daftar.getNo_mesin());
                }
            }
       }
       if (daftar.getLock().equals("1") && daftar.getTerhapus().equals("0")){
           holder.gembok.setVisibility(View.VISIBLE);
       }else {
           holder.gembok.setVisibility(View.GONE);
       }
          return v;
    }

    static class MenuHolder{
        TextView nama, detail;
        ImageView gembok;
    }
}
