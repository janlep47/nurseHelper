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
import android.text.Editable;
import android.text.TextWatcher;
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
    private static final String ITEM_SYSTOLIC_BP = "systolicBP";
    private static final String ITEM_DIASTOLIC_BP = "diastolicBP";
    private static final String ITEM_TEMP = "temp";
    private static final String ITEM_TEMP_DECIMAL = "tempDecimal";
    private static final String ITEM_PULSE = "pulse";
    private static final String ITEM_RR = "rr";
    private static final String ITEM_PAIN = "pain";
    private static final String ITEM_TEMP_STRING = "tempString";
    private static final String ITEM_FINDINGS = "findings";
    private static final String ITEM_EDEMA_SPINNER_POSN = "edemaPosn";
    private static final String ITEM_EDEMA_PITTING = "edemaPitting";
    private static final String ITEM_EDEMA_LLE_CHECKED = "lleChecked";
    private static final String ITEM_EDEMA_RLE_CHECKED = "rleChecked";
    private static final String ITEM_EDEMA_LUE_CHECKED = "lueChecked";
    private static final String ITEM_EDEMA_RUE_CHECKED = "rueChecked";

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

    private int mSystolicBP, mDiastolicBP, mTemp, mTempDecimal, mPulse, mRR,  mPain;
    private int edemaSpinnerPosn;
    private String mTempString, mFindings;
    private boolean edemaPitting, mEdema_LLE_checked, mEdema_RLE_checked, mEdema_LUE_checked, mEdema_RUE_checked;

    /*
    private static final String[] ASSESSMENT_COLUMNS = {
            ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER,
            ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE,
            ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE,
            ResidentContract.AssessmentEntry.COLUMN_PULSE,
            ResidentContract.AssessmentEntry.COLUMN_RR,
            ResidentContract.AssessmentEntry.COLUMN_EDEMA,
            ResidentContract.AssessmentEntry.COLUMN_EDEMA_LOCN,
            ResidentContract.AssessmentEntry.COLUMN_EDEMA_PITTING,
            ResidentContract.AssessmentEntry.COLUMN_PAIN,
            ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS,
            ResidentContract.AssessmentEntry.COLUMN_TIME
};
    */

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
        actionBar.setSubtitle(activity.getResources().getString(R.string.action_bar_room_number_title)+mRoomNumber);


        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);

        // Calculate ActionBar height
        int actionBarHeight = getActivity().getResources().getInteger(R.integer.appbar_default_height);
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        Picasso.with(getActivity())
                .load(mPortraitFilePath)
                .placeholder(R.drawable.blank_portrait)
                //.noFade().resize(actionBar.getHeight(), actionBar.getHeight())
                .noFade().resize(actionBarHeight, actionBarHeight)
                .error(R.drawable.blank_portrait)
                .into(imageView);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = getActivity().getResources().getInteger(R.integer.appbar_portrait_margin);
        imageView.setLayoutParams(layoutParams);
        actionBar.setCustomView(imageView);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_assessment, container, false);

        if (savedInstanceState != null) {
            mSystolicBP = savedInstanceState.getInt(ITEM_SYSTOLIC_BP);
            mDiastolicBP = savedInstanceState.getInt(ITEM_DIASTOLIC_BP);
            mTemp = savedInstanceState.getInt(ITEM_TEMP);
            mTempDecimal = savedInstanceState.getInt(ITEM_TEMP_DECIMAL);
            mPulse = savedInstanceState.getInt(ITEM_PULSE);
            mRR = savedInstanceState.getInt(ITEM_RR);
            mPain = savedInstanceState.getInt(ITEM_PAIN);
            mTempString = savedInstanceState.getString(ITEM_TEMP_STRING);
            mFindings = savedInstanceState.getString(ITEM_FINDINGS);
            edemaSpinnerPosn = savedInstanceState.getInt(ITEM_EDEMA_SPINNER_POSN);
            edemaPitting = savedInstanceState.getBoolean(ITEM_EDEMA_PITTING);
            mEdema_LLE_checked = savedInstanceState.getBoolean(ITEM_EDEMA_LLE_CHECKED);
            mEdema_RLE_checked = savedInstanceState.getBoolean(ITEM_EDEMA_RLE_CHECKED);
            mEdema_LUE_checked = savedInstanceState.getBoolean(ITEM_EDEMA_LUE_CHECKED);
            mEdema_RUE_checked = savedInstanceState.getBoolean(ITEM_EDEMA_RUE_CHECKED);
        } else {
            // not coming from a restored state, set all values to defaults to start
            mSystolicBP = getActivity().getResources().getInteger(R.integer.bp_systolic_default);
            mDiastolicBP = getActivity().getResources().getInteger(R.integer.bp_diastolic_default);
            mTemp = getActivity().getResources().getInteger(R.integer.temp_default);
            mTempDecimal = getActivity().getResources().getInteger(R.integer.temp_decimal_default);
            mTempString = getActivity().getResources().getString(R.string.temperature_default);
            mPulse = getActivity().getResources().getInteger(R.integer.pulse_default);
            mRR = getActivity().getResources().getInteger(R.integer.rr_default);
            edemaSpinnerPosn = 0;
            mEdema_LLE_checked = false;
            mEdema_RLE_checked = false;
            mEdema_LUE_checked = false;
            mEdema_RUE_checked = false;
            mPain = 0;
            mFindings = "";
            edemaPitting = false;
        }

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


        mFindings_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mFindings = mFindings_editText.getText().toString();
            }
        });

        mSystolicBP_picker.setMinValue(getActivity().getResources().getInteger(R.integer.bp_systolic_min));
        mSystolicBP_picker.setMaxValue(getActivity().getResources().getInteger(R.integer.bp_systolic_max));
        //mSystolicBP_picker.setValue(getActivity().getResources().getInteger(R.integer.bp_systolic_default));
        //mSystolicBP_textView.setText(String.valueOf(mSystolicBP_picker.getValue()));
        mSystolicBP_picker.setValue(mSystolicBP);
        mSystolicBP_textView.setText(String.valueOf(mSystolicBP));
        mSystolicBP_picker.setWrapSelectorWheel(false);

        mDiastolicBP_picker.setMinValue(getActivity().getResources().getInteger(R.integer.bp_diastolic_min));
        mDiastolicBP_picker.setMaxValue(getActivity().getResources().getInteger(R.integer.bp_diastolic_max));
        //mDiastolicBP_picker.setValue(getActivity().getResources().getInteger(R.integer.bp_diastolic_default));
        //mDiastolicBP_textView.setText(String.valueOf(mDiastolicBP_picker.getValue()));
        mDiastolicBP_picker.setValue(mDiastolicBP);
        mDiastolicBP_textView.setText(String.valueOf(mDiastolicBP));
        mDiastolicBP_picker.setWrapSelectorWheel(false);

        mSystolicBP_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    mSystolicBP = mSystolicBP_picker.getValue();
                    mSystolicBP_textView.setText(String.valueOf(mSystolicBP));
                }
            }
        });

        mDiastolicBP_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    mDiastolicBP = mDiastolicBP_picker.getValue();
                    mDiastolicBP_textView.setText(String.valueOf(mDiastolicBP));
                }
            }
        });

        mTemp_int_picker.setMinValue(getActivity().getResources().getInteger(R.integer.temp_min));
        mTemp_int_picker.setMaxValue(getActivity().getResources().getInteger(R.integer.temp_max));
        //mTemp_int_picker.setValue(getActivity().getResources().getInteger(R.integer.temp_default));
        mTemp_int_picker.setValue(mTemp);
        mTemp_int_picker.setWrapSelectorWheel(false);

        mTemp_decimal_picker.setMinValue(getActivity().getResources().getInteger(R.integer.temp_decimal_min));
        mTemp_decimal_picker.setMaxValue(getActivity().getResources().getInteger(R.integer.temp_decimal_max));
        //mTemp_decimal_picker.setValue(getActivity().getResources().getInteger(R.integer.temp_decimal_default));
        mTemp_decimal_picker.setValue(mTempDecimal);
        mTemp_decimal_picker.setWrapSelectorWheel(false);

        //mTemperature_textView.setText(getActivity().getResources().getString(R.string.temperature_default));
        mTemperature_textView.setText(mTempString);

        NumberPicker.OnScrollListener tempScrollListener = new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    mTemp = mTemp_int_picker.getValue();
                    mTempDecimal = mTemp_decimal_picker.getValue();
                    mTempString = String.valueOf(mTemp) + "." +
                            String.valueOf(mTempDecimal);
                    mTemperature_textView.setText(mTempString);
                }
            }
        };

        mTemp_int_picker.setOnScrollListener(tempScrollListener);
        mTemp_decimal_picker.setOnScrollListener(tempScrollListener);

        mPulse_picker.setMinValue(getActivity().getResources().getInteger(R.integer.pulse_min));
        mPulse_picker.setMaxValue(getActivity().getResources().getInteger(R.integer.pulse_max));
        //mPulse_picker.setValue(getActivity().getResources().getInteger(R.integer.pulse_default));
        //mPulse_textView.setText(String.valueOf(mPulse_picker.getValue()));
        mPulse_picker.setValue(mPulse);
        mPulse_textView.setText(String.valueOf(mPulse));
        mPulse_picker.setWrapSelectorWheel(false);

        mPulse_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    mPulse = mPulse_picker.getValue();
                    mPulse_textView.setText(String.valueOf(mPulse));
                }
            }
        });

        mRR_picker.setMinValue(getActivity().getResources().getInteger(R.integer.rr_min));
        mRR_picker.setMaxValue(getActivity().getResources().getInteger(R.integer.rr_max));
        //mRR_picker.setValue(getActivity().getResources().getInteger(R.integer.rr_default));
        //mRR_textView.setText(String.valueOf(mRR_picker.getValue()));
        mRR_picker.setValue(mRR);
        mRR_textView.setText(String.valueOf(mRR));
        mRR_picker.setWrapSelectorWheel(false);

        mRR_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    mRR = mRR_picker.getValue();
                    mRR_textView.setText(String.valueOf(mRR));
                }
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

        if (edemaSpinnerPosn != 0)
            mEdema_spinner.setSelection(edemaSpinnerPosn);
        if (mEdema_LLE_checked) mEdema_LLE.setChecked(true);
        if (mEdema_RLE_checked) mEdema_RLE.setChecked(true);
        if (mEdema_LUE_checked) mEdema_LUE.setChecked(true);
        if (mEdema_RUE_checked) mEdema_RUE.setChecked(true);

        /*
        // Do the same for the edemaLocationSpinner:
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.edema_locn_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEdema_location_spinner.setAdapter(adapter);
        */

        mPain_picker.setMinValue(getActivity().getResources().getInteger(R.integer.pain_min));
        mPain_picker.setMaxValue(getActivity().getResources().getInteger(R.integer.pain_max));
        //mPain_picker.setValue(getActivity().getResources().getInteger(R.integer.pain_default));
        //mPain_textView.setText(String.valueOf(mPain_picker.getValue()));
        mPain_picker.setValue(mPain);
        mPain_textView.setText(String.valueOf(mPain));
        mPain_picker.setWrapSelectorWheel(false);

        mPain_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    mPain = mPain_picker.getValue();
                    mPain_textView.setText(String.valueOf(mPain));
                }
            }
        });

        rootView.invalidate();

        //mEdemaLocation_textView;

        mDone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float temp = Float.parseFloat(mTempString);
                mFindings = mFindings_editText.getText().toString();
                String edemaLocations = "";
                if (mEdema_spinner.getSelectedItemPosition() != 0) {
                    if (mEdema_LLE.isChecked()) edemaLocations += " "+
                            getActivity().getResources().getString(R.string.edema_LLE);
                    if (mEdema_LUE.isChecked()) edemaLocations += " "+
                            getActivity().getResources().getString(R.string.edema_LUE);
                    if (mEdema_RLE.isChecked()) edemaLocations += " "+
                            getActivity().getResources().getString(R.string.edema_RLE);
                    if (mEdema_RUE.isChecked()) edemaLocations += " "+
                            getActivity().getResources().getString(R.string.edema_RUE);
                }
                AssessmentItem.saveAssessment(getActivity(), mRoomNumber, mSystolicBP, mDiastolicBP, temp,
                        mPulse, mRR,
                        (String) mEdema_spinner.getSelectedItem(), edemaLocations, edemaPitting,
                        mPain, mFindings, mDatabase, mDbUserId);
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

        outState.putInt(ITEM_SYSTOLIC_BP,mSystolicBP);
        outState.putInt(ITEM_DIASTOLIC_BP,mDiastolicBP);
        outState.putInt(ITEM_TEMP,mTemp);
        outState.putInt(ITEM_TEMP_DECIMAL,mTempDecimal);
        outState.putInt(ITEM_PULSE,mPulse);
        outState.putInt(ITEM_RR,mRR);
        outState.putInt(ITEM_PAIN,mPain);
        outState.putString(ITEM_TEMP_STRING,mTempString);
        outState.putString(ITEM_FINDINGS,mFindings);
        outState.putInt(ITEM_EDEMA_SPINNER_POSN, edemaSpinnerPosn);
        outState.putBoolean(ITEM_EDEMA_PITTING, edemaPitting);
        outState.putBoolean(ITEM_EDEMA_LLE_CHECKED, mEdema_LLE_checked);
        outState.putBoolean(ITEM_EDEMA_RLE_CHECKED, mEdema_RLE_checked);
        outState.putBoolean(ITEM_EDEMA_LUE_CHECKED, mEdema_LUE_checked);
        outState.putBoolean(ITEM_EDEMA_RUE_CHECKED, mEdema_RUE_checked);
    }

}
