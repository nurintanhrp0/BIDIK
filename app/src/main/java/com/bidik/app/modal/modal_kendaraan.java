package com.bidik.app.modal;

public class modal_kendaraan {
    String id_leasing, tanggal, jenis, customer, no_polisi, model_kendaraan, warna_kendaraan, no_rangka, no_mesin, sisa_tagihan, jatuh_tempo,
            over_due, status, catatan, lock, terhapus, simpan;

    public modal_kendaraan(){}

    public String getId_leasing() {
        return id_leasing;
    }

    public void setId_leasing(String id_leasing) {
        this.id_leasing = id_leasing;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getNo_polisi() {
        return no_polisi;
    }

    public void setNo_polisi(String no_polisi) {
        this.no_polisi = no_polisi;
    }

    public String getModel_kendaraan() {
        return model_kendaraan;
    }

    public void setModel_kendaraan(String model_kendaraan) {
        this.model_kendaraan = model_kendaraan;
    }

    public String getWarna_kendaraan() {
        return warna_kendaraan;
    }

    public void setWarna_kendaraan(String warna_kendaraan) {
        this.warna_kendaraan = warna_kendaraan;
    }

    public String getNo_rangka() {
        return no_rangka;
    }

    public void setNo_rangka(String no_rangka) {
        this.no_rangka = no_rangka;
    }

    public String getNo_mesin() {
        return no_mesin;
    }

    public void setNo_mesin(String no_mesin) {
        this.no_mesin = no_mesin;
    }

    public String getSisa_tagihan() {
        return sisa_tagihan;
    }

    public void setSisa_tagihan(String sisa_tagihan) {
        this.sisa_tagihan = sisa_tagihan;
    }

    public String getJatuh_tempo() {
        return jatuh_tempo;
    }

    public void setJatuh_tempo(String jatuh_tempo) {
        this.jatuh_tempo = jatuh_tempo;
    }

    public String getOver_due() {
        return over_due;
    }

    public void setOver_due(String over_due) {
        this.over_due = over_due;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public modal_kendaraan(String id_leasing, String tanggal, String jenis, String customer, String no_polisi, String model_kendaraan, String warna_kendaraan, String no_rangka, String no_mesin, String sisa_tagihan, String jatuh_tempo, String over_due, String status, String catatan, String lock, String terhapus, String simpan) {
        this.id_leasing = id_leasing;
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.customer = customer;
        this.no_polisi = no_polisi;
        this.model_kendaraan = model_kendaraan;
        this.warna_kendaraan = warna_kendaraan;
        this.no_rangka = no_rangka;
        this.no_mesin = no_mesin;
        this.sisa_tagihan = sisa_tagihan;
        this.jatuh_tempo = jatuh_tempo;
        this.over_due = over_due;
        this.status = status;
        this.catatan = catatan;
        this.lock = lock;
        this.terhapus = terhapus;
        this.simpan = simpan;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public String getTerhapus() {
        return terhapus;
    }

    public void setTerhapus(String terhapus) {
        this.terhapus = terhapus;
    }

    public String getSimpan() {
        return simpan;
    }

    public void setSimpan(String simpan) {
        this.simpan = simpan;
    }
}
