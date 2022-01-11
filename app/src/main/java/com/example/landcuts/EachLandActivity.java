package com.example.landcuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.landcuts.Constants.Constants;
import com.example.landcuts.Models.Land;
import com.example.landcuts.Models.Transaction;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EachLandActivity extends AppCompatActivity {

    TextView current_available_land_parts, current_land_share_price,land_name, land_location,
            total_price_ofShare_bought_by_user, total_cuts_ofLand_bought_by_user, show_your_acquirings;
    ImageView land_image_view;
    Button buy_button, sell_button;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_land);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        current_available_land_parts = findViewById(R.id.current_available_land_parts);
        current_land_share_price = findViewById(R.id.current_land_share_price);
        land_name = findViewById(R.id.land_name);
        land_location = findViewById(R.id.land_location);
        show_your_acquirings = findViewById(R.id.show_your_acquirings);
        total_price_ofShare_bought_by_user = findViewById(R.id.total_price_ofShare_bought_by_user);
        total_cuts_ofLand_bought_by_user = findViewById(R.id.total_cuts_ofLand_bought_by_user);
        land_image_view = findViewById(R.id.land_image_view);
        buy_button = findViewById(R.id.buy_button);
        sell_button = findViewById(R.id.sell_button);

        Intent intent = getIntent();
        Land land = (Land) intent.getSerializableExtra("land");

        updateLandDetails(land);

        ArrayList<Transaction> owners = getOwnersOfLand(land);

        buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(EachLandActivity.this)
                        .setMessage("Are you sure you want to buy 1 share of this land")
                        .setTitle("Confirm")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference = database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land");
                                int no_of_shares = 1;
                                if (land.getNo_of_available_cuts() == 0) {
                                    Toast.makeText(EachLandActivity.this, "All shares are sold", Toast.LENGTH_SHORT).show();
                                } else {
                                    Transaction currentOwner = new Transaction(auth.getCurrentUser().getUid());
                                    if (auth.getCurrentUser() != null) {
                                        if (owners.size() != 0) {
                                            for (Transaction owner : owners) {
                                                if (owner.getBoughtBy().equals(auth.getCurrentUser().getUid())) {
                                                    currentOwner = owner;
                                                    no_of_shares = owner.getNo_of_shares_bought();
                                                    break;
                                                }
                                            }
                                            no_of_shares += 1;
                                            land.setNo_of_available_cuts(land.getNo_of_available_cuts() - 1);
                                            database.getReference().child("land").child(String.valueOf(land.getId())).child("no_of_available_cuts").setValue(land.getNo_of_available_cuts());

                                            currentOwner.setNo_of_shares_bought(currentOwner.getNo_of_shares_bought() + 1);
                                            int index = owners.indexOf(currentOwner) + 1;
                                            databaseReference = databaseReference.child(String.valueOf(index));
                                            databaseReference.child("no_of_shares").setValue(no_of_shares);
                                        } else {
                                            land.setNo_of_available_cuts(land.getNo_of_available_cuts() - 1);
                                            currentOwner.setNo_of_shares_bought(currentOwner.getNo_of_shares_bought() + 1);
                                            currentOwner.setinitial_buy_price(land.getCurrentPrice());


                                            database.getReference().child("land").child(String.valueOf(land.getId())).child("no_of_available_cuts").setValue(land.getNo_of_available_cuts());
                                            databaseReference.child(String.valueOf(owners.size() + 1)).child("bought_by").setValue(auth.getCurrentUser().getUid().toString());
                                            databaseReference.child(String.valueOf(owners.size() + 1)).child("initial_buy_price").setValue(land.getInitialPrice());
                                            databaseReference.child(String.valueOf(owners.size() + 1)).child("no_of_shares").setValue(no_of_shares);
                                            owners.add(currentOwner);
                                        }

                                        current_available_land_parts.setText((String.valueOf(land.getNo_of_available_cuts()) + "/100"));
                                        total_cuts_ofLand_bought_by_user.setText(String.valueOf(currentOwner.getNo_of_shares_bought()));
                                        total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice() * currentOwner.getNo_of_shares_bought())));

                                        land.setCurrentPrice(updateCurrentPrice(land,true));
                                        database.getReference().child("land").child(String.valueOf(land.getId())).child("currentPrice").setValue(land.getCurrentPrice());
                                        total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice() * currentOwner.getNo_of_shares_bought())));
                                        current_land_share_price.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice())));
                                    }
                                }
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(EachLandActivity.this, "transaction cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }).show();

            }
        });

        sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(owners.size()!=0){
                    new MaterialAlertDialogBuilder(EachLandActivity.this)
                            .setMessage("Are you sure you want to sell 1 share of this land")
                            .setTitle("Confirm")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(auth.getCurrentUser()!=null) {
                                        int no_of_shares = 0;
                                        databaseReference = database.getReference().child("land").child(String.valueOf(land.getId()));
                                        databaseReference = database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land");
                                        boolean exists = false;
                                        Transaction currentOwner = null;
                                        for (Transaction owner : owners){
                                            if(owner.getBoughtBy().equals(auth.getCurrentUser().getUid())){
                                                exists = true;
                                                currentOwner = owner;
                                                no_of_shares = owner.getNo_of_shares_bought();
                                                break;
                                            }
                                        }
                                        if(!exists) {
                                            Toast.makeText(EachLandActivity.this, "You have no shares in this property", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            if(currentOwner.getNo_of_shares_bought()>0){
                                                no_of_shares-=1;
                                                land.setNo_of_available_cuts(land.getNo_of_available_cuts()+1);
                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("no_of_available_cuts").setValue(land.getNo_of_available_cuts());
                                                currentOwner.setNo_of_shares_bought(currentOwner.getNo_of_shares_bought()-1);
                                                int index = owners.indexOf(currentOwner)+1;
                                                databaseReference  = databaseReference.child(String.valueOf(index));
                                                databaseReference.child("no_of_shares").setValue(no_of_shares);
                                            }else{
                                                Toast.makeText(EachLandActivity.this, "You have no shares in this property", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        current_available_land_parts.setText((String.valueOf(land.getNo_of_available_cuts())+"/100"));
                                        total_cuts_ofLand_bought_by_user.setText(String.valueOf(currentOwner.getNo_of_shares_bought()));
                                        total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol+String.valueOf(land.getCurrentPrice()*currentOwner.getNo_of_shares_bought())));

                                        land.setCurrentPrice(updateCurrentPrice(land,false));
                                        database.getReference().child("land").child(String.valueOf(land.getId())).child("currentPrice").setValue(land.getCurrentPrice());
                                        total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice() * currentOwner.getNo_of_shares_bought())));
                                        current_land_share_price.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice())));
                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(EachLandActivity.this, "transaction cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                }else{
                    Toast.makeText(EachLandActivity.this, "You have no shares in this property", Toast.LENGTH_SHORT).show();
                }

            }
        });

        show_your_acquirings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(owners.size());
                for(Transaction transaction:owners){
                    if(transaction.getBoughtBy().equals(auth.getCurrentUser().getUid().toString())){
                        int no_of_shares = transaction.getNo_of_shares_bought();
                        total_cuts_ofLand_bought_by_user.setText(String.valueOf(no_of_shares));
                        long current_value = no_of_shares*land.getCurrentPrice();
                        total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol+String.valueOf(current_value)));
                    }
                }
            }
        });
    }

    public long updateCurrentPrice(Land land, boolean bought){
        long currentPrice = land.getCurrentPrice();
        if(bought){
            //increment amount by 1%
            currentPrice += 1;
        }else{
            //decrement amount by 1%
            currentPrice -= 1;
        }
        return currentPrice;
    }

    public ArrayList<Transaction> getOwnersOfLand(Land land){
        ArrayList<Transaction> owners = new ArrayList<>();
        databaseReference = database.getReference().child("land").child(String.valueOf(land.getId()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("users_who_bought_current_land").exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child("users_who_bought_current_land").getChildren()) {
                        Transaction individual_owner = new Transaction();
                        individual_owner.setBoughtBy(dataSnapshot.child("bought_by").getValue().toString());
                        if (dataSnapshot.child("Current_price_for_each_share").getValue() != null)
                            individual_owner.setinitial_buy_price((long) dataSnapshot.child("Current_price_for_each_share").getValue());
                        else
                            individual_owner.setinitial_buy_price(land.getCurrentPrice());
                        if (dataSnapshot.child("no_of_shares").getValue() != null)
                            individual_owner.setNo_of_shares_bought(((Long) dataSnapshot.child("no_of_shares").getValue()).intValue());
                        else
                            individual_owner.setNo_of_shares_bought(0);

                        owners.add(individual_owner);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return owners;
    }

    public void updateLandDetails(Land land){
        land_name.setText(land.getName());
        land_location.setText(land.getLocation());
        current_land_share_price.setText((Constants.rupee_symbol+String.valueOf(land.getCurrentPrice())));
        current_available_land_parts.setText((String.valueOf(land.getNo_of_available_cuts())+"/100"));
        String image_path = land.getLocation().toLowerCase();
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