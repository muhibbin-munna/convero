package com.androidapp.convero.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidapp.convero.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_about_us);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle =  toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("About Us");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });
//        WebView view = new WebView(this);
//        view.setVerticalScrollBarEnabled(false);
//
//        ((LinearLayout)findViewById(R.id.inset_web_view)).addView(view);
        WebView view = (WebView) findViewById(R.id.textContent);

        view.loadData(getString(R.string.hello), "text/html; charset=utf-8", "utf-8");
    }
}