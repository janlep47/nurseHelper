package com.android.janice.nursehelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class MedicationsFragment  extends ListFragment {
    MedicationsAdapter mAdapter;
    View mLoadingPanel;
    Context mContext;
    List<MedicationItem> mMedList = new ArrayList<>();
    //TextView mProblemText;

    String mRoomNumber;

    //boolean mAddProblem = false;

    private static final String LOG_TAG = MedicationsFragment.class.getSimpleName();
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

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_meds, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActivity = activity;

        Toolbar toolbarView = (Toolbar) root.findViewById(R.id.toolbar);
        mLoadingPanel = (View) root.findViewById(R.id.loadingPanel);

        activity.supportStartPostponedEnterTransition();


        ListView mListView = (ListView) root.findViewById(android.R.id.list);
        //mProblemText = (TextView) root.findViewById(R.id.add_stock_problem);

        mContext = getContext();
        mAdapter = new MedicationsAdapter(mContext,android.R.layout.simple_list_item_2, mMedList);
        mListView.setAdapter(mAdapter);

        new GetMedicationsListTask().execute(mRoomNumber);

        return root;
    }


    // List item click means this medication given/refused.
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MedicationItem item = mAdapter.getItem(position);
        //new MedicationTask().execute(item.getGenericName());
    }







    // AsyncTask<Params, Progress, Result>
    // Params - what you pass to the AsyncTask
    // Progress - if you have any updates; passed to onProgressUpdate()
    // Result - the output; returned by doInBackground()
    //
    private class GetMedicationsListTask extends AsyncTask<String, Void, Cursor> {
        @Override
        protected Cursor doInBackground(String... params) {
            String roomNumber = params[0];
            Uri uri = ResidentContract.MedicationEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(roomNumber).build();
            Cursor cursor = getContext().getContentResolver().query(uri,
                    new String[]{ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER,
                            ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC,
                            ResidentContract.MedicationEntry.COLUMN_NAME_TRADE,
                            ResidentContract.MedicationEntry.COLUMN_DOSAGE,
                            ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS,
                            ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE,
                            ResidentContract.MedicationEntry.COLUMN_FREQUENCY,
                            ResidentContract.MedicationEntry.COLUMN_TIMES},
                    null, null, ResidentContract.MedicationEntry.COLUMN_NAME_TRADE + " ASC");
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
                    MedicationItem item = new MedicationItem(result);
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
