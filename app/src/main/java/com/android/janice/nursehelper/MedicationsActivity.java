package com.android.janice.nursehelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by janicerichards on 2/4/17.
 */

public class MedicationsActivity  extends AppCompatActivity  implements MedicationsFragment.Callback {

    MedicationsFragment mFragment;
    private static final String LOG_TAG = MedicationsActivity.class.getSimpleName();

    String mRoomNumber, mPortraitFilePath, mNurseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            mRoomNumber = getIntent().getStringExtra(MainActivity.ITEM_ROOM_NUMBER);
            mPortraitFilePath = getIntent().getStringExtra(MainActivity.ITEM_PORTRAIT_FILEPATH);
            mNurseName = getIntent().getStringExtra(MainActivity.ITEM_NURSE_NAME);

            arguments.putString(MainActivity.ITEM_ROOM_NUMBER, mRoomNumber);
            arguments.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, mPortraitFilePath);
            arguments.putString(MainActivity.ITEM_NURSE_NAME, mNurseName);
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
            bundle.putString(MainActivity.ITEM_ROOM_NUMBER,mRoomNumber);
            bundle.putString(MainActivity.ITEM_PORTRAIT_FILEPATH,mPortraitFilePath);
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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRoomNumber = savedInstanceState.getString(MainActivity.ITEM_ROOM_NUMBER);
        mPortraitFilePath = savedInstanceState.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
        mNurseName = savedInstanceState.getString(MainActivity.ITEM_NURSE_NAME);
    }

    @Override
    public void onItemSelected(String roomNumber, MedicationsAdapter.MedicationsAdapterViewHolder vh) {
        //Intent intent;
        //Bundle bundle = new Bundle();
        //bundle.putString("roomNumber",roomNumber);
        //intent = new Intent(this, DisplayMedicationActivity.class);
        //intent.putExtras(bundle);
        //startActivity(intent);
    }


}
