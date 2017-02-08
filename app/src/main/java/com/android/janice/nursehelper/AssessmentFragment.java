package com.android.janice.nursehelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.janice.nursehelper.data.ResidentContract;
import com.squareup.picasso.Picasso;
//import com.android.janice.nursehelper.sync.NurseHelperSyncAdapter;

/**
 * Created by janicerichards on 2/2/17.
 */

public class AssessmentFragment extends Fragment {
    public static final String LOG_TAG = AssessmentFragment.class.getSimpleName();
    private NumberPicker mSystolicBP_picker;
    private NumberPicker mDiastolicBP_picker;
    private NumberPicker mTemp_int_picker;
    private NumberPicker mTemp_decimal_picker;
    private TextView mSystolicBP_textView;
    private TextView mDiastolicBP_textView;
    private TextView mTemperature_textView;

    String mRoomNumber;
    String mPortraitFilePath;

    public static final int DEFAULT_ACTION_BAR_HEIGHT = 60;



    private static final String[] ASSESSMENT_COLUMNS = {
            ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER,
            ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE,
            ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE,
            ResidentContract.AssessmentEntry.COLUMN_PULSE,
            ResidentContract.AssessmentEntry.COLUMN_RR,
            ResidentContract.AssessmentEntry.COLUMN_EDEMA,
            ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS,
            ResidentContract.AssessmentEntry.COLUMN_TIME
};



    public AssessmentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mRoomNumber = arguments.getString(MainActivity.ITEM_ROOM_NUMBER);
            mPortraitFilePath = arguments.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
            Log.e(LOG_TAG, "mPortraitFilePath is "+mPortraitFilePath);
        }


        //View rootView = inflater.inflate(R.layout.fragment_assessment, container, false);
        View rootView = inflater.inflate(R.layout.item_assessment, container, false);

        mSystolicBP_textView = (TextView) rootView.findViewById(R.id.bp_systolic_textview);
        mDiastolicBP_textView = (TextView) rootView.findViewById(R.id.bp_diastolic_textview);
        mTemperature_textView = (TextView) rootView.findViewById(R.id.temperature_textview);

        mSystolicBP_picker = (NumberPicker) rootView.findViewById(R.id.bp_systolic_numberpicker);
        mDiastolicBP_picker = (NumberPicker) rootView.findViewById(R.id.bp_diastolic_numberpicker);
        mTemp_int_picker = (NumberPicker) rootView.findViewById(R.id.temp_int_numberpicker);
        mTemp_decimal_picker = (NumberPicker) rootView.findViewById(R.id.temp_decimal_numberpicker);

        mSystolicBP_picker.setValue(120);
        mSystolicBP_picker.setMinValue(60);
        mSystolicBP_picker.setMaxValue(250);

        mDiastolicBP_picker.setValue(80);
        mDiastolicBP_picker.setMinValue(40);
        mDiastolicBP_picker.setMaxValue(160);

        mSystolicBP_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE)
                    mSystolicBP_textView.setText(String.valueOf(mSystolicBP_picker.getValue()));
            }
        });

        mDiastolicBP_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE)
                    mDiastolicBP_textView.setText(String.valueOf(mDiastolicBP_picker.getValue()));
            }
        });

        mTemp_int_picker.setMinValue(95);
        mTemp_int_picker.setMaxValue(106);
        mTemp_int_picker.setValue(98);
        mTemp_decimal_picker.setMinValue(0);
        mTemp_decimal_picker.setMaxValue(9);
        mTemp_decimal_picker.setMaxValue(7);

        mTemperature_textView.setText("98.7");

        NumberPicker.OnScrollListener tempScrollListener = new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE)
                    mTemperature_textView.setText(String.valueOf(mTemp_int_picker.getValue())+"."+
                        String.valueOf(mTemp_decimal_picker.getValue()));
            }
        };

        mTemp_int_picker.setOnScrollListener(tempScrollListener);
        mTemp_decimal_picker.setOnScrollListener(tempScrollListener);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // We need to start the enter transition after the data has loaded
        //if ( mTransitionAnimation ) {
        activity.supportStartPostponedEnterTransition();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setSubtitle("room: "+mRoomNumber);



        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);

        // Calculate ActionBar height
        int actionBarHeight = DEFAULT_ACTION_BAR_HEIGHT;
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

        return rootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        super.onSaveInstanceState(outState);
    }

}
