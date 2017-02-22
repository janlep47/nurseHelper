package com.android.janice.nursehelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.janice.nursehelper.data.ResidentContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janicerichards on 2/5/17.
 */

public class PastAssessmentsFragment  extends ListFragment {
    PastAssessmentsAdapter mAdapter;
    View mLoadingPanel;
    Context mContext;
    List<AssessmentItem> mAssessmentList = new ArrayList<>();
    //TextView mProblemText;

    String mRoomNumber, mPortraitFilePath;


    private static final String LOG_TAG = PastAssessmentsFragment.class.getSimpleName();
    AppCompatActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mRoomNumber = arguments.getString(MainActivity.ITEM_ROOM_NUMBER);
            mPortraitFilePath = arguments.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
        }

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_past_assessments, container, false);
        mLoadingPanel = (View) root.findViewById(R.id.loadingPanel);

/*
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActivity = activity;

        Toolbar toolbarView = (Toolbar) root.findViewById(R.id.toolbar);
        mLoadingPanel = (View) root.findViewById(R.id.loadingPanel);

        activity.supportStartPostponedEnterTransition();

        // We need to start the enter transition after the data has loaded
        //if ( mTransitionAnimation ) {
        activity.supportStartPostponedEnterTransition();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setSubtitle("room: "+mRoomNumber);



        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);

        // Calculate ActionBar height
        int actionBarHeight = AssessmentFragment.DEFAULT_ACTION_BAR_HEIGHT;
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        Picasso.with(getActivity())
                .load("file:///android_asset/"+mPortraitFilePath)
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
*/



        ListView mListView = (ListView) root.findViewById(android.R.id.list);
        //mProblemText = (TextView) root.findViewById(R.id.add_stock_problem);

        mContext = getContext();
        mAdapter = new PastAssessmentsAdapter(mContext,android.R.layout.simple_list_item_2, mAssessmentList);
        mListView.setAdapter(mAdapter);


        new PastAssessmentsFragment.GetAssessmentListTask().execute(mRoomNumber);



        return root;
    }


    // List item click means this Assessment given/refused.
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AssessmentItem item = mAdapter.getItem(position);
        //new AssessmentTask().execute(item.getGenericName());
    }







    // AsyncTask<Params, Progress, Result>
    // Params - what you pass to the AsyncTask
    // Progress - if you have any updates; passed to onProgressUpdate()
    // Result - the output; returned by doInBackground()
    //
    private class GetAssessmentListTask extends AsyncTask<String, Void, Cursor> {
        @Override
        protected Cursor doInBackground(String... params) {
            String roomNumber = params[0];
            Uri uri = ResidentContract.AssessmentEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(roomNumber).build();
            Cursor cursor = getContext().getContentResolver().query(uri,
                    new String[]{ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER,
                            ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE,
                            ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE,
                            ResidentContract.AssessmentEntry.COLUMN_PULSE,
                            ResidentContract.AssessmentEntry.COLUMN_RR,
                            ResidentContract.AssessmentEntry.COLUMN_EDEMA,
                            ResidentContract.AssessmentEntry.COLUMN_EDEMA_LOCN,
                            ResidentContract.AssessmentEntry.COLUMN_EDEMA_PITTING,
                            ResidentContract.AssessmentEntry.COLUMN_PAIN,
                            ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS,
                            ResidentContract.AssessmentEntry.COLUMN_TIME},
                    null, null, ResidentContract.AssessmentEntry.COLUMN_TIME + " ASC");
            return cursor;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingPanel.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(Cursor result) {
            if (result != null && result.moveToFirst()) {
                do {
                    AssessmentItem item = new AssessmentItem(result);
                    mAssessmentList.add(item);
                } while (result.moveToNext());
            }

            mAdapter.data = mAssessmentList;
            // invalidate the list adapter:
            mAdapter.notifyDataSetChanged();
            mLoadingPanel.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }


}
