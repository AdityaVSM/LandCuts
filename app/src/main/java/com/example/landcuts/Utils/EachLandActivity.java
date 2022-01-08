package com.example.landcuts.Utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.landcuts.Models.Land;
import com.example.landcuts.R;

public class EachLandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_land);

        Intent intent = getIntent();
        Land land = (Land) intent.getSerializableExtra("land");
        System.out.println(land.getName());
    }
}