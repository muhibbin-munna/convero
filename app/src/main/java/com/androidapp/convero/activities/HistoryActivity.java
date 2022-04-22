package com.androidapp.convero.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.convero.R;
import com.androidapp.convero.adapters.HistoryAdapter;
import com.androidapp.convero.database.MyDatabaseHelper;
import com.androidapp.convero.models.History;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    MyDatabaseHelper myDatabaseHelper;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private LinearLayoutManager mlayoutManager;
    private TextView historyText;
    private List<History> historyList;
    private int historyCount;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history_clear:
                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        TextView mTitle =  toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("History");
        toolbar.inflateMenu(R.menu.history_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_history_clear:
                        if(historyCount == 0){
                            Toast.makeText(getApplicationContext(), "No History", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            showAlertDialog();
                        }

                        return true;
                    default:
                        return false;
                }
            }
        });

        myDatabaseHelper = new MyDatabaseHelper(this);

        mRecyclerView = findViewById(R.id.historyrecyclerViewId);
        mlayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mlayoutManager);
        mProgressCircle = findViewById(R.id.historyprogressbarId);
        historyText = findViewById(R.id.historyText);
        historyList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = myDatabaseHelper.read_all_data();
        historyCount = cursor.getCount();
        if (historyCount == 0) {
            historyText.setVisibility(View.VISIBLE);
            mProgressCircle.setVisibility(View.INVISIBLE);
            if (!cursor.isClosed())
                cursor.close();
            sqLiteDatabase.close();
        } else {
            historyText.setVisibility(View.GONE);
            try {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.KEY_ID));
                    String date = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.DATE));
                    String item1 = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.ITEM1));
                    String item2 = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.ITEM2));
                    String item_name = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.ITEM_NAME));

                    historyList.add(new History(id,date,item1, item2,item_name));
                }
            } finally {
                if (!cursor.isClosed())
                    cursor.close();
                sqLiteDatabase.close();
            }
            Collections.reverse(historyList);
            mAdapter = new HistoryAdapter(this,historyList );

            mRecyclerView.setAdapter(mAdapter);
//            mAdapter.setOnItemClickListener(Fragent_favorites.this);
            mProgressCircle.setVisibility(View.INVISIBLE);
        }

    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete History")
                .setMessage("Are you sure you want to delete all the history?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation


                            SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
                            myDatabaseHelper.delete_all_data();
                            Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            sqLiteDatabase.close();
                        historyList.clear();
                        mAdapter.notifyDataSetChanged();
                        historyText.setVisibility(View.VISIBLE);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}