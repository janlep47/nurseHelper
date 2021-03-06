package com.android.janice.nursehelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.android.janice.nursehelper.data.ResidentContract;
import com.squareup.picasso.Picasso;

/*
 * Created by janicerichards on 2/4/17.
 */

public class MedsGivenFragment extends ListFragment {
    private MedsGivenAdapter mAdapter;
    private View mLoadingPanel;
    private Context mContext;
    private final List<MedGivenItem> mMedList = new ArrayList<>();

    private String mRoomNumber, mPortraitFilePath;


    private static final String LOG_TAG = MedsGivenFragment.class.getSimpleName();
    private AppCompatActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mRoomNumber = arguments.getString(MainActivity.ITEM_ROOM_NUMBER);
            mPortraitFilePath = arguments.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
        }

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_meds_given, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActivity = activity;

        //Toolbar toolbarView = (Toolbar) root.findViewById(R.id.toolbar);
        mLoadingPanel = root.findViewById(R.id.loadingPanel);

        // We need to start the enter transition after the data has loaded
        //if ( mTransitionAnimation ) {
        activity.supportStartPostponedEnterTransition();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setSubtitle(activity.getResources().getString(R.string.action_bar_room_number_title) + mRoomNumber);


            actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            ImageView imageView = new ImageView(actionBar.getThemedContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);

            // Calculate ActionBar height
            int actionBarHeight = getActivity().getResources().getInteger(R.integer.appbar_default_height);
            TypedValue tv = new TypedValue();
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }
            int targetWidth = actionBarHeight;
            Picasso.with(getActivity())
                    .load(mPortraitFilePath)
                    .placeholder(R.drawable.blank_portrait)
                    //.noFade().resize(actionBar.getHeight(), actionBar.getHeight())
                    .noFade().resize(targetWidth, actionBarHeight)
                    .error(R.drawable.blank_portrait)
                    .into(imageView);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT, Gravity.END
                    | Gravity.CENTER_VERTICAL);
            layoutParams.rightMargin = mActivity.getResources().getInteger(R.integer.appbar_portrait_margin);
            imageView.setLayoutParams(layoutParams);
            actionBar.setCustomView(imageView);
        }

        ListView mListView = (ListView) root.findViewById(android.R.id.list);
        TextView emptyView = (TextView) root.findViewById(R.id.list_meds_given_empty);

        mContext = getContext();
        mAdapter = new MedsGivenAdapter(mContext, android.R.layout.simple_list_item_2, mMedList, emptyView);
        mListView.setAdapter(mAdapter);

        new GetMedsGivenListTask().execute(mRoomNumber);

        return root;
    }


    // AsyncTask<Params, Progress, Result>
    // Params - what you pass to the AsyncTask
    // Progress - if you have any updates; passed to onProgressUpdate()
    // Result - the output; returned by doInBackground()
    //
    private class GetMedsGivenListTask extends AsyncTask<String, Void, Cursor> {
        @Override
        protected Cursor doInBackground(String... params) {
            String roomNumber = params[0];
            Uri uri = ResidentContract.MedsGivenEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(roomNumber).build();
            return getContext().getContentResolver().query(uri,
                    new String[]{ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER,
                            ResidentContract.MedsGivenEntry.COLUMN_NAME_GENERIC,
                            ResidentContract.MedsGivenEntry.COLUMN_DOSAGE,
                            ResidentContract.MedsGivenEntry.COLUMN_DOSAGE_UNITS,
                            ResidentContract.MedsGivenEntry.COLUMN_GIVEN,
                            ResidentContract.MedsGivenEntry.COLUMN_NURSE,
                            ResidentContract.MedsGivenEntry.COLUMN_TIME_GIVEN},
                    null, null, ResidentContract.MedsGivenEntry.COLUMN_TIME_GIVEN + " ASC");
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
                    MedGivenItem item = new MedGivenItem(result);
                    mMedList.add(item);
                } while (result.moveToNext());
            }
            mAdapter.data = mMedList;

            // invalidate the list adapter:
            mAdapter.notifyDataSetChanged();
            mLoadingPanel.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }


}
