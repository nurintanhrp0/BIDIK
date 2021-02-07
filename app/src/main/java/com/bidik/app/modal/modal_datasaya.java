package com.bidik.app.modal;

public class modal_datasaya {
    String id_kendaraan, catatan, simpan, terhapus;

    public modal_datasaya(){}

    public modal_datasaya(String id_kendaraan, String catatan, String simpan, String terhapus) {
        this.id_kendaraan = id_kendaraan;
        this.catatan = catatan;
        this.simpan = simpan;
        this.terhapus = terhapus;
    }

    public String getId_kendaraan() {
        return id_kendaraan;
    }

    public void setId_kendaraan(String id_kendaraan) {
        this.id_kendaraan = id_kendaraan;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getSimpan() {
        return simpan;
    }

    public void setSimpan(String simpan) {
        this.simpan = simpan;
    }

    public String getTerhapus() {
        return terhapus;
    }

    public void setTerhapus(String terhapus) {
        this.terhapus = terhapus;
    }
}
