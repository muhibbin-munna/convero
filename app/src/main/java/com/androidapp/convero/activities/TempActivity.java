package com.androidapp.convero.activities;

import static com.androidapp.convero.data.Constants.AD_FREE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.androidapp.convero.R;
import com.androidapp.convero.adapters.TempUnitAdapter;
import com.androidapp.convero.data.IngredientReader;
import com.androidapp.convero.data.TempUnits;

import org.jscience.physics.amount.Amount;

import java.util.Arrays;
import java.util.Locale;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class TempActivity extends AppCompatActivity {
    String TAG = "TemperatureFragment";
    private boolean ignoreListeners = false;


    private Spinner topUnit, bottomUnit;
    private EditText topInput, bottomInput;
    // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
    private static final String AD_UNIT_ID = "ca-app-pub-7423615197865998/7930622729";
    private FrameLayout adContainerView;
    private AdView adView;

    private void setupViewWidgets() {

        topUnit = findViewById(R.id.unit1);
        bottomUnit = findViewById(R.id.unit2);

        topInput = findViewById(R.id.value1);
        bottomInput = findViewById(R.id.value2);

        adContainerView = findViewById(R.id.ad_view_container);


    }

    private void setupKeyButtons() {
        setKeyButtonClickListener(R.id.button_0, KeyEvent.KEYCODE_0);
        setKeyButtonClickListener(R.id.button_1, KeyEvent.KEYCODE_1);
        setKeyButtonClickListener(R.id.button_2, KeyEvent.KEYCODE_2);
        setKeyButtonClickListener(R.id.button_3, KeyEvent.KEYCODE_3);
        setKeyButtonClickListener(R.id.button_4, KeyEvent.KEYCODE_4);
        setKeyButtonClickListener(R.id.button_5, KeyEvent.KEYCODE_5);
        setKeyButtonClickListener(R.id.button_6, KeyEvent.KEYCODE_6);
        setKeyButtonClickListener(R.id.button_7, KeyEvent.KEYCODE_7);
        setKeyButtonClickListener(R.id.button_8, KeyEvent.KEYCODE_8);
        setKeyButtonClickListener(R.id.button_9, KeyEvent.KEYCODE_9);
        setKeyButtonClickListener(R.id.button_dot, KeyEvent.KEYCODE_PERIOD);
        setKeyButtonClickListener(R.id.button_back, KeyEvent.KEYCODE_DEL);
//        setKeyButtonClickListener(R.id.button_minus, KeyEvent.KEYCODE_MINUS,view);
//        setKeyButtonClickListener(R.id.button_ac, KeyEvent.KEYCODE_CLEAR,view);
    }


    private void setupClearButton() {
        findViewById(R.id.button_back).setOnLongClickListener(v -> {
            ignoreListeners = true;

            topInput.getText().clear();
            bottomInput.getText().clear();

            ignoreListeners = false;

            return true;
        });
    }

    private void setupSwapButton() {
        findViewById(R.id.swap).setOnClickListener(v -> {
            ignoreListeners = true;

            // Swap units
            int tempPosition = topUnit.getSelectedItemPosition();
            topUnit.setSelection(bottomUnit.getSelectedItemPosition());
            bottomUnit.setSelection(tempPosition);

            // Swap values
            CharSequence tempText = topInput.getText();
            topInput.setText(bottomInput.getText());
            bottomInput.setText(tempText);
            ignoreListeners = false;

        });
    }

    private void setupMinusButton() {
        findViewById(R.id.button_minus).setOnClickListener(v -> {
            if (!(topInput.getText().toString().isEmpty() || bottomInput.getText().toString().isEmpty())) {
                if (topInput.hasFocus()) {
                    //EditText1 is focused
                    // Get from value
                    String fromString = topInput.getText().toString();
                    // Prepend a 0 in case user submitted value beginning with decimal point
                    double fromVal;
                    if (!(fromString.equals(".") || fromString.equals("-") || fromString.equals("-."))) {
                        if (fromString.contains("-")) {
                            fromVal = Double.parseDouble(fromString);
                        } else {
                            fromVal = Double.parseDouble("0" + fromString);
                        }
                        String data = naturalFormat(fromVal * -1);
                        ignoreListeners = true;
                        topInput.setText(data);
                        topInput.setSelection(data.length());
                        ignoreListeners = false;
                        convert(true);
                    }
                } else if (bottomInput.hasFocus()) {
                    //EditText2 is focused
                    // Get from value
                    String fromString = bottomInput.getText().toString();
                    // Prepend a 0 in case user submitted value beginning with decimal point
                    double fromVal;
                    if (!(fromString.equals(".") || fromString.equals("-") || fromString.equals("-."))) {
                        if (fromString.contains("-")) {
                            fromVal = Double.parseDouble(fromString);
                        } else {
                            fromVal = Double.parseDouble("0" + fromString);
                        }

                        String data = naturalFormat(fromVal * -1);
                        ignoreListeners = true;
                        bottomInput.setText(data);
                        bottomInput.setSelection(data.length());
                        ignoreListeners = false;
                        convert(false);
                    }
                }
                ignoreListeners = false;
            }
        });

    }

    private void setupACButton() {
        findViewById(R.id.button_ac).setOnClickListener(v -> {
            ignoreListeners = true;
            topInput.setText("");
            bottomInput.setText("");
            ignoreListeners = false;
        });
    }

    private void setupSpinners() {
        Log.d(TAG, "setupSpinners: ");
        // Show ingredients in spinner
        IngredientReader ingredientReader = new IngredientReader(this);
        Log.d(TAG, "setupSpinners: " + ingredientReader.getAll());

        // Show units in spinners
        TempUnitAdapter unitAdapter = new TempUnitAdapter(this, TempUnits.getAll());
        topUnit.setAdapter(unitAdapter);
        bottomUnit.setAdapter(unitAdapter);

        // Set initial values
        // TODO Avoid hard-coding

        int cupUsIndex = 0;
        int gramIndex = 1;

        topUnit.setSelection(cupUsIndex);
        bottomUnit.setSelection(gramIndex);
    }

    private void setupChangeListeners() {
        // Listen for changes to ingredients/units and convert
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (ignoreListeners) return;
                convert(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
//        ingredientInput.setOnItemSelectedListener(listener);
        topUnit.setOnItemSelectedListener(listener);
        bottomUnit.setOnItemSelectedListener(listener);

        // Listen for changes to input/output and convert
        topInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + ignoreListeners + " " + s);
                if (ignoreListeners) return;
                convert(true);
            }
        });
        bottomInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ignoreListeners) return;
                convert(false);
            }
        });
    }

    private void disableSystemKeyboard() {
        topInput.setShowSoftInputOnFocus(false);
        bottomInput.setShowSoftInputOnFocus(false);
    }

    private void setKeyButtonClickListener(final int resId, final int keyCode) {
        View button = findViewById(resId);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        button.setOnClickListener(v ->
                getFocusedInput().dispatchKeyEvent(event));
    }

    private void setFractionButtonClickListener(final int resId, final float val) {
        View button = findViewById(resId);
        button.setOnClickListener(v -> addFraction(val));
    }

    private void convert(boolean forward) {
        Spinner fromUnitSpinner, toUnitSpinner;
        EditText fromInput, toInput;
        if (forward) {
            fromUnitSpinner = topUnit;
            toUnitSpinner = bottomUnit;
            fromInput = topInput;
            toInput = bottomInput;
        } else {
            fromUnitSpinner = bottomUnit;
            toUnitSpinner = topUnit;
            fromInput = bottomInput;
            toInput = topInput;
        }

        // Get ingredients and units

        Unit<? extends Quantity> fromUnit = (Unit<? extends Quantity>) fromUnitSpinner.getSelectedItem();
        Unit<? extends Quantity> toUnit = (Unit<? extends Quantity>) toUnitSpinner.getSelectedItem();

        Log.d(TAG, "convert: " + " " + fromUnit + " " + toUnit);

        // Get from value
        String fromString = fromInput.getText().toString();
        // Prepend a 0 in case user submitted value beginning with decimal point
        double fromVal;
        if (fromString.isEmpty()) {
//            fromVal = Double.parseDouble("0" + fromString);
            toInput.getText().clear();
        } else {
            if (!(fromString.equals(".") || fromString.equals("-") || fromString.equals("-."))) {
                if (fromString.contains("-")) {
                    fromVal = Double.parseDouble(fromString);
                } else {
                    fromVal = Double.parseDouble("0" + fromString);
                }


                // Convert to new value
                Amount<? extends Quantity> from = Amount.valueOf(fromVal, fromUnit);
                Amount<? extends Quantity> to = from.to(toUnit);

                double toVal = to.getEstimatedValue();

                // Display new value
                ignoreListeners = true;
                toInput.setText(naturalFormat(toVal));
                ignoreListeners = false;
            }
        }


    }

    //private void convert(boolean forward) {
//    Spinner fromUnitSpinner, toUnitSpinner;
//    EditText fromInput, toInput;
//    if (forward) {
//        fromUnitSpinner = topUnit;
//        toUnitSpinner = bottomUnit;
//        fromInput = topInput;
//        toInput = bottomInput;
//    } else {
//        fromUnitSpinner = bottomUnit;
//        toUnitSpinner = topUnit;
//        fromInput = bottomInput;
//        toInput = topInput;
//    }
//
//    // Get ingredients and units
//
//    Unit<? extends Quantity> fromUnit = (Unit<? extends Quantity>) fromUnitSpinner.getSelectedItem();
//    Unit<? extends Quantity> toUnit = (Unit<? extends Quantity>) toUnitSpinner.getSelectedItem();
//
//
//
//    // Get from value
//    String fromString = fromInput.getText().toString();
//    // Prepend a 0 in case user submitted value beginning with decimal point
//    double fromVal = Double.parseDouble("0" + fromString);
//
//    // Convert to new value
//    Amount<? extends Quantity> from = Amount.valueOf(fromVal, fromUnit);
//    Amount<? extends Quantity> to;
//
//    double toVal = from.getEstimatedValue();
//
//    // Display new value
//    ignoreListeners = true;
//    toInput.setText(naturalFormat(toVal));
//    ignoreListeners = false;
//}
    private EditText getFocusedInput() {
        return bottomInput.hasFocus() ? bottomInput : topInput;
    }

    private void addFraction(float fractionValue) {
        EditText focusedInput = getFocusedInput();
        String focusString = focusedInput.getText().toString();

        double val = focusString.isEmpty() ? 0 : Double.valueOf(focusString);
        val += fractionValue;

        // Weird rounding mostly to account for +⅓ three times
        double fractional = val % 1;
        if (fractional <= 0.01 || fractional >= 0.99) {
            val = Math.round(val);
        }

        focusedInput.setText(naturalFormat(val));
        focusedInput.setSelection(focusedInput.getText().length());
    }

    private static String naturalFormat(double d) {
        if (d == 0)
            return "0";

        // Format with 2 decimal places
        // TODO Should have as many decimal places as input as maximum
        String s = String.format(Locale.US, "%.2f", d);

        // Remove trailing zeros
        s = s.replaceAll("0*$", "");

        // Remove trailing dot
        s = s.replaceAll("\\.$", "");

        return s;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_temp);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle =  toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Temperature Conversion");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });
        setupViewWidgets();
        setupKeyButtons();

        setupClearButton();
        setupSwapButton();
        setupMinusButton();
        setupACButton();
        setupSpinners();
        setupChangeListeners();
        disableSystemKeyboard();
        if(!AD_FREE){
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {}
            });

            adContainerView.post(new Runnable() {
                @Override
                public void run() {
                    loadBanner();
                }
            });
        }


    }
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }
    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void loadBanner() {
        // Create an ad request.
        adView = new AdView(this);
        adView.setAdUnitId(AD_UNIT_ID);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }
    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

}