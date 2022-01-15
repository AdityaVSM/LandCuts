package com.example.landcuts;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Animation top_anim,bottom_anim;
    TextView txt_anim,txt_anim2;
    ImageView image_anim;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      

//        top_anim = AnimationUtils.loadAnimation(this,R.animator.top_animation);
//        bottom_anim = AnimationUtils.loadAnimation(this,R.animator.bottom_animation);

        txt_anim = findViewById(R.id.text_anim);
        txt_anim2 = findViewById(R.id.text_anim2);
        image_anim = findViewById(R.id.image_anim);

        txt_anim.setAnimation(top_anim);
        image_anim.setAnimation(top_anim);
        txt_anim2.setAnimation(bottom_anim);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        },3000);


    }


}