package com.android.janice.nursehelper.widget;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.janice.nursehelper.R;
import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.utility.Utility;

import static com.android.janice.nursehelper.MainActivity.ITEM_PORTRAIT_FILEPATH;
import static com.android.janice.nursehelper.MainActivity.ITEM_ROOM_NUMBER;

/**
 * Created by janicerichards on 2/18/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ResidentlistWidgetRemoteViewsService extends RemoteViewsService {
    private static final String[] RESIDENTLIST_COLUMNS = {
            ResidentContract.ResidentEntry.TABLE_NAME + "." + ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER,
            ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH
    };
    // These indices are tied to above.
    static final int COL_ROOM_NUMBER = 0;
    static final int COL_PORTRAIT_FILEPATH = 1;

    private static final String[] MEDLIST_COLUMNS = {
            ResidentContract.MedicationEntry.TABLE_NAME + "." + ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER,
            ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME
    };
    // These indices are tied to above.
    //static final int COL_ROOM_NUMBER = 0;
    static final int COL_MED_TIME = 1;
    static final int COL_MED_TIME_LONG = 2;

    private static final String[] VSLIST_COLUMNS = {
            ResidentContract.AssessmentEntry.TABLE_NAME + "." + ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER,
            ResidentContract.AssessmentEntry.COLUMN_TIME
    };
    // static final int COL_ROOM_NUMBER = 0;
    static final int COL_TIME_ASSESSED = 1;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            private Cursor medData = null;
            private Cursor vsData = null;

            @Override
            public void onCreate() {

                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                if (medData != null) {
                    medData.close();
                }
                if (vsData != null) {
                    vsData.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(ResidentContract.ResidentEntry.CONTENT_URI,
                        RESIDENTLIST_COLUMNS,
                        null,
                        null,
                        ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER + " ASC");

                medData = getContentResolver().query(ResidentContract.MedicationEntry.CONTENT_URI,
                        MEDLIST_COLUMNS, null, null, null);

                vsData = getContentResolver().query(ResidentContract.AssessmentEntry.CONTENT_URI,
                        VSLIST_COLUMNS, null, null, null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
                if (medData != null) {
                    medData.close();
                    medData = null;
                }
                if (vsData != null) {
                    vsData.close();
                    vsData = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                } else if (medData == null || !medData.moveToPosition(position)) {
                    return null;
                } else if (vsData == null || !vsData.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);
                String roomNumber = data.getString(COL_ROOM_NUMBER);
                String portraitFilePath = data.getString(COL_PORTRAIT_FILEPATH);
                String medAdminTime = medData.getString(COL_MED_TIME);
                long vsTimeLong = vsData.getLong(COL_TIME_ASSESSED);
                String vsTime = Utility.getReadableTimestamp(getApplicationContext(),vsTimeLong);

                long medAdminTimeLong = medData.getLong(COL_MED_TIME_LONG);
                long currTime = System.currentTimeMillis();
                if (currTime > medAdminTimeLong) {
                    views.setTextColor(R.id.widget_meds_due,
                            ContextCompat.getColor(getApplicationContext(), R.color.widgetTimeAlertColor));
                } else {
                    views.setTextColor(R.id.widget_meds_due,
                            ContextCompat.getColor(getApplicationContext(), R.color.colorMedTime));
                }

                views.setTextViewText(R.id.widget_room_number, roomNumber);
                views.setTextViewText(R.id.widget_meds_due, medAdminTime);
                views.setTextViewText(R.id.widget_last_assessment, vsTime);
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                //    setRemoteContentDescription(views, description);
                //}


                final Intent medsIntent = new Intent();
                medsIntent.putExtra(ITEM_ROOM_NUMBER, roomNumber);
                medsIntent.putExtra(ITEM_PORTRAIT_FILEPATH, portraitFilePath);

                Uri medsUri = ResidentContract.MedicationEntry.buildMedsWithRoomNumber(roomNumber);
                medsIntent.setData(medsUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, medsIntent);
                views.setOnClickFillInIntent(R.id.widget_meds_due, medsIntent);

                final Intent assessmentIntent = new Intent();
                Uri assessementUri = ResidentContract.AssessmentEntry.buildAssessmentsWithRoomNumber(roomNumber);
                assessmentIntent.setData(assessementUri);
                views.setOnClickFillInIntent(R.id.widget_last_assessment, assessmentIntent);

                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                //views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                data.moveToPosition(position);
                medData.moveToPosition(position);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
