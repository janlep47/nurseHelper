package com.android.janice.nursehelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/*
 * Created by janicerichards on 2/4/17.
 */

public class MedsGivenActivity extends AppCompatActivity {

    private MedsGivenFragment mFragment;
    private static final String LOG_TAG = MedicationsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds_given);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            String roomNumber = getIntent().getStringExtra(MainActivity.ITEM_ROOM_NUMBER);
            String portraitFilePath = getIntent().getStringExtra(MainActivity.ITEM_PORTRAIT_FILEPATH);
            arguments.putString(MainActivity.ITEM_ROOM_NUMBER, roomNumber);
            arguments.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, portraitFilePath);

            mFragment = new MedsGivenFragment();
            mFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.meds_given_container, mFragment)
                    .commit();

            // animation mode
            supportPostponeEnterTransition();
        }
    }

}
