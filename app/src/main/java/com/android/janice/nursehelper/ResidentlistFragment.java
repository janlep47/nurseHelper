package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.janice.nursehelper.data.ResidentContract;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/*
 * Created by janicerichards on 2/2/17.
 */

public class ResidentlistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = ResidentlistFragment.class.getSimpleName();
    private ResidentlistAdapter mResidentlistAdapter;
    private RecyclerView mRecyclerView;
    private boolean mAutoSelectView;
    private int mChoiceMode;
    private boolean mHoldForTransition;


    private String mInitialSelectedRoomNumber = "";

    //private static final String SELECTED_KEY = "selected_position";

    private static final int RESIDENTLIST_LOADER = 0;
    private static final int MEDTIME_LOADER = 1;

    private static final String[] RESIDENTLIST_COLUMNS = {
            ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER,
            ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH,
            ResidentContract.ResidentEntry.COLUMN_CAREPLAN_FILEPATH};

    // These indices are tied to above.
    static final int COL_ROOM_NUMBER = 0;
    static final int COL_PORTRAIT = 1;
    static final int COL_CAREPLAN = 2;

    // This index is tied to the timeCursor (index 0 is room number, in the same (asc) sorted
    //   order as room number in "cursor"
    static final int COL_NEXT_ADMIN_TIME = 1;
    static final int COL_NEXT_ADMIN_TIME_LONG = 2;

    private String mNurseName;
    private String mDbUserId;
    private DatabaseReference mDatabase;

    public interface Callback {
        // for when a list item has been selected.
        //public void onItemSelected(Uri dateUri, int selectionType, ResidentlistAdapter.ResidentlistAdapterViewHolder vh);
        void onItemSelected(String roomNumber, String portraitFilePath, String careplanFilePath,
                                   int selectionType, ResidentlistAdapter.ResidentlistAdapterViewHolder vh);
        String getNurseName();
        String getDbUserId();
        DatabaseReference getDatabaseReference();
    }

    public ResidentlistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResidentlistFragment,
                0, 0);
        mChoiceMode = a.getInt(R.styleable.ResidentlistFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE);
        mAutoSelectView = a.getBoolean(R.styleable.ResidentlistFragment_autoSelectView, false);
        mHoldForTransition = a.getBoolean(R.styleable.ResidentlistFragment_sharedElementTransitions, false);
        a.recycle();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_residentlist);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = rootView.findViewById(R.id.recyclerview_residentlist_empty);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // The ResidentlistAdapter will take data from a source and
        // use it to populate the RecyclerView it's attached to.
        mResidentlistAdapter = new ResidentlistAdapter(getActivity(), new ResidentlistAdapter.ResidentlistAdapterOnClickHandler() {
            @Override
            //public void onClick(Long date, ResidentlistAdapter.ResidentlistAdapterViewHolder vh) {
            //    String locationSetting = Utility.getPreferredLocation(getActivity());
            public void onClick(String roomNumber, String portraitFilePath, String careplanFilePath,
                                int selectionType, ResidentlistAdapter.ResidentlistAdapterViewHolder vh) {
                ((Callback) getActivity())
                        .onItemSelected(roomNumber,
                                portraitFilePath,
                                careplanFilePath,
                                selectionType,
                                vh
                        );
            }
        }, emptyView, mChoiceMode);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mResidentlistAdapter);


        // For when device is rotated
        if (savedInstanceState != null) {
            mResidentlistAdapter.onRestoreInstanceState(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // We hold for transition here just in-case the activity
        // needs to be re-created. In a standard return transition,
        // this doesn't actually make a difference.
        if (mHoldForTransition) {
            getActivity().supportPostponeEnterTransition();
        }
        getLoaderManager().initLoader(RESIDENTLIST_LOADER, null, this);
        getLoaderManager().initLoader(MEDTIME_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);

        mNurseName = ((Callback) getActivity()).getNurseName();
        mDbUserId = ((Callback) getActivity()).getDbUserId();
        mDatabase = ((Callback) getActivity()).getDatabaseReference();
        mDatabase.child("users").child(mDbUserId).child("residents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UpdateResidentsFromFirebaseTask updateResidentsTask =
                        new UpdateResidentsFromFirebaseTask(getActivity(), dataSnapshot);
                updateResidentsTask.execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, " Firebase database 'residents' onCancelled: " + databaseError.toString());
            }
        });
    }

    //void onResidentlistChanged() {
    //    getLoaderManager().restartLoader(RESIDENTLIST_LOADER, null, this);
    //}


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses two loaders, so we care about checking the id.
        if (i == RESIDENTLIST_LOADER) {
            // Sort order:  Ascending, by Resident symbol.
            String sortOrder = ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER + " ASC";

            return new CursorLoader(getActivity(),
                    ResidentContract.ResidentEntry.CONTENT_URI,
                    RESIDENTLIST_COLUMNS,
                    null,
                    null,
                    sortOrder);
        } else {  // MEDTIME_LOADER
            // This call will do a special raw-query, which will find the earliest (if any)
            //   time due for scheduled meds, for each resident.
            return new CursorLoader(getActivity(), ResidentContract.MedicationEntry.CONTENT_URI, null,
                    null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == MEDTIME_LOADER) {
            mResidentlistAdapter.swapTimeCursor(data);
            return;
        }
        // Otherwise, RESIDENT_LOADER
        mResidentlistAdapter.swapCursor(data);
        updateEmptyView();
        if (data.getCount() == 0) {
            getActivity().supportStartPostponedEnterTransition();
        } else {
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (mRecyclerView.getChildCount() > 0) {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int position = mResidentlistAdapter.getSelectedItemPosition();
                        if (position == RecyclerView.NO_POSITION && !mInitialSelectedRoomNumber.equals("")) {
                            Cursor data = mResidentlistAdapter.getCursor();

                            mResidentlistAdapter.getTimeCursor();

                            int count = data.getCount();
                            int roomNumberColumn = data.getColumnIndex(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER);
                            for (int i = 0; i < count; i++) {
                                data.moveToPosition(i);
                                if ((data.getString(roomNumberColumn)).equals(mInitialSelectedRoomNumber)) {
                                    position = i;
                                    break;
                                }
                            }
                        }
                        if (position == RecyclerView.NO_POSITION) position = 0;
                        // If we don't need to restart the loader, and there's a desired position to restore
                        // to, do so now.
                        mRecyclerView.smoothScrollToPosition(position);
                        RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(position);
                        if (null != vh && mAutoSelectView) {
                            mResidentlistAdapter.selectView(vh);
                        }
                        if (mHoldForTransition) {
                            getActivity().supportStartPostponedEnterTransition();
                        }


                        AppCompatActivity activity = (AppCompatActivity) getActivity();

                        // We need to start the enter transition after the data has loaded
                        //if ( mTransitionAnimation ) {
                        activity.supportStartPostponedEnterTransition();
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRecyclerView) {
            mRecyclerView.clearOnScrollListeners();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mResidentlistAdapter.swapCursor(null);
    }

    //public void setInitialSelectedRoomNumber(String initialSelectedRoomNumber) {
    //    mInitialSelectedRoomNumber = initialSelectedRoomNumber;
    //}

    // Update the empty-list view if empty Resident DB or server down
    private void updateEmptyView() {
        if (mResidentlistAdapter.getItemCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.recyclerview_residentlist_empty);
            if (tv != null) {
                int message = R.string.empty_residentlist_no_network;   // FOR NOW ONLY!!!
                tv.setText(message);
            }
        }
    }

    private void dataUpdated() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_status_key))) {
            //Utility.resetStatus(getContext());
            updateEmptyView();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mResidentlistAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    private class UpdateResidentsFromFirebaseTask extends AsyncTask<Void, Void, Void> {
        protected final Context context;
        protected final DataSnapshot dataSnapshot;

        public UpdateResidentsFromFirebaseTask(Context context, DataSnapshot dataSnapshot) {
            this.context = context;
            this.dataSnapshot = dataSnapshot;
        }


        @Override
        protected Void doInBackground(Void... params) {
            // NOTE there is a PROBLEM with firebase sending TOO MANY FALSE data changes ...
            //  ... in addition to real ones  .... could be expensive and causes annoying screen
            //  flicker with all the extra "changes" causing deletions and re-writes.

            //getActivity().getContentResolver().delete(ResidentContract.ResidentEntry.CONTENT_URI,null, null);
            if (dataSnapshot.exists()) {
                // Just delete ALL records in the device 'residents' table, and add the ones from the
                //  Firebase dataSnapshot:
                if (dataSnapshot.hasChild(ResidentContract.ResidentEntry.TABLE_NAME)) {
                    getActivity().getContentResolver().delete(ResidentContract.ResidentEntry.CONTENT_URI, null, null);
                } else {
                    return null;
                }


                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                    if (issue.exists()) {
                        ResidentItem resident = issue.getValue(ResidentItem.class);
                        ContentValues resValues = new ContentValues();
                        resValues.put(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER, resident.getRoomNumber());
                        resValues.put(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH, resident.getPortraitFilepath());
                        getActivity().getContentResolver().insert(ResidentContract.ResidentEntry.CONTENT_URI, resValues);
                    }
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            dataUpdated();
            super.onPostExecute(result);
        }
    }




/*
    ALTERNATE doInBackground()  instead of deleting ALL records (SINCE firebase is sending FALSE changes,
     and we want to prevent to many deletes and reloads (takes time, battery life, and causes resident list
     screen to flicker annoyingly on start-up), we'll just compare the snapshot to the current device content
     'residents' table and make any changes to that as needed (from the firebase snapshot).

    @Override
    protected Void doInBackground(Void... params) {
        // DO NOT delete, b/c this is called every time the app starts! (it shouldn't!) ... and it causes a pause
        //  and an annoying re-draw of the resident-list screen:
        if (dataSnapshot.exists()) {
            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                if (issue.exists()) {
                    ResidentItem resident = issue.getValue(ResidentItem.class);
                    // INSTEAD of inserting all the snapshot records, see if this one is defined in the 'residents' table
                    String roomNumber = resident.getRoomNumber();
                    String portraitFilepath = resident.getPortraitFilepath();
                    Uri testUri = ResidentContract.ResidentEntry.CONTENT_URI;
                    testUri = testUri.buildUpon().appendPath(roomNumber).build();
                    Cursor testCursor = context.getContentResolver().query(testUri,
                            new String[]{ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER},
                            null, null, null);

                    if (testCursor != null) {
                        if (testCursor.moveToFirst()) {
                            // This (room# == 'residents' key) resident ALREADY EXISTS;
                            //  if the portraitFilepath is the SAME, just skip it
                            if (portraitFilepath.equals(testCursor.getString(COL_PORTRAIT))) {
                                testCursor.close();
                                continue;  // This resident ALREADY EXISTS, skip to next one to add
                            } else {
                                // Need to update this record with the new portraitFilepath
                                ContentValues resValues = new ContentValues();
                                resValues.put(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH, portraitFilepath);

                                Uri uriResident = ResidentContract.ResidentEntry.CONTENT_URI;
                                uriResident = uriResident.buildUpon().appendPath(roomNumber).build();
                                int rowsUpdated = context.getContentResolver().update(uriResident, resValues,
                                        ResidentProvider.sResidentByRoomNumberSelection ,
                                        new String[]{roomNumber});
                            }
                        }
                        testCursor.close();
                    }
                }
            }
        }
        return null;
    }
*/

}
