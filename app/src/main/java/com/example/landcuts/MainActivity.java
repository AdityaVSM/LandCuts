package com.example.landcuts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.landcuts.Fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {
    Animation top_anim,bottom_anim;
    TextView txt_anim,txt_anim2;
    ImageView image_anim;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top_anim = AnimationUtils.loadAnimation(this,R.animator.top_animation);
        bottom_anim = AnimationUtils.loadAnimation(this,R.animator.bottom_animation);

        txt_anim = findViewById(R.id.text_anim);
        txt_anim2 = findViewById(R.id.text_anim2);
        image_anim = findViewById(R.id.image_anim);

        txt_anim.setAnimation(top_anim);
        image_anim.setAnimation(top_anim);
        txt_anim2.setAnimation(bottom_anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        },3000);


    }


}