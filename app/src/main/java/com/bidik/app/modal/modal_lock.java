package com.bidik.app.modal;

public class modal_lock {
    String id_member, id_kendaraan, waktu_selesai, koordinat_terakhir, alamat_terakhir;

    public modal_lock(){}

    public modal_lock(String id_member, String id_kendaraan, String waktu_selesai, String koordinat_terakhir, String alamat_terakhir) {
        this.id_member = id_member;
        this.id_kendaraan = id_kendaraan;
        this.waktu_selesai = waktu_selesai;
        this.koordinat_terakhir = koordinat_terakhir;
        this.alamat_terakhir = alamat_terakhir;
    }

    public String getId_member() {
        return id_member;
    }

    public void setId_member(String id_member) {
        this.id_member = id_member;
    }

    public String getId_kendaraan() {
        return id_kendaraan;
    }

    public void setId_kendaraan(String id_kendaraan) {
        this.id_kendaraan = id_kendaraan;
    }

    public String getWaktu_selesai() {
        return waktu_selesai;
    }

    public void setWaktu_selesai(String waktu_selesai) {
        this.waktu_selesai = waktu_selesai;
    }

    public String getKoordinat_terakhir() {
        return koordinat_terakhir;
    }

    public void setKoordinat_terakhir(String koordinat_terakhir) {
        this.koordinat_terakhir = koordinat_terakhir;
    }

    public String getAlamat_terakhir() {
        return alamat_terakhir;
    }

    public void setAlamat_terakhir(String alamat_terakhir) {
        this.alamat_terakhir = alamat_terakhir;
    }
}
