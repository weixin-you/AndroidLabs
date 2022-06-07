package com.cst2335.you00018;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.button2);
        btn.setOnClickListener(e -> handleBtnClick());
        CompoundButton switchBtn = findViewById(R.id.switch1);
        switchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Snackbar.make(buttonView,getResources().getString(R.string.switch_on), LENGTH_LONG).setAction(getResources().getString(R.string.undo),click->buttonView.setChecked(!isChecked)).show();
            }
            else{
                Snackbar.make(buttonView,getResources().getString(R.string.switch_off), LENGTH_LONG).setAction(getResources().getString(R.string.undo),click->buttonView.setChecked(!isChecked)).show();
            }
        });
    }

    public void handleBtnClick(){
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.toast_message);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}