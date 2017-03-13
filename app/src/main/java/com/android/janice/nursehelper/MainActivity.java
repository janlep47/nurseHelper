package com.android.janice.nursehelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;

import com.android.janice.nursehelper.sync.MedCheckSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements ResidentlistFragment.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String ITEM_ROOM_NUMBER = "roomNumber";
    public static final String ITEM_PORTRAIT_FILEPATH = "portraitFilePath";
    public static final String ITEM_NURSE_NAME = "nurseName";

    private String mNurseName;

    private GoogleApiClient mGoogleApiClient;

    private final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mNurseName = getIntent().getStringExtra(ITEM_NURSE_NAME);
        } else {
            mNurseName = savedInstanceState.getString(ITEM_NURSE_NAME);
        }
        //Log.d(LOG_TAG," mNurseName: "+mNurseName);

        //ResidentlistFragment mFragment = ((ResidentlistFragment)getSupportFragmentManager()
        //        .findFragmentById(R.id.fragment_residentlist));

        ResidentItem.putInDummyData(this);
        MedicationItem.putInDummyData(this);
        AssessmentItem.putInDummyData(this);



        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        //TEMPORARY !!!!!  MedCheckSyncAdapter.initializeSyncAdapter(this);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "onConnected: " + String.valueOf(mGoogleApiClient.isConnected()));
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            checkPermission();
        }

        //LocationClient mLocationClient = new LocationClient(this,this,this);

        LocationRequest mLocationRequest = LocationRequest.create();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.e(LOG_TAG," ERROR: no rights to check device location!! \n"+
                    e.toString());
        }

    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (FacilityLocation.isDeviceAtFacility(location.getLatitude(), location.getLongitude()))
            // Location is good, continue on with UI:
            updateUI();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.invalid_location_message)
                    .setTitle(R.string.dialog_invalid_location);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FirebaseAuth.getInstance().signOut();
                    MainActivity.this.finishAffinity();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    private void updateUI() {
        setContentView(R.layout.activity_main);
        ResidentlistFragment mFragment = ((ResidentlistFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_residentlist));
        MedCheckSyncAdapter.initializeSyncAdapter(this);
    }


    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "onConnectionSuspended: " + cause);
        }
    }


    @Override // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "onConnectionFailed: " + result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.residentlist, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            this.finishAffinity();
            return true;
        }

        if (id == R.id.about)     {
            AboutDialogFragment dialog = new AboutDialogFragment();
            dialog.show(getSupportFragmentManager(),"About:");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemSelected(String roomNumber, String portraitFilePath,
                               int selectionType, ResidentlistAdapter.ResidentlistAdapterViewHolder vh) {
        Intent intent;
        Bundle bundle = new Bundle();
        bundle.putString(ITEM_ROOM_NUMBER,roomNumber);
        bundle.putString(ITEM_PORTRAIT_FILEPATH,portraitFilePath);
        bundle.putString(ITEM_NURSE_NAME,mNurseName);

        switch (selectionType) {
            case ResidentlistAdapter.ResidentlistAdapterViewHolder.MEDICATIONS_SELECTED:
                intent = new Intent(this, MedicationsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case ResidentlistAdapter.ResidentlistAdapterViewHolder.ASSESSMENT_SELECTED:
                intent = new Intent(this, AssessmentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            //case ResidentlistAdapter.ResidentlistAdapterViewHolder.MEDICATIONS_SELECTED:
            //    startActivity(new Intent(this, MedicationsActivity.class));
            //    break;
            default:
                break;
        }
        //Intent intent = new Intent(this, DetailActivity.class)
        //        .setData(contentUri);
    }

}
