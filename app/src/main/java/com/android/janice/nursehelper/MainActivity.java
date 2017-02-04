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

public class MainActivity extends AppCompatActivity  implements ResidentlistFragment.Callback {

    private ResidentlistFragment mFragment;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFragment = ((ResidentlistFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_residentlist));

        ResidentItem.putInDummyData(this);

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
    public void onItemSelected(Uri contentUri, ResidentlistAdapter.ResidentlistAdapterViewHolder vh) {
        Log.i(LOG_TAG, " .. ok, this item selected: "+contentUri.toString());
        //Intent intent = new Intent(this, DetailActivity.class)
        //        .setData(contentUri);

        //ActivityCompat.startActivity(this, intent, null);

    }
}
