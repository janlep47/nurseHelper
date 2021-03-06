package com.android.janice.nursehelper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.database.DatabaseReference;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.janice.nursehelper.utility.Utility;

/*
 * Created by janicerichards on 2/2/17.
 */

public class MedicationsAdapter extends RecyclerView.Adapter<MedicationsAdapter.MedicationsAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    //final private MedicationsAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    //final private ItemChoiceManager mICM;
    final private String mRoomNumber;
    final private String mNurseName;

    final private DatabaseReference mDatabase;
    final private String mDbUserId;


    public static final String LOG_TAG = MedicationsAdapter.class.getSimpleName();

    /**
     * Cache of the children views for a Medications list item.
     */
    public class MedicationsAdapterViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener {
        public final TextView mGenericNameView;
        public final TextView mTradeNameView;
        public final TextView mDosageView;
        public final TextView mDosageUnitsView;
        public final TextView mDosageRouteView;
        public final TextView mFrequencyView;
        public final TextView mAdminTimesView;
        public final TextView mLastTimeGivenView;
        public final TextView mNextDueTimeView;

        public final CheckBox mGiveBox;
        public final CheckBox mRefuseBox;


        public MedicationsAdapterViewHolder(View view) {
            super(view);

            mGenericNameView = (TextView) view.findViewById(R.id.medication_generic_name_textview);
            mTradeNameView = (TextView) view.findViewById(R.id.medication_trade_name_textview);
            mDosageView = (TextView) view.findViewById(R.id.medication_dosage_textview);
            mDosageUnitsView = (TextView) view.findViewById(R.id.medication_dosage_units_textview);
            mDosageRouteView = (TextView) view.findViewById(R.id.medication_dosage_route_textview);
            mFrequencyView = (TextView) view.findViewById(R.id.medication_frequency_textview);
            mAdminTimesView = (TextView) view.findViewById(R.id.medication_admin_times_textview);
            mLastTimeGivenView = (TextView) view.findViewById(R.id.medication_last_given_time_textview);
            mNextDueTimeView = (TextView) view.findViewById(R.id.medication_due_time_textview);

            mGiveBox = (CheckBox) view.findViewById(R.id.give_med_checkbox);
            mRefuseBox = (CheckBox) view.findViewById(R.id.refuse_med_checkbox);

            mGiveBox.setOnCheckedChangeListener(this);
            mRefuseBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            if (button == mGiveBox) {
                if (isChecked && !mRefuseBox.isChecked()) {
                    // Medication given for the current med
                    MedicationItem.medGiven(mContext, mCursor, mRoomNumber, mNurseName, true, mDatabase, mDbUserId);
                } else if (!isChecked && !mRefuseBox.isChecked()) {
                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.undo_med_given_dialog_title)
                            .setMessage(R.string.undo_med_given_dialog_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MedicationItem.undoMedGiven(mContext, mCursor, mRoomNumber, mNurseName,
                                            mDatabase, mDbUserId);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User did not mean to uncheck the 'give' box, so just show it as
                                    //   checked again:
                                    mGiveBox.setChecked(true);
                                }
                            })
                            .create();
                    dialog.show();
                } else if (isChecked && mRefuseBox.isChecked()) {
                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.invalid_med_given_dialog_title)
                            .setMessage(R.string.invalid_med_given_dialog_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mGiveBox.setChecked(false);
                                }
                            })
                            .create();
                    dialog.show();
                }
            } else if (button == mRefuseBox) {
                if (isChecked && !mGiveBox.isChecked()) {
                    // Medication refused for the current med
                    MedicationItem.medGiven(mContext, mCursor, mRoomNumber, mNurseName, false, mDatabase, mDbUserId);
                } else if (!isChecked && !mGiveBox.isChecked()) {
                    // nurse made a MISTAKE (?) and unchecked the box ...
                    //  probably pressed by mistake, ask if she wants to undo the "refuse" record!!
                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.undo_med_refused_dialog_title)
                            .setMessage(R.string.undo_med_refused_dialog_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MedicationItem.undoMedRefused(mContext, mCursor, mRoomNumber, mNurseName, mDatabase, mDbUserId);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User did not mean to uncheck the 'refuse' box, so just show it as
                                    //   checked again:
                                    mRefuseBox.setChecked(true);
                                }
                            })
                            .create();
                    dialog.show();
                } else if (isChecked && mGiveBox.isChecked()) {
                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.invalid_med_refused_dialog_title)
                            .setMessage(R.string.invalid_med_refused_dialog_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mRefuseBox.setChecked(false);
                                }
                            })
                            .create();
                    dialog.show();
                }
            }

        }
    }

    public interface MedicationsAdapterOnClickHandler {
        void onClick(String roomNumber, MedicationsAdapterViewHolder vh);
    }

    public MedicationsAdapter(Context context, MedicationsAdapterOnClickHandler dh, View emptyView,
                              String roomNumber, String nurseName, DatabaseReference database, String userId) {
        mContext = context;
        //mClickHandler = dh;
        mEmptyView = emptyView;
        //mICM = new ItemChoiceManager(this);
        //mICM.setChoiceMode(choiceMode);
        mRoomNumber = roomNumber;
        mNurseName = nurseName;
        mDatabase = database;
        mDbUserId = userId;
    }

    /*
        This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
        RecyclerView that will be used to contain the view, so that it can get the current
        ItemSelectionManager from the view.

        One could implement this pattern without modifying RecyclerView by taking advantage
        of the view tag to store the ItemChoiceManager.
     */
    @Override
    public MedicationsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = R.layout.list_item_medications;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new MedicationsAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MedicationsAdapterViewHolder medicationsAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        // Read the medication data from cursor
        String genericName = mCursor.getString(MedicationsFragment.COL_GENERIC);
        String tradeName = mCursor.getString(MedicationsFragment.COL_TRADE);
        float dosage = mCursor.getFloat(MedicationsFragment.COL_DOSAGE);
        String dosageUnits = mCursor.getString(MedicationsFragment.COL_DOSAGE_UNITS);
        String dosageRoute = mCursor.getString(MedicationsFragment.COL_DOSAGE_ROUTE);
        String freq = mCursor.getString(MedicationsFragment.COL_FREQUENCY);
        String adminTimes = mCursor.getString(MedicationsFragment.COL_ADMIN_TIMES);
        long lastGivenTime = mCursor.getLong(MedicationsFragment.COL_LAST_GIVEN);
        String nextTimeToGive = mCursor.getString(MedicationsFragment.COL_NEXT_GIVE_TIME);

        String formattedDate;
        if (!Utility.timeIsNull(lastGivenTime))
            formattedDate = Utility.getReadableTimestamp(mContext, lastGivenTime);
        else
            formattedDate = mContext.getResources().getString(R.string.med_not_given_yet);
        medicationsAdapterViewHolder.mGenericNameView.setText("   (" + genericName + ")");
        medicationsAdapterViewHolder.mTradeNameView.setText(tradeName);
        medicationsAdapterViewHolder.mDosageView.setText(String.valueOf(dosage));
        String space = mContext.getResources().getString(R.string.space);
        medicationsAdapterViewHolder.mDosageUnitsView.setText(space + dosageUnits);
        medicationsAdapterViewHolder.mDosageRouteView.setText(dosageRoute);
        medicationsAdapterViewHolder.mFrequencyView.setText(freq);
        medicationsAdapterViewHolder.mAdminTimesView.setText(adminTimes);
        medicationsAdapterViewHolder.mLastTimeGivenView.setText(formattedDate);
        medicationsAdapterViewHolder.mNextDueTimeView.setText(nextTimeToGive);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

}
