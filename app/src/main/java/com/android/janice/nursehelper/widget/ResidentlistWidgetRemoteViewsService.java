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

/**
 * Created by janicerichards on 2/18/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ResidentlistWidgetRemoteViewsService extends RemoteViewsService {
    private static final String[] RESIDENTLIST_COLUMNS = {
            ResidentContract.ResidentEntry.TABLE_NAME + "." + ResidentContract.ResidentEntry._ID,
            ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER
    };
    // These indices are tied to above.
    static final int COL_ID = 0;
    static final int COL_ROOM_NUMBER = 1;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(ResidentContract.ResidentEntry.CONTENT_URI,
                        RESIDENTLIST_COLUMNS,
                        null,
                        null,
                        ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
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
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);
                String roomNumber = data.getString(COL_ROOM_NUMBER);

                views.setTextViewText(R.id.widget_room_number, roomNumber);
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                //    setRemoteContentDescription(views, description);
                //}

/*
                final Intent medsIntent = new Intent();
                Uri medsUri = ResidentContract.MedicationEntry.buildMedsWithRoomNumber(roomNumber);
                medsIntent.setData(medsUri);
                views.setOnClickFillInIntent(R.id.widget_meds_due, medsIntent);

                final Intent assessmentIntent = new Intent();
                Uri assessementUri = ResidentContract.AssessmentEntry.buildAssessmentsWithRoomNumber(roomNumber);
                assessmentIntent.setData(assessementUri);
                views.setOnClickFillInIntent(R.id.widget_last_assessment, assessmentIntent);
*/
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
                if (data.moveToPosition(position))
                    return data.getLong(COL_ID);
                    //return data.getLong(INDEX_STOCK_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
