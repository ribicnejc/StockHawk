package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Nejc on 2. 05. 2017.
 */

public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
    }
}
