package com.android.janice.nursehelper;

/**
 * Created by janicerichards on 3/12/17.
 */

public class FacilityLocation {
    protected static final double FACILITY_LAT = 42.306306306306304;
    protected static final double FACITITY_LONGITUDE = -83.71215290813885;
    private static final double allowableError = 0.035;
    //lat = 42.306306306306304   long = -83.71215290813885
    //lat = 42.306306306306304   long = -83.68778965059865

    protected static boolean isDeviceAtFacility(double latitude, double longitude) {
        double latDiff = Math.abs(latitude-FACILITY_LAT);
        double longDiff = Math.abs(longitude-FACITITY_LONGITUDE);
        if (latDiff <= allowableError && longDiff <= allowableError) return true;
        return false;
    }
}
