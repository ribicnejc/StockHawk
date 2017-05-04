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
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

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
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
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

                String together = stockAbsoluteChange;
                boolean percent = false;
                if (intent.hasExtra(DetailWidgetProvider.EXTRA_PERCENT)){
                    String extra = intent.getStringExtra(DetailWidgetProvider.EXTRA_PERCENT);
                    if (extra.equals("percentage")){
                        together = stockPercentageChange;
                        percent = true;
                    }
                }

                views.setTextViewText(R.id.widget_symbol, stockSymbol);
                views.setTextViewText(R.id.widget_price, "$"+stockPrice);

                float raw = Float.parseFloat(together);
                if (raw < 0){
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }else{
                    together = "+" + together;
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                }
                if (percent){
                    views.setTextViewText(R.id.widget_change, together + "%");
                }else{
                    String tmp = together.charAt(0) + "$" + together.substring(1);
                    views.setTextViewText(R.id.widget_change, tmp);
                }



                //TODO content description for widget

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(MainActivity.EXTRA_SYMBOL, stockSymbol);
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
