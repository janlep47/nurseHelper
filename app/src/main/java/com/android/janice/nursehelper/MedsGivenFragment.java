package com.android.janice.nursehelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.janice.nursehelper.data.ResidentContract;
/**
 * Created by janicerichards on 2/4/17.
 */

public class MedsGivenFragment  extends ListFragment {
    MedsGivenAdapter mAdapter;
    View mLoadingPanel;
    Context mContext;
    List<MedGivenItem> mMedList = new ArrayList<>();
    //TextView mProblemText;

    String mRoomNumber;

    //boolean mAddProblem = false;

    private static final String LOG_TAG = MedsGivenFragment.class.getSimpleName();
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
            mRoomNumber = arguments.getString("roomNumber");
        }

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_meds_given, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActivity = activity;

        Toolbar toolbarView = (Toolbar) root.findViewById(R.id.toolbar);
        mLoadingPanel = (View) root.findViewById(R.id.loadingPanel);

        activity.supportStartPostponedEnterTransition();


        ListView mListView = (ListView) root.findViewById(android.R.id.list);
        //mProblemText = (TextView) root.findViewById(R.id.add_stock_problem);

        mContext = getContext();
        mAdapter = new MedsGivenAdapter(mContext,android.R.layout.simple_list_item_2, mMedList);
        mListView.setAdapter(mAdapter);


        //new PastAssessmentsFragment.GetAssessmentListTask().execute(mRoomNumber);


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
            Log.i(LOG_TAG, " ********  room # is "+roomNumber);
            Uri uri = ResidentContract.MedsGivenEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(roomNumber).build();
            Cursor cursor = getContext().getContentResolver().query(uri,
                    new String[]{ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER,
                            ResidentContract.MedsGivenEntry.COLUMN_NAME_GENERIC,
                            ResidentContract.MedsGivenEntry.COLUMN_DOSAGE,
                            ResidentContract.MedsGivenEntry.COLUMN_DOSAGE_UNITS,
                            ResidentContract.MedsGivenEntry.COLUMN_GIVEN,
                            ResidentContract.MedsGivenEntry.COLUMN_TIME_GIVEN},
                    null, null, ResidentContract.MedsGivenEntry.COLUMN_TIME_GIVEN + " ASC");
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
                    MedGivenItem item = new MedGivenItem(result);
                    mMedList.add(item);
                } while (result.moveToNext());
            }

            mAdapter.data = mMedList;

            Log.i(LOG_TAG, "  ********** here ************" );
            for (int i = 0; i < mMedList.size(); i++) {
                MedGivenItem item = mMedList.get(i);
                Log.i(LOG_TAG,"   *** medgiven:   room#="+item.getRoomNumber()+"  med="+item.getGenericName());
            }

            // invalidate the list adapter:
            mAdapter.notifyDataSetChanged();
            mLoadingPanel.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }


}
