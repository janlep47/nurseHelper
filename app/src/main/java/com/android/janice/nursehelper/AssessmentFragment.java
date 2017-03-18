package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.janice.nursehelper.data.ResidentContract;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
    private NumberPicker mPulse_picker;
    private NumberPicker mRR_picker;
    private Spinner mEdema_spinner;
    //private Spinner mEdema_location_spinner;
    private NumberPicker mPain_picker;
    private CheckBox mEdema_LLE, mEdema_RLE, mEdema_LUE, mEdema_RUE;
    private RadioButton mEdema_pitting, mEdema_non_pitting;

    private TextView mSystolicBP_textView;
    private TextView mDiastolicBP_textView;
    private TextView mTemperature_textView;
    private TextView mPulse_textView;
    private TextView mRR_textView;
    private TextView mEdema_textView;
    private TextView mEdemaLocation_textView;
    private TextView mPain_textView;
    private EditText mFindings_editText;
    private Button mDone_button;

    String mRoomNumber;
    String mPortraitFilePath;
    String mNurseName;
    String mDbUserId;
    DatabaseReference mDatabase;

    private boolean edemaPitting = false;

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


    public interface Callback {
        // for when a list item has been selected.
        //public void onItemSelected(Uri dateUri, int selectionType, MedicationsAdapter.MedicationsAdapterViewHolder vh);
        public DatabaseReference getDatabaseReference();
    }


    public AssessmentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mRoomNumber = savedInstanceState.getString(MainActivity.ITEM_ROOM_NUMBER);
            mPortraitFilePath = savedInstanceState.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
            mNurseName = savedInstanceState.getString(MainActivity.ITEM_NURSE_NAME);
            mDbUserId = savedInstanceState.getString(MainActivity.ITEM_USER_ID);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mRoomNumber = arguments.getString(MainActivity.ITEM_ROOM_NUMBER);
                mPortraitFilePath = arguments.getString(MainActivity.ITEM_PORTRAIT_FILEPATH);
                mNurseName = arguments.getString(MainActivity.ITEM_NURSE_NAME);
                mDbUserId = arguments.getString(MainActivity.ITEM_USER_ID);

            }
        }
        mDatabase = ((Callback) getActivity()).getDatabaseReference();
        // NOTE!! 'assessments' data which is changed on the central Firebase database is NOT VALID, so
        //  will be ignored.  That data may ONLY come for the nurses personal device, as she logs it.
        //  If a mistake was made, it can be noted later, but never deleted!!

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

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_assessment, container, false);

        mSystolicBP_textView = (TextView) rootView.findViewById(R.id.bp_systolic_textview);
        mDiastolicBP_textView = (TextView) rootView.findViewById(R.id.bp_diastolic_textview);
        mTemperature_textView = (TextView) rootView.findViewById(R.id.temperature_textview);
        mPulse_textView = (TextView) rootView.findViewById(R.id.pulse_textview);
        mRR_textView =  (TextView) rootView.findViewById(R.id.rr_textview);
        mEdema_textView = (TextView) rootView.findViewById(R.id.edema_textview);
        mPain_textView = (TextView) rootView.findViewById(R.id.pain_textview);
        mFindings_editText = (EditText) rootView.findViewById(R.id.findings_edittext);

        mSystolicBP_picker = (NumberPicker) rootView.findViewById(R.id.bp_systolic_numberpicker);
        mDiastolicBP_picker = (NumberPicker) rootView.findViewById(R.id.bp_diastolic_numberpicker);
        mTemp_int_picker = (NumberPicker) rootView.findViewById(R.id.temp_int_numberpicker);
        mTemp_decimal_picker = (NumberPicker) rootView.findViewById(R.id.temp_decimal_numberpicker);
        mPulse_picker = (NumberPicker) rootView.findViewById(R.id.pulse_numberpicker);
        mRR_picker = (NumberPicker) rootView.findViewById(R.id.rr_numberpicker);
        mEdema_spinner = (Spinner) rootView.findViewById(R.id.edema_spinner);
        //mEdema_location_spinner = (Spinner) rootView.findViewById(R.id.edema_locn_spinner);
        mEdema_LLE = (CheckBox) rootView.findViewById(R.id.edema_LLE);
        mEdema_RLE = (CheckBox) rootView.findViewById(R.id.edema_RLE);
        mEdema_LUE = (CheckBox) rootView.findViewById(R.id.edema_LUE);
        mEdema_RUE = (CheckBox) rootView.findViewById(R.id.edema_RUE);
        mEdema_pitting = (RadioButton) rootView.findViewById(R.id.edema_pitting);
        mEdema_non_pitting = (RadioButton) rootView.findViewById(R.id.edema_non_pitting);
        mPain_picker = (NumberPicker) rootView.findViewById(R.id.pain_picker);
        mDone_button = (Button) rootView.findViewById(R.id.done_button);

        mSystolicBP_picker.setMinValue(60);
        mSystolicBP_picker.setMaxValue(250);
        mSystolicBP_picker.setValue(120);
        mSystolicBP_textView.setText(String.valueOf(mSystolicBP_picker.getValue()));
        mSystolicBP_picker.setWrapSelectorWheel(false);

        mDiastolicBP_picker.setMinValue(40);
        mDiastolicBP_picker.setMaxValue(160);
        mDiastolicBP_textView.setText(String.valueOf(mDiastolicBP_picker.getValue()));
        mDiastolicBP_picker.setValue(80);
        mDiastolicBP_picker.setWrapSelectorWheel(false);

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
        mTemp_int_picker.setWrapSelectorWheel(false);
        mTemp_decimal_picker.setMinValue(0);
        mTemp_decimal_picker.setMaxValue(9);
        mTemp_decimal_picker.setValue(7);
        mTemp_decimal_picker.setWrapSelectorWheel(false);

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

        mPulse_picker.setMinValue(20);
        mPulse_picker.setMaxValue(200);
        mPulse_picker.setValue(60);
        mPulse_textView.setText(String.valueOf(mPulse_picker.getValue()));
        mPulse_picker.setWrapSelectorWheel(false);

        mPulse_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE)
                    mPulse_textView.setText(String.valueOf(mPulse_picker.getValue()));
            }
        });

        mRR_picker.setMinValue(6);
        mRR_picker.setMaxValue(70);
        mRR_picker.setValue(14);
        mRR_textView.setText(String.valueOf(mRR_picker.getValue()));
        mRR_picker.setWrapSelectorWheel(false);

        mRR_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE)
                    mRR_textView.setText(String.valueOf(mRR_picker.getValue()));
            }
        });


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.edema_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mEdema_spinner.setAdapter(adapter);

        mEdema_pitting.setOnClickListener(new RadioButton.OnClickListener() {
            public void onClick(View view) {
                edemaPitting = ((RadioButton) view).isChecked();
            }
        });
        mEdema_non_pitting.setOnClickListener(new RadioButton.OnClickListener() {
            public void onClick(View view) {
                edemaPitting = !((RadioButton) view).isChecked();
            }
        });

        /*
        // Do the same for the edemaLocationSpinner:
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.edema_locn_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEdema_location_spinner.setAdapter(adapter);
        */

        mPain_picker.setMinValue(0);
        mPain_picker.setMaxValue(10);
        mPain_picker.setValue(0);
        mPain_textView.setText(String.valueOf(mPain_picker.getValue()));
        mPain_picker.setWrapSelectorWheel(false);

        mPain_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE)
                    mPain_textView.setText(String.valueOf(mPain_picker.getValue()));
            }
        });

        rootView.invalidate();

        //mEdemaLocation_textView;

        mDone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int systolicBP, diastolicBP, pulse, rr, pain;
                float temp;
                systolicBP = Integer.parseInt((String) mSystolicBP_textView.getText());
                diastolicBP = Integer.parseInt((String) mDiastolicBP_textView.getText());
                temp = Float.parseFloat((String) mTemperature_textView.getText());
                pulse = Integer.parseInt((String) mPulse_textView.getText());
                rr = Integer.parseInt((String) mRR_textView.getText());
                pain = Integer.parseInt((String) mPain_textView.getText());
                String findings = mFindings_editText.getText().toString();
                String edemaLocations = "";
                if (mEdema_spinner.getSelectedItemPosition() != 0) {
                    if (mEdema_LLE.isChecked()) edemaLocations += " LLE";
                    if (mEdema_LUE.isChecked()) edemaLocations += " LUE";
                    if (mEdema_RLE.isChecked()) edemaLocations += " RLE";
                    if (mEdema_RUE.isChecked()) edemaLocations += " RUE";
                }
                boolean edemaPitting = false;
                AssessmentItem.saveAssessment(getActivity(), mRoomNumber, systolicBP, diastolicBP, temp, pulse, rr,
                        //(String) mEdema_spinner.getSelectedItem(), (String) mEdema_location_spinner.getSelectedItem(),
                        (String) mEdema_spinner.getSelectedItem(), edemaLocations, edemaPitting,
                        pain, findings, mDatabase, mDbUserId);
                getActivity().finish();
            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        super.onSaveInstanceState(outState);
        outState.putString(MainActivity.ITEM_ROOM_NUMBER, mRoomNumber);
        outState.putString(MainActivity.ITEM_PORTRAIT_FILEPATH, mPortraitFilePath);
        outState.putString(MainActivity.ITEM_NURSE_NAME,mNurseName);
        outState.putString(MainActivity.ITEM_USER_ID,mDbUserId);
    }

}
