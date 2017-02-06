package com.android.janice.nursehelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.janice.nursehelper.data.ResidentContract;
//import com.android.janice.nursehelper.sync.NurseHelperSyncAdapter;

/**
 * Created by janicerichards on 2/2/17.
 */

public class MedicationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String LOG_TAG = MedicationsFragment.class.getSimpleName();
    private MedicationsAdapter mMedicationsAdapter;
    private RecyclerView mRecyclerView;
    private boolean mAutoSelectView;
    private int mChoiceMode;
    private boolean mHoldForTransition;

    String mRoomNumber;


    private String mInitialSelectedRoomNumber = "";

    private static final String SELECTED_KEY = "selected_position";

    private static final int MEDICATIONS_LOADER = 0;

    private static final String[] MEDICATIONS_COLUMNS = {
            ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER,
            ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC,
            ResidentContract.MedicationEntry.COLUMN_NAME_TRADE,
            ResidentContract.MedicationEntry.COLUMN_DOSAGE,
            ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS,
            ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE,
            ResidentContract.MedicationEntry.COLUMN_FREQUENCY,
            ResidentContract.MedicationEntry.COLUMN_TIMES
    };


    // These indices are tied to above.
    static final int COL_ROOM_NUMBER = 0;
    static final int COL_GENERIC = 1;
    static final int COL_TRADE = 2;
    static final int COL_DOSAGE = 3;
    static final int COL_DOSAGE_UNITS = 4;
    static final int COL_DOSAGE_ROUTE = 5;
    static final int COL_FREQUENCY = 6;
    static final int COL_ADMIN_TIMES = 7;


    public interface Callback {
        // for when a list item has been selected.
        //public void onItemSelected(Uri dateUri, int selectionType, MedicationsAdapter.MedicationsAdapterViewHolder vh);
        public void onItemSelected(String roomNumber, MedicationsAdapter.MedicationsAdapterViewHolder vh);
    }

    public MedicationsFragment() {
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

        Bundle arguments = getArguments();
        if (arguments != null) {
            mRoomNumber = arguments.getString("roomNumber");
            Log.e(LOG_TAG, "room# is "+mRoomNumber);
        }


        View rootView = inflater.inflate(R.layout.fragment_meds, container, false);

        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_medications);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = rootView.findViewById(R.id.recyclerview_medications_empty);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // The MedicationsAdapter will take data from a source and
        // use it to populate the RecyclerView it's attached to.
        mMedicationsAdapter = new MedicationsAdapter(getActivity(), new MedicationsAdapter.MedicationsAdapterOnClickHandler() {
            @Override
            //public void onClick(Long date, MedicationsAdapter.MedicationsAdapterViewHolder vh) {
            //    String locationSetting = Utility.getPreferredLocation(getActivity());
            public void onClick(String roomNumber, MedicationsAdapter.MedicationsAdapterViewHolder vh) {
                ((Callback) getActivity())
                        .onItemSelected(roomNumber,
                                vh
                        );
                /*
                ((Callback) getActivity())
                        .onItemSelected(ResidentContract.ResidentEntry.buildResidentInfoWithRoomNumber(
                                //locationSetting, date),
                                roomNumber),
                                selectionType,
                                vh
                        );
                        */
            }
        }, emptyView, mChoiceMode, mRoomNumber);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mMedicationsAdapter);


        // For when device is rotated
        if (savedInstanceState != null) {
            mMedicationsAdapter.onRestoreInstanceState(savedInstanceState);
        } else {
            //ResidentSyncAdapter.syncImmediately(getContext());
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // We hold for transition here just in-case the activity
        // needs to be re-created. In a standard return transition,
        // this doesn't actually make a difference.
        if ( mHoldForTransition ) {
            getActivity().supportPostponeEnterTransition();
        }
        getLoaderManager().initLoader(MEDICATIONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onMedicationsChanged() {getLoaderManager().restartLoader(MEDICATIONS_LOADER, null, this);}


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        mMedicationsAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // Sort order:  Ascending, by Generic Name.
        String sortOrder = ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC + " ASC";
        Uri uri = ResidentContract.MedicationEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mRoomNumber).build();

        return new CursorLoader(getActivity(),
                uri,
                MEDICATIONS_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMedicationsAdapter.swapCursor(data);
        updateEmptyView();
        if ( data.getCount() == 0 ) {
            getActivity().supportStartPostponedEnterTransition();
        } else {
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (mRecyclerView.getChildCount() > 0) {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int position = mMedicationsAdapter.getSelectedItemPosition();
                        //if (position == RecyclerView.NO_POSITION &&
                        //        -1 != mInitialSelectedDate) {
                        if (position == RecyclerView.NO_POSITION && !mInitialSelectedRoomNumber.equals("")) {
                            Cursor data = mMedicationsAdapter.getCursor();
                            int count = data.getCount();
                            //int dateColumn = data.getColumnIndex(ResidentsContract.ResidentEntry.COLUMN_DATE);
                            int roomNumberColumn = data.getColumnIndex(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER);
                            for (int i = 0; i < count; i++) {
                                data.moveToPosition(i);
                                //if ( data.getLong(dateColumn) == mInitialSelectedDate ) {
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
                            mMedicationsAdapter.selectView(vh);
                        }
                        if (mHoldForTransition) {
                            getActivity().supportStartPostponedEnterTransition();
                        }


                        AppCompatActivity activity = (AppCompatActivity) getActivity();
                        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);

                        // We need to start the enter transition after the data has loaded
                        //if ( mTransitionAnimation ) {
                        activity.supportStartPostponedEnterTransition();
/*
                        if (null != toolbarView) {
                            activity.setSupportActionBar(toolbarView);
                            //getActivity().getSupportActionBar().setTitle(mSymbol);
                            toolbarView.setTitle(R.string.title_activity_list);

                            //activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                            activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
                            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        }
                        */
                        //}

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
        mMedicationsAdapter.swapCursor(null);
    }

    public void setInitialSelectedRoomNumber(String initialSelectedRoomNumber) {
        mInitialSelectedRoomNumber = initialSelectedRoomNumber;
    }

    // Update the empty-list view if empty Resident DB or server down
    private void updateEmptyView() {
        if ( mMedicationsAdapter.getItemCount() == 0 ) {
            TextView tv = (TextView) getView().findViewById(R.id.recyclerview_medications_empty);
            if ( null != tv ) {


                /*
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_Medications;
                @ResidentSyncAdapter.Status int status = Utility.getStatus(getActivity());
                switch (status) {
                    case ResidentSyncAdapter.STATUS_SERVER_DOWN:
                        message = R.string.empty_Medications_server_down;
                        break;
                    default:
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_Medications_no_network;
                        }
                }
                */
                int message = R.string.empty_medications_no_network;   // FOR NOW ONLY!!!
                tv.setText(message);
            }
        }
    }

    public void dataUpdated() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_status_key))) {
            //Utility.resetStatus(getContext());
            updateEmptyView();
        }
    }

}
