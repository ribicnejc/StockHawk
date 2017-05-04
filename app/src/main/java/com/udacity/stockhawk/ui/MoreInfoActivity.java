package com.udacity.stockhawk.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class MoreInfoActivity extends AppCompatActivity {

    private String symbol = null;
    private static String[] HISTORY = {
            Contract.Quote.COLUMN_HISTORY
    };
    private XAxis xAxis;
    private YAxis yAxisLeft;
    private YAxis yAxisRight;

    private LineChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        mChart = (LineChart) findViewById(R.id.chart);
        xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        yAxisLeft = mChart.getAxisLeft();
        yAxisRight = mChart.getAxisRight();
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisRight.setTextColor(Color.WHITE);


        if (getIntent().hasExtra(MainActivity.EXTRA_SYMBOL)){
            symbol = getIntent().getStringExtra(MainActivity.EXTRA_SYMBOL);
            setTitle(symbol);
            getData(symbol);

        }

    }


    private void getData(final String symbol){
        new AsyncTask<Void, Void, Cursor>(){
            @Override
            protected Cursor doInBackground(Void... voids) {
                Cursor data;
                Uri uri = Contract.Quote.makeUriForStock(symbol);
                data = getContentResolver().query(uri, HISTORY, null, null, null);
                return data;

            }
            @Override
            protected void onPostExecute(Cursor c) {
                super.onPostExecute(c);
                drawGraph(c);
            }
        }.execute();
    }


    private void drawGraph(Cursor data){
        if(data == null || !data.moveToFirst()) return;
        String history = null;
        try{
            do{
                history = data.getString(0);
            }
            while (data.moveToNext());
        }
        catch (Exception e){
            Timber.e(e);
        }
        finally {
            data.close();
        }
        setData(history);
    }

    private void setData(String history){
        ArrayList<Entry> values = new ArrayList<>();
        String[] dat = history.split("\n");
        String[] dat2 = new String[dat.length];
        for (int i = dat.length - 1; i >= 0; i--) {
            String[] t = dat[i].split(", ");
            long time = Long.parseLong(t[0]);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
            calendar.setTimeInMillis(time);
            String tmp = sdf.format(calendar.getTime());
            dat2[dat.length - i - 1] = tmp;
            float price = Float.parseFloat(t[1]);
            Entry ent = new Entry(100 - i, price, null);
            values.add(ent);
        }
        LineDataSet set1;

        xAxis.setValueFormatter(new MyXAxisValueFormatter(dat2));
        yAxisLeft.setValueFormatter(new MyYAxisValueFormatter());
        yAxisRight.setValueFormatter(new MyYAxisValueFormatter());

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, String.format("%s growth line", symbol));
            //set1.setFillColor(Color.WHITE);
            //et1.setHighLightColor(Color.WHITE);

            set1.setHighLightColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setCircleRadius(2f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setValueTextColor(Color.WHITE);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
            mChart.animateX(2500);
        }
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            try{
                return mValues[(int) value];
            }catch (Exception e){
                return mValues[0];
            }

        }
    }
    //TODO add few more info elements
    //TODO check graph for loops for incorrectness
    //TODO after all coding check for RTL and TALK BACK and check for all STRINGS and translatable string also
    public class MyYAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {

            // format values to 1 decimal digit
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mFormat.format(value) + " $";
        }

    }
}
