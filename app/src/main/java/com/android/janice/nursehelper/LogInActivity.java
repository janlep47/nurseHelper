package com.android.janice.nursehelper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.util.Log;
import android.os.Build;
import android.content.Intent;
import android.widget.Toast;


import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by janicerichards on 3/12/17.
 */

public class LogInActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private final String LOG_TAG = LogInActivity.class.getSimpleName();

    private SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseAuth.AuthStateListener mAuthListener;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    private String mNurseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set the dimensions of the sign-in button.
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(this)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    //FirebaseUser user = firebaseAuth.getCurrentUser();
                    mUser = firebaseAuth.getCurrentUser();
                    if (mUser != null) {
                        // User is signed in
                        Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                        // Can now exit, and go back to calling activity (Main)
                        /*
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        } else {
                            finish();
                            System.exit(0);
                        }
                        */
                    } else {
                        // User is signed out
                        Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                // force the user to choose an account ... only problem is how to force entering
                //  the password?  (this doesn't really help yet)
                //mGoogleApiClient.clearDefaultAccountAndReconnect();
                signIn();
                break;
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            mNurseName = acct.getDisplayName();
            firebaseAuthWithGoogle(acct);
        } else {
            // Signed out, show unauthenticated UI.
        }
    }


    /*
    "If the call to signInWithCredential succeeds, the AuthStateListener runs the
    onAuthStateChanged callback. In the callback, you can use the getCurrentUser method
    to get the user's account data."  ... but looking at logcat, onComplete finishes after
    onAuthStateChanged, so launching MainActivity in onComplete() instead of onAuthStateChanged().
    */

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        } else {
                            mNurseName = mUser.getDisplayName();

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Bundle bundle = new Bundle();
                            bundle.putString(MainActivity.ITEM_NURSE_NAME,mNurseName);
                            bundle.putString(MainActivity.ITEM_USER_ID, mUser.getUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }


    //private void exitLogin() {
    //    this.finishAffinity();
    //}


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            FirebaseAuth.getInstance().signOut();
            mGoogleApiClient.disconnect();
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, " googleapiclient disconnected");
            }
        }
    }



    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
           Log.d(LOG_TAG, "onConnected: " + connectionHint);
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
}

