package com.android.janice.nursehelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 * Created by janicerichards on 2/4/17.
 */

public class MedicationsActivity extends AppCompatActivity implements MedicationsFragment.Callback {

    private MedicationsFragment mFragment;
    private static final String LOG_TAG = MedicationsActivity.class.getSimpleName();

    private String mRoomNumber, mPortraitFilePath, mNurseName, mDbUserId;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            mRoomNumber = getIntent().getStringExtra(MainActivity.ITEM_ROOM_NUMBER);
            mPortraitFilePath = getIntent().getStringExtra(MainActivity.ITEM_PORTRAIT_FILEPATH);
            mNurseName = getIntent().getStringExtra(MainActivity.ITEM_NURSE_NAME);
            mDbUserId = getIntent().getStringExtra(MainActivity.ITEM_USER_ID);

            arguments.putString(MainActivity.ITEM_ROOM_NUMBER, mRoomNumber);
            arguments.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, mPortraitFilePath);
            arguments.putString(MainActivity.ITEM_NURSE_NAME, mNurseName);
            arguments.putString(MainActivity.ITEM_USER_ID, mDbUserId);
            FragmentManager fm = getSupportFragmentManager();
            mFragment = (MedicationsFragment) fm.findFragmentById(R.id.medications_container);

            if (mFragment == null) {
                mFragment = new MedicationsFragment();
                mFragment.setArguments(arguments);
                fm.beginTransaction()
                        .add(R.id.medications_container, mFragment)
                        .commit();
            }
            // animation mode
            supportPostponeEnterTransition();

        } else {
            mRoomNumber = savedInstanceState.getString(MainActivity.ITEM_ROOM_NUMBER);
            mPortraitFilePath = savedInstanceState.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
            mNurseName = savedInstanceState.getString(MainActivity.ITEM_NURSE_NAME);
            mDbUserId = savedInstanceState.getString(MainActivity.ITEM_USER_ID);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.medslist, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.list_past_medications) {
            Intent intent;
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.ITEM_ROOM_NUMBER, mRoomNumber);
            bundle.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, mPortraitFilePath);
            intent = new Intent(this, MedsGivenActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(MainActivity.ITEM_ROOM_NUMBER, mRoomNumber);
        savedInstanceState.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, mPortraitFilePath);
        savedInstanceState.putString(MainActivity.ITEM_NURSE_NAME, mNurseName);
        savedInstanceState.putString(MainActivity.ITEM_USER_ID, mDbUserId);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRoomNumber = savedInstanceState.getString(MainActivity.ITEM_ROOM_NUMBER);
        mPortraitFilePath = savedInstanceState.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
        mNurseName = savedInstanceState.getString(MainActivity.ITEM_NURSE_NAME);
        mDbUserId = savedInstanceState.getString(MainActivity.ITEM_USER_ID);
    }

    @Override
    public void onItemSelected(String roomNumber, MedicationsAdapter.MedicationsAdapterViewHolder vh) {
        //NOT USED YET
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }
}
