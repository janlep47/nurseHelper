package com.android.janice.nursehelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;

import com.android.janice.nursehelper.data.NurseHelperPreferences;
import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.sync.MedCheckSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements ResidentlistFragment.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int REQUEST_CODE = 123;
    public static final String ITEM_ROOM_NUMBER = "roomNumber";
    public static final String ITEM_PORTRAIT_FILEPATH = "portraitFilepath";
    public static final String ITEM_NURSE_NAME = "nurseName";
    public static final String ITEM_USER_ID = "dataBaseUserID";
    public static final String NO_CARE_PLAN_PDF = "CarePlanNONE.pdf";

    public static final String ITEM_TEMP_UNITS = "tempUnits";

    public static final String ITEM_COUNT = "count";
    public static final String ITEM_DELETE_AMT = "deleteAmt";

    private String mNurseName;
    private String mDbUserId;

    private GoogleApiClient mGoogleApiClient;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mNurseName = getIntent().getStringExtra(ITEM_NURSE_NAME);
            mDbUserId = getIntent().getStringExtra(ITEM_USER_ID);
        } else {
            mNurseName = savedInstanceState.getString(ITEM_NURSE_NAME);
            mDbUserId = savedInstanceState.getString(ITEM_USER_ID);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ResidentlistFragment mFragment = ((ResidentlistFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_residentlist));

        ResidentItem.putInDummyData(this, mDatabase, mDbUserId);
        MedicationItem.putInDummyData(this, mDatabase, mDbUserId);
        AssessmentItem.putInDummyData(this, mDatabase, mDbUserId);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        MedCheckSyncAdapter.initializeSyncAdapter(this);
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

        LocationRequest mLocationRequest = LocationRequest.create();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.d(LOG_TAG," No rights to check device location!! \n"+
                    e.toString());
        }

    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!FacilityLocation.isDeviceAtFacility(location.getLatitude(), location.getLongitude())) {
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
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        super.onSaveInstanceState(outState);
        outState.putString(MainActivity.ITEM_NURSE_NAME, mNurseName);
        outState.putString(MainActivity.ITEM_USER_ID, mDbUserId);
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
            dialog.show(getSupportFragmentManager(),getResources().getString(R.string.action_about_title));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemSelected(String roomNumber, String portraitFilePath, String careplanFilePath,
                               int selectionType, ResidentlistAdapter.ResidentlistAdapterViewHolder vh) {
        Intent intent;
        Bundle bundle = new Bundle();
        bundle.putString(ITEM_ROOM_NUMBER,roomNumber);
        bundle.putString(ITEM_PORTRAIT_FILEPATH,portraitFilePath);
        bundle.putString(ITEM_NURSE_NAME,mNurseName);
        bundle.putString(ITEM_USER_ID,mDbUserId);

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
            case ResidentlistAdapter.ResidentlistAdapterViewHolder.CARE_PLAN_SELECTED:
                intent = new Intent(this, CareplanActivity.class);
                //intent.putExtras(bundle);
                if (careplanFilePath == null)
                    careplanFilePath = NO_CARE_PLAN_PDF;
                else if (careplanFilePath.length() == 0)
                    careplanFilePath = NO_CARE_PLAN_PDF;
                intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, getAssetsPdfPath(careplanFilePath));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public String getAssetsPdfPath(String assetsFile) {
        String filePath = getFilesDir() + File.separator + assetsFile;
        File destinationFile = new File(filePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            InputStream inputStream = getAssets().open(assetsFile);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: "+e.toString());
        }

        return destinationFile.getPath();
    }

    public String getNurseName() { return mNurseName; }

    public String getDbUserId() { return mDbUserId; }

    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }
}
