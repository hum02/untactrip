package com.example.untactrip3.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum_code {
    @SerializedName("lDongCd")
    @Expose
    private String lDongCd;
    @SerializedName("siDo")
    @Expose
    private String siDo;
    @SerializedName("siGunGu")
    @Expose
    private String siGunGu;
    @SerializedName("dong")
    @Expose
    private String dong;

    public String getDong() {
        return dong;
    }
    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getLDongCd() {
        return lDongCd;
    }

    public void setLDongCd(String lDongCd) {
        this.lDongCd = lDongCd;
    }

    public String  getSiDo() {
        return siDo;
    }

    public void setSiDo(String siDo) {
        this.siDo = siDo;
    }

    public String getSiGunGu() {return siGunGu;}

    public void setSiGunGu(String siGunGu) {
        this.siGunGu = siGunGu;
    }
}
