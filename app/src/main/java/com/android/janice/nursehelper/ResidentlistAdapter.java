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
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by janicerichards on 2/2/17.
 */

public class ResidentlistAdapter extends RecyclerView.Adapter<ResidentlistAdapter.ResidentlistAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private ResidentlistAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;

    private static int THUMBNAIL_SIZE =150;

    public static final String LOG_TAG = ResidentlistAdapter.class.getSimpleName();
    /**
     * Cache of the children views for a residentlist list item.
     */
    public class ResidentlistAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mRoomNumberView;
        public final ImageView mPortraitView;
        public final TextView mMeds;
        public final TextView mAssessment;
        public final TextView mCarePlan;
        public final static int PATIENT_SELECTED = 0;
        public final static int MEDICATIONS_SELECTED = 1;
        public final static int ASSESSMENT_SELECTED = 2;
        public final static int CARE_PLAN_SELECTED = 3;


        public ResidentlistAdapterViewHolder(View view) {
            super(view);
            mRoomNumberView = (TextView) view.findViewById(R.id.list_item_room_number_textview);
            mPortraitView = (ImageView) view.findViewById(R.id.list_item_portrait_imageview);
            mMeds = (TextView) view.findViewById(R.id.list_item_medications_textview);
            mAssessment = (TextView) view.findViewById(R.id.list_item_assessment_textview);
            mCarePlan = (TextView) view.findViewById(R.id.list_item_careplan_textview);
            mMeds.setOnClickListener(this);
            mAssessment.setOnClickListener(this);
            mCarePlan.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int roomNumberColumnIndex = mCursor.getColumnIndex(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER);
            int portraitIndex = mCursor.getColumnIndex(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH);

            int selectionType = PATIENT_SELECTED;

            if (v == mMeds) selectionType = MEDICATIONS_SELECTED;
            else if (v == mAssessment) selectionType = ASSESSMENT_SELECTED;
            else if (v == mCarePlan) selectionType = CARE_PLAN_SELECTED;


            mClickHandler.onClick(mCursor.getString(roomNumberColumnIndex),
                    mCursor.getString(portraitIndex),
                    selectionType, this);
            mICM.onClick(this);
        }
    }

    public static interface ResidentlistAdapterOnClickHandler {
        void onClick(String roomNumber, String portraitFilePath, int selectionType, ResidentlistAdapterViewHolder vh);
    }

    public ResidentlistAdapter(Context context, ResidentlistAdapterOnClickHandler dh, View emptyView, int choiceMode) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    /*
        This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
        RecyclerView that will be used to contain the view, so that it can get the current
        ItemSelectionManager from the view.

        One could implement this pattern without modifying RecyclerView by taking advantage
        of the view tag to store the ItemChoiceManager.
     */
    @Override
    public ResidentlistAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            int layoutId = R.layout.list_item_residents;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ResidentlistAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ResidentlistAdapterViewHolder residentlistAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        // Read roomNumber from cursor
        String roomNumber = mCursor.getString(ResidentlistFragment.COL_ROOM_NUMBER);  // actually a string (e.g. "200a")
        residentlistAdapterViewHolder.mRoomNumberView.setText(roomNumber);
        residentlistAdapterViewHolder.mRoomNumberView.setContentDescription(mContext.getString(R.string.a11y_roomNumber, roomNumber));

        // Read filename of resident's portrait from cursor
        String filePath = mCursor.getString(ResidentlistFragment.COL_PORTRAIT);

        Picasso.with(mContext)
                .load("file:///android_asset/"+filePath)
                .placeholder(R.drawable.blank_portrait)
                .noFade().resize(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                .error(R.drawable.blank_portrait)
                .into(residentlistAdapterViewHolder.mPortraitView);
        mICM.onBindViewHolder(residentlistAdapterViewHolder, position);
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
        if ( viewHolder instanceof ResidentlistAdapterViewHolder ) {
            ResidentlistAdapterViewHolder vfh = (ResidentlistAdapterViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

}
