package com.android.janice.nursehelper;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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



            // new stuff
            //AppCompatActivity activity = (AppCompatActivity) getActivity();
            //mActivity = activity;

            //Toolbar toolbarView = (Toolbar) root.findViewById(R.id.toolbar);
            //mLoadingPanel = (View) root.findViewById(R.id.loadingPanel);


            // We need to start the enter transition after the data has loaded
            //if ( mTransitionAnimation ) {
            //supportStartPostponedEnterTransition();
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setSubtitle("room: "+roomNumber);



            actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            ImageView imageView = new ImageView(actionBar.getThemedContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);

            // Calculate ActionBar height
            int actionBarHeight = AssessmentFragment.DEFAULT_ACTION_BAR_HEIGHT;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }

            Picasso.with(this)
                    .load("file:///android_asset/"+portraitFilePath)
                    .placeholder(R.drawable.blank_portrait)
                    //.noFade().resize(actionBar.getHeight(), actionBar.getHeight())
                    .noFade().resize(actionBarHeight, actionBarHeight)
                    .error(R.drawable.blank_portrait)
                    .into(imageView);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                    | Gravity.CENTER_VERTICAL);
            layoutParams.rightMargin = 40;
            imageView.setLayoutParams(layoutParams);
            actionBar.setCustomView(imageView);

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
