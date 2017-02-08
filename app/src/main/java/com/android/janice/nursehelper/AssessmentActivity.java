package com.android.janice.nursehelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by janicerichards on 2/4/17.
 */

public class AssessmentActivity  extends AppCompatActivity  implements AssessmentFragment.Callback {

    AssessmentFragment mFragment;
    private static final String LOG_TAG = AssessmentActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            String roomNumber = getIntent().getStringExtra(MainActivity.ITEM_ROOM_NUMBER);
            String portraitFilePath = getIntent().getStringExtra(MainActivity.ITEM_PORTRAIT_FILEPATH);
            arguments.putString(MainActivity.ITEM_ROOM_NUMBER, roomNumber);
            arguments.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, portraitFilePath);


            mFragment = new AssessmentFragment();
            mFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.assessment_container, mFragment)
                    .commit();

            //mFragment = ((AssessmentFragment)getSupportFragmentManager()
            //        .findFragmentById(R.id.fragment_assessment));
            //mFragment.setArguments(arguments);

            // animation mode
            supportPostponeEnterTransition();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.assessmentslist, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.list_past_assessments) {
            Log.i(LOG_TAG,"chose 'list past meds'");
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemSelected(String roomNumber, AssessmentAdapter.AssessmentAdapterViewHolder vh) {
        //Intent intent;
        //Bundle bundle = new Bundle();
        //bundle.putString("roomNumber",roomNumber);
        //intent = new Intent(this, DisplayMedicationActivity.class);
        //intent.putExtras(bundle);
        //startActivity(intent);
    }


}
