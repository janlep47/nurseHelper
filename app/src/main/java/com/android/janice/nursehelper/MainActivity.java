package com.android.janice.nursehelper;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


import com.android.janice.nursehelper.sync.MedCheckSyncAdapter;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,
        ResidentlistFragment.Callback {

    public static final String ITEM_ROOM_NUMBER = "roomNumber";
    public static final String ITEM_PORTRAIT_FILEPATH = "portraitFilePath";
    public static final String ITEM_NURSE_NAME = "nurseName";

    private String mNurseName;

    private SignInButton mSignInButton;
    private ResidentlistFragment mFragment;

    private GoogleApiClient mGoogleApiClient;



    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;


    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_sign_in);
        // Set the dimensions of the sign-in button.
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.  DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(this)
                .build();


        //mFragment = ((ResidentlistFragment)getSupportFragmentManager()
        //        .findFragmentById(R.id.fragment_residentlist));



        ResidentItem.putInDummyData(this);
        MedicationItem.putInDummyData(this);
        AssessmentItem.putInDummyData(this);


        //MedCheckSyncAdapter.initializeSyncAdapter(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }


    private void signIn() {
        //mGoogleApiClient.clearDefaultAccountAndReconnect();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    mGoogleApiClient.disconnect();
                }
            });

        }
        //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                        }
                    });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            //result.startResolutionForResult(this, // your activity
            //        RC_SIGN_IN);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            mNurseName = acct.getDisplayName();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean ok) {
        if (ok) {
            // Manually  delete the sign-in button:
            mSignInButton.removeAllViews();

            final ViewGroup rootViewGroup = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);

            getLayoutInflater().inflate(R.layout.activity_main,   rootViewGroup);

            mFragment = ((ResidentlistFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_residentlist));


            MedCheckSyncAdapter.initializeSyncAdapter(this);
        } else {
            // EITHER reprompt for user id/pw OR ABORT THIS APP
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.residentlist, menu);
        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            signOut();
            mGoogleApiClient.disconnect();
        }
        Log.e(LOG_TAG," stopped and mGoogleApiClient DISCONNECTED");
        super.onStop();
    }



    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        //if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
        //    Log.d(LOG_TAG, "onConnected: " + connectionHint);
        //}
        Log.e(LOG_TAG, "onConnected: " + connectionHint);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
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
