package com.udacity.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.stockhawk.R;

public class MoreInfoActivity extends AppCompatActivity {

    private String symbol = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        if (getIntent().hasExtra(MainActivity.EXTRA_SYMBOL)){
            symbol = getIntent().getStringExtra(MainActivity.EXTRA_SYMBOL);
        }


    }
}
