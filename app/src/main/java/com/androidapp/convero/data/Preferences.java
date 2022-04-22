package com.androidapp.convero.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private String PREFERENCE = "pref";
    private SharedPreferences.Editor editor;

    public Long getNoThanksPressedLong(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getLong("button pressed date", 0);
    }

    public void setNoThanksPressedLong(Context context, Long buttonPressedLong) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putLong("button pressed date", buttonPressedLong);
        editor.apply();
    }

    public boolean getHasReviewed(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getBoolean("reviewed", false);
    }

    public void setHasReviewed(Context context, boolean hasReviewed) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("reviewed", hasReviewed);
        editor.apply();
    }
}
