package com.bidik.app.modal;

public class modal_leasing {
    String id, nama, alamat, kota, conctact_person, no_hp, status;

    public modal_leasing(){}

    public modal_leasing(String id, String nama, String alamat, String kota, String conctact_person, String no_hp, String status) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.conctact_person = conctact_person;
        this.no_hp = no_hp;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getConctact_person() {
        return conctact_person;
    }

    public void setConctact_person(String conctact_person) {
        this.conctact_person = conctact_person;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
