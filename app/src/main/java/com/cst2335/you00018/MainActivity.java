package com.cst2335.you00018;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EMAIL="EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_linear);
        EditText typeEmail = findViewById(R.id.type_email);
        SharedPreferences sharedPreferences = getSharedPreferences(EMAIL, Context.MODE_PRIVATE);
        String preferencesString = sharedPreferences.getString(EMAIL,"");

        typeEmail.setText(preferencesString);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(click ->{
            String emailEntered = typeEmail.getText().toString();
            Intent gotoProfile = new Intent(MainActivity.this,ProfileActivity.class);
            gotoProfile.putExtra(EMAIL,emailEntered);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(EMAIL,emailEntered);
            editor.apply();
            startActivity(gotoProfile);
        });
    }

}