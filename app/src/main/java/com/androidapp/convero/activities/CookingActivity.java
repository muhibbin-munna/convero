package com.androidapp.convero.activities;

import static com.androidapp.convero.data.Constants.AD_FREE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.convero.R;
import com.androidapp.convero.adapters.CookingUnitAdapter;
import com.androidapp.convero.data.CookingUnits;
import com.androidapp.convero.data.IngredientReader;
import com.androidapp.convero.database.MyDatabaseHelper;
import com.androidapp.convero.models.Ingredient;
import com.androidapp.convero.views.IngredientSpinner;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jscience.physics.amount.Amount;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class CookingActivity extends AppCompatActivity {

    String TAG = "CookingActivity";
    private boolean ignoreListeners = false;
    private boolean flipListeners = false;

    private IngredientSpinner ingredientInput;
    private Spinner topUnit, bottomUnit;
    private EditText topInput, bottomInput;
    String topInputNumber, bottomInputNumber;
    private EditText originalInput, desiredInput;
    private double originalValue = 1.0, desiredValue = 1.0;
    // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
    private static final String AD_UNIT_ID = "ca-app-pub-7423615197865998/7930622729";
    private FrameLayout adContainerView;
    private AdView adView;

    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms

    TextView resizedText;
    boolean focusTop = true;

    MyDatabaseHelper myDatabaseHelper;

    private void setupViewWidgets() {
        ingredientInput = findViewById(R.id.ingredient);
        topUnit = findViewById(R.id.unit1);
        bottomUnit = findViewById(R.id.unit2);

        topInput = findViewById(R.id.value1);
        bottomInput = findViewById(R.id.value2);
        originalInput = findViewById(R.id.originalValue);
        desiredInput = findViewById(R.id.desiredValue);
        resizedText = findViewById(R.id.resizedText);

        adContainerView = findViewById(R.id.ad_view_container);

        myDatabaseHelper = new MyDatabaseHelper(this);
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
    }

    private void setupFractionButtons() {
        setFractionButtonClickListener(R.id.button_quarter, 0.25f);
        setFractionButtonClickListener(R.id.button_third, 0.33f);
        setFractionButtonClickListener(R.id.button_half, 0.5f);
    }

    private void setupClearButton() {
        findViewById(R.id.button_back).setOnLongClickListener(v -> {

            topInput.getText().clear();
            bottomInput.getText().clear();
            originalValue = 1;
            desiredValue = 1;

            return true;
        });
    }

    private void setupHistoryButton() {
        findViewById(R.id.swap).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }

    private void setupSpinners() {
        // Show ingredients in spinner
        IngredientReader ingredientReader = new IngredientReader(this);
//        Log.d(TAG, "setupSpinners: " + ingredientReader.getAll());
        ingredientInput.setIngredients(ingredientReader.getAll());

        // Show units in spinners
        CookingUnitAdapter cookingUnitAdapter = new CookingUnitAdapter(this, CookingUnits.getAll());
        topUnit.setAdapter(cookingUnitAdapter);
        bottomUnit.setAdapter(cookingUnitAdapter);
    }

    private void setupChangeListeners() {
        // Listen for changes to ingredients/units and convert
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (ignoreListeners || flipListeners) return;
                else convert(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        ingredientInput.setSelection(Adapter.NO_SELECTION, true);
        topUnit.setSelection(Adapter.NO_SELECTION, true);
        bottomUnit.setSelection(Adapter.NO_SELECTION, true);

        ingredientInput.setOnItemSelectedListener(listener);
        topUnit.setOnItemSelectedListener(listener);
        bottomUnit.setOnItemSelectedListener(listener);
        // Set initial values

        int flourIndex = 0;
        int cupUsIndex = 0;
        int gramIndex = 3;

        ingredientInput.setSelection(flourIndex);
        topUnit.setSelection(cupUsIndex);
        bottomUnit.setSelection(gramIndex);

        // Listen for changes to input/output and convert
        topInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (focusTop && timer != null)
                    timer.cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                topInputNumber = s.toString();
                if (ignoreListeners || flipListeners) return;
                else convert(true);

            }
        });
        topInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // When focus is lost check that the text field has valid values.

                if (hasFocus) {
                    {
                        // Validate youredittext
                        focusTop = true;
                        Log.d(TAG, "onFocusChange: top");
                    }
                }
            }
        });
        bottomInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!focusTop && timer != null)
                    timer.cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                bottomInputNumber = s.toString();
                if (ignoreListeners || flipListeners) return;
                else convert(false);
            }
        });

        originalInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    originalValue = 1;
                    if (desiredInput.getText().toString().isEmpty())
                        resizedText.setVisibility(View.INVISIBLE);
                } else {
                    originalValue = Integer.parseInt(s.toString());
                    resizedText.setVisibility(View.VISIBLE);
                }
                if (ignoreListeners || flipListeners) return;
                else convert(true);
            }
        });

        desiredInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    desiredValue = 1;
                    if (originalInput.getText().toString().isEmpty())
                        resizedText.setVisibility(View.INVISIBLE);
                } else {
                    desiredValue = Integer.parseInt(s.toString());
                    resizedText.setVisibility(View.VISIBLE);
                }
                if (ignoreListeners || flipListeners) return;
                else convert(true);

            }
        });

        bottomInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // When focus is lost check that the text field has valid values.

                if (hasFocus) {
                    {
                        focusTop = false;
                        // Validate youredittext
                        Log.d(TAG, "onFocusChange: bottom");
                    }
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

//        Log.d(TAG, "onConfigurationChanged: " + topInputNumber + " " + bottomInputNumber + " " + String.valueOf(originalValue) + " " + desiredValue);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_cooking);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.cooking_activity_land);
        } else {
            Log.w("tag", "other: " + orientation);
        }


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Cooking Conversion");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });
        flipListeners = true;
        setupViewWidgets();
        setupKeyButtons();
        setupFractionButtons();
        setupClearButton();
        setupHistoryButton();
        setupSpinners();
        setupChangeListeners();
        disableSystemKeyboard();

        ignoreListeners = true;

        topInput.setText(topInputNumber);
        bottomInput.setText(bottomInputNumber);
        if( !(originalValue==1 && desiredValue==1))
        {
            originalInput.setText(String.valueOf((int)originalValue));
            desiredInput.setText(String.valueOf((int)desiredValue));
        }
        ignoreListeners = false;
        flipListeners = false;



        if (!AD_FREE) {

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            adContainerView.post(new Runnable() {
                @Override
                public void run() {
                    loadBanner();
                }
            });
        }


    }

    private void disableSystemKeyboard() {
        topInput.setShowSoftInputOnFocus(false);
        bottomInput.setShowSoftInputOnFocus(false);
        originalInput.setShowSoftInputOnFocus(false);
        desiredInput.setShowSoftInputOnFocus(false);
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
        Ingredient ingredient = (Ingredient) ingredientInput.getSelectedItem();
        Unit<? extends Quantity> fromUnit = (Unit<? extends Quantity>) fromUnitSpinner.getSelectedItem();
        Unit<? extends Quantity> toUnit = (Unit<? extends Quantity>) toUnitSpinner.getSelectedItem();

        Log.d(TAG, "convert: " + ingredient + " " + fromUnit + " " + toUnit);

        // Get from value
        String fromString = fromInput.getText().toString();
        // Prepend a 0 in case user submitted value beginning with decimal point
        double fromVal = Double.parseDouble("0" + fromString);
        double tempfromVal = desiredValue / originalValue * fromVal;
        // Convert to new value
        Amount<? extends Quantity> from = Amount.valueOf(tempfromVal, fromUnit);
        Amount<? extends Quantity> to;
        if (fromUnit.isCompatible(toUnit))
            to = from.to(toUnit);
        else {
            // Mass -> Volume
            if (Mass.UNIT.isCompatible(fromUnit)) {
                to = from.divide(ingredient.getDensity()).to(toUnit);
            }
            // Volume -> Mass
            else {
                to = from.times(ingredient.getDensity()).to(toUnit);
            }
        }
        double toVal = to.getEstimatedValue();
        if (!flipListeners) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (toVal != 0 && tempfromVal != 0) {
//                    Log.d(TAG, "run: "+System.currentTimeMillis()+" " +fromVal+" "+CookingUnits.unitToString(fromUnit)+ toVal+" "+CookingUnits.unitToString(toUnit)+ ingredient.getName());

                        try {
                            SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
                            long i = myDatabaseHelper.insertData(String.valueOf(System.currentTimeMillis()), naturalFormat(fromVal) + " " + CookingUnits.unitToString(fromUnit), naturalFormat(toVal) + " " + CookingUnits.unitToString(toUnit), ingredient.getName());
                            Log.d(TAG, "run: a" + i);
                            sqLiteDatabase.close();
                        } catch (Exception e) {
                            Log.d(TAG, "run: error");
                        }

                    }

                }

            }, DELAY);
        }
        // Display new value
        ignoreListeners = true;
        toInput.setText(naturalFormat(toVal));
        ignoreListeners = false;


    }

    private EditText getFocusedInput() {
        if (bottomInput.hasFocus()) {
            return bottomInput;
        } else if (topInput.hasFocus()) {
            return topInput;
        } else if (originalInput.hasFocus()) {
            return originalInput;
        } else if (desiredInput.hasFocus()) {
            return desiredInput;
        } else {
            return topInput;
        }
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
            return "";

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
        setContentView(R.layout.activity_cooking);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Cooking Conversion");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        setupViewWidgets();
        setupKeyButtons();
        setupFractionButtons();
        setupClearButton();
        setupHistoryButton();
        setupSpinners();
        setupChangeListeners();
        disableSystemKeyboard();

        if (!AD_FREE) {

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            adContainerView.post(new Runnable() {
                @Override
                public void run() {
                    loadBanner();
                }
            });
        }
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
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