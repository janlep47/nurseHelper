package com.android.janice.nursehelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by janicerichards on 2/4/17.
 */

public class MedicationsActivity  extends AppCompatActivity  implements MedicationsFragment.Callback {

    MedicationsFragment mFragment;
    private static final String LOG_TAG = MedicationsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            String roomNumber = getIntent().getStringExtra("roomNumber");
            arguments.putString("roomNumber", roomNumber);


            mFragment = new MedicationsFragment();
            mFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.medications_container, mFragment)
                    .commit();

            //mFragment = ((MedicationsFragment)getSupportFragmentManager()
            //        .findFragmentById(R.id.fragment_medications));
            //mFragment.setArguments(arguments);

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