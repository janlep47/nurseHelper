package com.android.janice.nursehelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by janicerichards on 2/5/17.
 */

public class PastAssessmentsActivity extends AppCompatActivity {

    PastAssessmentsFragment mFragment;
    private static final String LOG_TAG = PastAssessmentsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_assessments);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            String roomNumber = getIntent().getStringExtra("roomNumber");
            arguments.putString("roomNumber", roomNumber);

            mFragment = new PastAssessmentsFragment();
            mFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.assessment_container, mFragment)
                    .commit();
            // animation mode
            supportPostponeEnterTransition();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.medsedit, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        if (id == R.id.action_add) {
            startActivity(new Intent(this, AddMedicationActivity.class));
            mFragment.dataUpdated();
            return true;
        } else if (id == R.id.action_delete) {
            startActivity(new Intent(this, DeleteMedicationActivity.class));
            mFragment.dataUpdated();
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

}
