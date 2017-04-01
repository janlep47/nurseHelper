package com.android.janice.nursehelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/*
 * Created by janicerichards on 2/5/17.
 */

public class PastAssessmentsActivity extends AppCompatActivity {

    private PastAssessmentsFragment mFragment;
    private static final String LOG_TAG = PastAssessmentsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_assessments);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            String roomNumber = getIntent().getStringExtra(MainActivity.ITEM_ROOM_NUMBER);
            String portraitFilePath = getIntent().getStringExtra(MainActivity.ITEM_PORTRAIT_FILEPATH);
            arguments.putString(MainActivity.ITEM_ROOM_NUMBER, roomNumber);
            arguments.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, portraitFilePath);

            mFragment = new PastAssessmentsFragment();
            mFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.assessment_container, mFragment)
                    .commit();
            // animation mode
            supportPostponeEnterTransition();
        }
    }
}
