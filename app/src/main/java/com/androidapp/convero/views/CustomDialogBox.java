package com.androidapp.convero.views;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.convero.R;
import com.androidapp.convero.data.Preferences;

public class CustomDialogBox extends Dialog implements android.view.View.OnClickListener {
    private Preferences preferences;
    private Activity activity;
    private TextView tv_positive, tv_negative;

    public CustomDialogBox(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_box_custom);

        preferences = new Preferences();
        tv_positive = findViewById(R.id.tv_reviewApp);
        tv_negative = findViewById(R.id.tv_notNow);
        tv_positive.setOnClickListener(this);
        tv_negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reviewApp:
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                preferences.setHasReviewed(activity, true);
                activity.finish();
                break;
            case R.id.tv_notNow:
                long today = System.currentTimeMillis();
                preferences.setNoThanksPressedLong(activity, today);
                break;
            default:
                break;
        }
        dismiss();
    }
}