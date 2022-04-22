package com.androidapp.convero.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.convero.R;

import com.androidapp.convero.activities.CookingActivity;
import com.androidapp.convero.activities.MainActivity;
import com.androidapp.convero.activities.TempActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {

    CardView cooking_button, temp_button;


    private void setupViewWidgets(View view) {
        cooking_button = view.findViewById(R.id.cooking_button);
        temp_button = view.findViewById(R.id.temp_button);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        setupViewWidgets(view);
        MainActivity.toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        Toolbar toolbar = MainActivity.toolbar;
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        TextView mTitle =  toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("");
        cooking_button.setOnClickListener(this);
        temp_button.setOnClickListener(this);
        return view;
    }




    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.cooking_button){
//            MainActivity.navigationView.setCheckedItem(R.id.nav_cooking);
//            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new CookingFragment()).commit();
//            startActivity(new Intent(getContext(), CookingActivity.class));
            startActivity(new Intent(getContext(), CookingActivity.class));
        }
        else if(v.getId() == R.id.temp_button){
//            MainActivity.navigationView.setCheckedItem(R.id.nav_temp);
//            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new TemperatureFragment()).commit();
            startActivity(new Intent(getContext(), TempActivity.class));
        }

    }
}