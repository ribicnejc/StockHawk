package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.MoreInfoActivity;

public class DetailWidgetProvider extends AppWidgetProvider{
    public static final String EXTRA_PERCENT = "extra_percent_on_widget";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detail);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            setRemoteAdapter(context, views);


            Intent clickIntentTemplate = new Intent(context, MoreInfoActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        String percent = PrefUtils.getDisplayMode(context);
        Intent intent = new Intent(context, DetailWidgetRemoteViewsService.class);
        intent.putExtra(EXTRA_PERCENT, percent);
        views.setRemoteAdapter(R.id.widget_list, intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }

}
