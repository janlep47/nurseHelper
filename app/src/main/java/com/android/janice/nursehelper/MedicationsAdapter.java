package com.android.janice.nursehelper;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.janice.nursehelper.data.ResidentContract;
import com.squareup.picasso.Picasso;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by janicerichards on 2/2/17.
 */

public class MedicationsAdapter extends RecyclerView.Adapter<MedicationsAdapter.MedicationsAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private MedicationsAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;
    final private String mRoomNumber;

    public static final String LOG_TAG = MedicationsAdapter.class.getSimpleName();
    /**
     * Cache of the children views for a Medications list item.
     */
    public class MedicationsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                        CompoundButton.OnCheckedChangeListener {
        public final TextView mGenericNameView;
        public final TextView mTradeNameView;
        public final TextView mDosageView;
        public final TextView mDosageUnitsView;
        public final TextView mDosageRouteView;
        public final TextView mFrequencyView;
        public final TextView mAdminTimesView;
        public final TextView mLastDateGivenView;
        public final TextView mLastTimeGivenView;
        public final TextView mLastGivenByView;

        public final TextView mGiveLabelView;
        public final CheckBox mGiveBox;
        public final TextView mRefuseLabelView;
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
            mLastDateGivenView = (TextView) view.findViewById(R.id.medication_last_given_date_textview);
            mLastTimeGivenView = (TextView) view.findViewById(R.id.medication_last_given_time_textview);
            mLastGivenByView = (TextView) view.findViewById(R.id.medication_last_given_nurse_textview);

            mGiveLabelView = (TextView) view.findViewById(R.id.give_med_textview);
            mGiveBox = (CheckBox) view.findViewById(R.id.give_med_checkbox);
            mRefuseLabelView = (TextView) view.findViewById(R.id.refuse_med_textview);
            mRefuseBox = (CheckBox) view.findViewById(R.id.refuse_med_checkbox);

            mGiveBox.setOnCheckedChangeListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String genericName = mCursor.getString(MedicationsFragment.COL_GENERIC);
            float dosage = mCursor.getFloat(MedicationsFragment.COL_DOSAGE);
            if (button == mGiveBox) {
                if (isChecked) {
                    // Medication given for the current med
                    MedicationItem.medGiven(mRoomNumber, genericName, dosage);
                } else {
                    // nurse made a MISTAKE (?) and unchecked the box ...
                    //  probably pressed by mistake, ask if she wants to undo the "give" record!!
                    MedicationItem.askUndoMedGiven(mRoomNumber, genericName, dosage);
                }
            } else if (button == mRefuseBox) {
                if (isChecked) {
                    // Medication refused for the current med
                    MedicationItem.medRefused(mRoomNumber, genericName, dosage);
                } else {
                    // nurse made a MISTAKE (?) and unchecked the box ...
                    //  probably pressed by mistake, ask if she wants to undo the "refuse" record!!
                    MedicationItem.askUndoMedRefused(mRoomNumber, genericName, dosage);
                }
            }

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            //int roomNumberColumnIndex = mCursor.getColumnIndex(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER);


            mClickHandler.onClick(mRoomNumber, this);
            mICM.onClick(this);
        }
    }

    public static interface MedicationsAdapterOnClickHandler {
        void onClick(String roomNumber, MedicationsAdapterViewHolder vh);
    }

    public MedicationsAdapter(Context context, MedicationsAdapterOnClickHandler dh, View emptyView, int choiceMode,
                              String roomNumber) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
        mRoomNumber = roomNumber;
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
        if ( viewGroup instanceof RecyclerView ) {
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

        medicationsAdapterViewHolder.mGenericNameView.setText(genericName);
        medicationsAdapterViewHolder.mTradeNameView.setText(tradeName);
        medicationsAdapterViewHolder.mDosageView.setText(String.valueOf(dosage));
        medicationsAdapterViewHolder.mDosageUnitsView.setText(dosageUnits);
        medicationsAdapterViewHolder.mDosageRouteView.setText(dosageRoute);
        medicationsAdapterViewHolder.mFrequencyView.setText(freq);
        medicationsAdapterViewHolder.mAdminTimesView.setText(adminTimes);

        mICM.onBindViewHolder(medicationsAdapterViewHolder, position);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
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

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof MedicationsAdapterViewHolder ) {
            MedicationsAdapterViewHolder vfh = (MedicationsAdapterViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

}