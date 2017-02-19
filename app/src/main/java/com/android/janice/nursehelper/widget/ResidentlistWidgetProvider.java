package com.android.janice.nursehelper.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.janice.nursehelper.MedicationsActivity;
import com.android.janice.nursehelper.AssessmentActivity;
import com.android.janice.nursehelper.MainActivity;
import com.android.janice.nursehelper.R;
//import com.android.janice.nursehelper.sync.ResidentSyncAdapter;

/**
 * Created by janicerichards on 2/18/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ResidentlistWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }


            //boolean useDetailActivity = context.getResources()
            //        .getBoolean(R.bool.use_detail_activity);

            //Intent clickIntentTemplate = useDetailActivity
            //        ? new Intent(context, DetailActivity.class)
            //        : new Intent(context, MainActivity.class);



            Intent clickIntentTemplate = new Intent(context, MainActivity.class);

            PendingIntent clickPendingMedsIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingMedsIntentTemplate);


            /*

            Intent clickIntentMedsTemplate = new Intent(context, MedicationsActivity.class);

            PendingIntent clickPendingMedsIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentMedsTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_meds_due, clickPendingMedsIntentTemplate);
            views.setEmptyView(R.id.widget_meds_due, R.id.widget_empty);

            Intent clickIntentAssessmentTemplate = new Intent(context, AssessmentActivity.class);

            PendingIntent clickPendingAssessmentIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentAssessmentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_last_assessment, clickPendingAssessmentIntentTemplate);
            views.setEmptyView(R.id.widget_last_assessment, R.id.widget_empty);
*/

            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        // FOR NOW:
/*
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
*/

        /*
        if (ResidentlistSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
        */
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, ResidentlistWidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, ResidentlistWidgetRemoteViewsService.class));
    }
}
