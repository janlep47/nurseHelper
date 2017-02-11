package com.android.janice.nursehelper;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity  implements ResidentlistFragment.Callback {

    private ResidentlistFragment mFragment;

    public static final String ITEM_ROOM_NUMBER = "roomNumber";
    public static final String ITEM_PORTRAIT_FILEPATH = "portraitFilePath";
    public static final String ITEM_NURSE_NAME = "nurseName";

    private String mNurseName;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // TEMP!!
        mNurseName = "A. Baker";

        mFragment = ((ResidentlistFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_residentlist));

        //ResidentItem.putInDummyData(this);
        //MedicationItem.putInDummyData(this);
        //AssessmentItem.putInDummyData(this);

        //ResidentSyncAdapter.initializeSyncAdapter(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.residentlist, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about)     {
            AboutDialogFragment dialog = new AboutDialogFragment();
            dialog.show(getSupportFragmentManager(),"About:");
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
