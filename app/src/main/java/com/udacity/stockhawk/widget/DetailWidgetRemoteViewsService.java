package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.TABLE_NAME + "." + Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };
    static final int INDEX_POSITION_ID = 0;
    static final int INDEX_POSITION_SYMBOL = 1;
    static final int INDEX_POSITION_PRICE = 2;
    static final int INDEX_POSITION_ABSOLUTE_CHANGE = 3;
    static final int INDEX_POSITION_PERCENTAGE_CHANGE = 4;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null){
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                Uri uri = Contract.Quote.URI;
                data = getContentResolver().query(uri, STOCK_COLUMNS, null, null, Contract.Quote.COLUMN_SYMBOL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null){
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
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)){
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
                int stockId = data.getInt(INDEX_POSITION_ID);
                String stockSymbol = data.getString(INDEX_POSITION_SYMBOL);
                String stockPrice = data.getString(INDEX_POSITION_PRICE);
                String stockAbsoluteChange = data.getString(INDEX_POSITION_ABSOLUTE_CHANGE);
                String stockPercentageChange = data.getString(INDEX_POSITION_PERCENTAGE_CHANGE);
                //TODO check which is checked, maybe its saved in prefs, or you save it in prefs!

                views.setTextViewText(R.id.widget_symbol, stockSymbol);
                views.setTextViewText(R.id.widget_price, stockPrice);
                views.setTextViewText(R.id.widget_change, stockAbsoluteChange);

                //TODO content description for widget

                final Intent fillInIntent = new Intent();
                Uri uri = Contract.Quote.URI;
                fillInIntent.setData(uri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            private void setRemoteContentDescription(RemoteViews views, String description) {
                //views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getLong(INDEX_POSITION_ID);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
