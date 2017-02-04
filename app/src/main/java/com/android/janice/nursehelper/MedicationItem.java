package com.android.janice.nursehelper;

import android.graphics.Bitmap;

/**
 * Created by janicerichards on 2/4/17.
 */

public class MedicationItem {
    //private String roomNumber;
    //private Bitmap portrait;

    private String genericName;
    private String tradeName;

    public String getGenericName() {
        return genericName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }
}
