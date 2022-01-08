package com.example.landcuts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.landcuts.Constants.Constants;
import com.example.landcuts.Models.Land;
import com.example.landcuts.R;

public class EachLandActivity extends AppCompatActivity {

    TextView current_available_land_parts, current_land_share_price,land_name, land_location, total_price_ofShare_bought_by_user, total_cuts_ofLand_bought_by_user;
    ImageView land_image_view;

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        land_image_view.setImageDrawable(null);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_land);

        current_available_land_parts = findViewById(R.id.current_available_land_parts);
        current_land_share_price = findViewById(R.id.current_land_share_price);
        land_name = findViewById(R.id.land_name);
        land_location = findViewById(R.id.land_location);
        total_price_ofShare_bought_by_user = findViewById(R.id.total_price_ofShare_bought_by_user);
        total_cuts_ofLand_bought_by_user = findViewById(R.id.total_cuts_ofLand_bought_by_user);
        land_image_view = findViewById(R.id.land_image_view);

        Intent intent = getIntent();
        Land land = (Land) intent.getSerializableExtra("land");
        System.out.println(land.getCurrentPrice());

        land_name.setText(land.getName());
        land_location.setText(land.getLocation());
        current_land_share_price.setText((Constants.rupee_symbol+String.valueOf(land.getCurrentPrice())));
        current_available_land_parts.setText((String.valueOf(land.getNo_of_available_cuts())+"/100"));
        String image_path = land.getLocation().toLowerCase();
        System.out.println(image_path);
        int image_id=0;
        switch (image_path){
            case "mumbai":
                image_id = R.drawable.mumbai;
                break;
            case "arizona":
                image_id = R.drawable.arizona;
                break;
            case "california":
                image_id = R.drawable.california;
                break;
            case "uk":
                image_id = R.drawable.uk;
                break;
            case "bengaluru":
                image_id = R.drawable.banglore;
                break;
            case "delhi":
                image_id = R.drawable.delhi;
                break;
        }
        land_image_view.setImageResource(image_id);






    }
}