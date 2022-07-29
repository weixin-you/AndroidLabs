package com.cst2335.you00018;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras();

        DetailsFragment dFragment = new DetailsFragment();
        dFragment.setArguments(dataToPass);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentItem,dFragment)
                .commit();

    }
}