package com.android.janice.nursehelper.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MedCheckSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MedCheckSyncAdapter sMedCheckSyncAdapter = null;

    @Override
    public void onCreate() {
        //Log.d("MedCheckSyncService", "onCreate - MedCheckSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMedCheckSyncAdapter == null) {
                sMedCheckSyncAdapter = new MedCheckSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMedCheckSyncAdapter.getSyncAdapterBinder();
    }
}