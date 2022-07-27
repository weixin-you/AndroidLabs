package com.cst2335.you00018;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG="PROFILE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_second);
        Log.e(TAG, "onCreate");

        EditText emailEditText = findViewById(R.id.enterEmail);
        Intent fromMain = getIntent();
        emailEditText.setText(fromMain.getStringExtra(MainActivity.EMAIL));
        Button goToChatroom = findViewById(R.id.chat_button);
        goToChatroom.setOnClickListener(click -> {
            Intent gotoChatroom = new Intent(ProfileActivity.this,ChatRoomActivity.class);
            startActivity(gotoChatroom);
        });

        Button goToWeatherForecast = findViewById(R.id.weatherForecast_button);
        goToWeatherForecast.setOnClickListener(click -> {
            Intent goWeatherForecast = new Intent(ProfileActivity.this,WeatherForecast.class);
            startActivity(goWeatherForecast);
        });
    }

    ActivityResultLauncher<Intent> myPictureTakerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult()
            , result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bitmap imgBitMap = (Bitmap) data.getExtras().get("data");
                    ImageView imgView;
                }
                else if(result.getResultCode() == Activity.RESULT_CANCELED)
                    Log.i(TAG, "User refused to capture a picture.");
            });

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}