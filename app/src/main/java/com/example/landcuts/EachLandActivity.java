package com.example.landcuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.landcuts.Constants.Constants;
import com.example.landcuts.Models.Land;
import com.example.landcuts.Models.Transaction;
import com.example.landcuts.Models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EachLandActivity extends AppCompatActivity {

    TextView current_available_land_parts, current_land_share_price,land_name, land_location,
            total_price_ofShare_bought_by_user, total_cuts_ofLand_bought_by_user, show_your_acquirings, total_invested_by_user;
    ImageView land_image_view;
    Button buy_button, sell_button;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    LinearLayout acquirings_data;
    Switch show_your_acquirings_switch;


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
        total_invested_by_user = findViewById(R.id.total_invested_by_user);
        acquirings_data = findViewById(R.id.acquirings_data);
        show_your_acquirings_switch = findViewById(R.id.show_your_acquirings_switch);

        Intent intent = getIntent();
        Land land = (Land) intent.getSerializableExtra("land");

        databaseReference = database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land");

        updateLandDetails(land);

        ArrayList<Transaction> owners =  getOwnersOfLand(land);

        User current_user = convert_firebaseUser_to_user();

        buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(EachLandActivity.this)
                        .setMessage("Are you sure you want to buy 1 share of this land")
                        .setTitle("Confirm")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int no_of_shares = 1;
                                if (land.getNo_of_available_cuts() == 0) {
                                    Toast.makeText(EachLandActivity.this, "All shares are sold", Toast.LENGTH_SHORT).show();
                                } else {
                                    Transaction currentOwner = new Transaction(auth.getCurrentUser().getUid().toString(),owners.size()+1);

                                    if (auth.getCurrentUser() != null) {
                                        boolean exists = false;
                                        if (owners.size() != 0) {
                                            for (Transaction owner : owners) {
                                                if (owner.getBoughtBy().equals(auth.getCurrentUser().getUid())) {
                                                    currentOwner = owner;
                                                    no_of_shares = owner.getNo_of_shares_bought();
                                                    currentOwner.settotal_invested(owner.gettotal_invested());
                                                    currentOwner.setNo_of_shares_bought(owner.getNo_of_shares_bought());
                                                    exists = true;
                                                    break;
                                                }
                                            }
                                            if(!exists){
                                                databaseReference.child(String.valueOf(owners.size() + 1)).child("bought_by").setValue(auth.getCurrentUser().getUid().toString());
                                                currentOwner.setBoughtBy(auth.getCurrentUser().getUid());
                                                currentOwner.setNo_of_shares_bought(1);
                                                currentOwner.settotal_invested(land.getCurrentPrice());

                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land").child(String.valueOf(owners.size()+1)).child("bought_by").setValue(currentOwner.getBoughtBy());
                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land").child(String.valueOf(owners.size()+1)).child("id").setValue(String.valueOf(owners.size()+1));
                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land").child(String.valueOf(owners.size()+1)).child("no_of_shares").setValue(currentOwner.getNo_of_shares_bought());
                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land").child(String.valueOf(owners.size()+1)).child("total_invested").setValue(currentOwner.gettotal_invested());

                                                land.setNo_of_available_cuts(land.getNo_of_available_cuts() - 1);
                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("no_of_available_cuts").setValue(land.getNo_of_available_cuts());

                                                current_user.setInvested(land.getCurrentPrice());
                                                database.getReference().child("user").child(auth.getCurrentUser().getUid()).child("invested").setValue(current_user.getInvested());

                                                getCurrentBalanceOfCurrentUser(current_user,true,land);

                                                owners.add(currentOwner);
                                            }
                                            else {
                                                land.setNo_of_available_cuts(land.getNo_of_available_cuts() - 1);
                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("no_of_available_cuts").setValue(land.getNo_of_available_cuts());
                                                DatabaseReference databaseReference1 = database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land");

                                                currentOwner.setNo_of_shares_bought(currentOwner.getNo_of_shares_bought() + 1);
                                                int index = owners.indexOf(currentOwner) + 1;
                                                databaseReference1.child(String.valueOf(index)).child("no_of_shares").setValue(currentOwner.getNo_of_shares_bought());

                                                databaseReference1.child(String.valueOf(index)).child("id").setValue(index);

                                                long already_invested = currentOwner.gettotal_invested();
                                                currentOwner.settotal_invested(already_invested + land.getCurrentPrice());
                                                databaseReference1.child(String.valueOf(index)).child("total_invested").setValue(already_invested + land.getCurrentPrice());

                                                long already_invested_whole = current_user.getInvested();
                                                current_user.setInvested(already_invested_whole + land.getCurrentPrice());
                                                database.getReference().child("user").child(auth.getCurrentUser().getUid()).child("invested").setValue(current_user.getInvested());

                                            }
                                        }
                                        else {
                                            land.setNo_of_available_cuts(land.getNo_of_available_cuts() - 1);
                                            database.getReference().child("land").child(String.valueOf(land.getId())).child("no_of_available_cuts").setValue(land.getNo_of_available_cuts());

                                            currentOwner.setNo_of_shares_bought(1);
                                            currentOwner.settotal_invested(land.getCurrentPrice());

                                            current_user.setInvested(current_user.getInvested()+land.getCurrentPrice());
                                            database.getReference().child("user").child(auth.getCurrentUser().getUid()).child("invested").setValue(current_user.getInvested());

                                            DatabaseReference databaseReference1 = database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land");

                                            databaseReference1.child(String.valueOf(currentOwner.getId())).child("id").setValue(currentOwner.getId());
                                            databaseReference1.child(String.valueOf(currentOwner.getId())).child("bought_by").setValue(currentOwner.getBoughtBy());
                                            databaseReference1.child(String.valueOf(currentOwner.getId())).child("total_invested").setValue(currentOwner.gettotal_invested());
                                            databaseReference1.child(String.valueOf(currentOwner.getId())).child("no_of_shares").setValue(currentOwner.getNo_of_shares_bought());
                                            owners.add(currentOwner);
                                        }


                                        current_available_land_parts.setText((String.valueOf(land.getNo_of_available_cuts()) + "/100"));
                                        total_cuts_ofLand_bought_by_user.setText(String.valueOf(currentOwner.getNo_of_shares_bought()));
                                        total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice() * currentOwner.getNo_of_shares_bought())));

                                        land.setCurrentPrice(updateCurrentPrice(land,true));
                                        database.getReference().child("land").child(String.valueOf(land.getId())).child("currentPrice").setValue(land.getCurrentPrice());


                                        getCurrentBalanceOfCurrentUser(current_user,true,land);
//                                        current_user.setCurrentBalance(current_bal);
////                                        current_user.setCurrentBalance(current_user.getCurrentBalance()+(land.getCurrentPrice()*currentOwner.getNo_of_shares_bought()));
//                                        database.getReference().child("user").child(auth.getCurrentUser().getUid()).child("currentBalance").setValue(current_user.getCurrentBalance());


                                        total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice() * currentOwner.getNo_of_shares_bought())));
                                        current_land_share_price.setText((Constants.rupee_symbol + String.valueOf(land.getCurrentPrice())));
                                        total_invested_by_user.setText((Constants.rupee_symbol + String.valueOf(currentOwner.gettotal_invested())));
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

                                                current_user.setInvested(current_user.getInvested()-land.getCurrentPrice());
                                                database.getReference().child("user").child(auth.getCurrentUser().getUid()).child("invested").setValue(current_user.getInvested());

                                                current_user.setCurrentBalance(current_user.getCurrentBalance()-land.getCurrentPrice());
                                                database.getReference().child("user").child(auth.getCurrentUser().getUid()).child("currentBalance").setValue(current_user.getCurrentBalance());

                                                currentOwner.settotal_invested(currentOwner.gettotal_invested()-land.getCurrentPrice());
                                                database.getReference().child("land").child(String.valueOf(land.getId())).child("users_who_bought_current_land").child(String.valueOf(owners.indexOf(currentOwner)+1)).child("total_invested").setValue(currentOwner.gettotal_invested());
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
                                        total_invested_by_user.setText((Constants.rupee_symbol + String.valueOf(currentOwner.gettotal_invested())));

                                        getCurrentBalanceOfCurrentUser(current_user,true,land);

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

        show_your_acquirings_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    int no_of_shares=0;
                    long current_value=0, invested=0;
                    for(Transaction transaction:owners){
                        if(transaction.getBoughtBy().equals(auth.getCurrentUser().getUid().toString())){
                            no_of_shares = transaction.getNo_of_shares_bought();
                            current_value = no_of_shares*land.getCurrentPrice();
                            invested = transaction.gettotal_invested();
                        }
                    }
                    total_cuts_ofLand_bought_by_user.setText(String.valueOf(no_of_shares));
                    total_price_ofShare_bought_by_user.setText((Constants.rupee_symbol+String.valueOf(current_value)));
                    total_invested_by_user.setText((Constants.rupee_symbol+String.valueOf(invested)));
                    acquirings_data.setVisibility(View.VISIBLE);
                }else{
                    acquirings_data.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    public void getCurrentBalanceOfCurrentUser(User current_user, boolean buy, Land land) {
        final long[] currentBalance = {0};
        DatabaseReference databaseReference = database.getReference().child("land");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    long land_current_price=0;
                    if(dataSnapshot.child("currentPrice").getValue()!=null)
                        land_current_price = (long) dataSnapshot.child("currentPrice").getValue();
                    if(dataSnapshot.child("users_who_bought_current_land").exists()){
                        for (DataSnapshot eachDataSnapshot : dataSnapshot.child("users_who_bought_current_land").getChildren()){
                            if (eachDataSnapshot.child("bought_by").getValue().toString().equals(auth.getCurrentUser().getUid())) {
                                long no_of_shares = 0;
                                if(eachDataSnapshot.child("no_of_shares").getValue()!=null)
                                    no_of_shares = (long) eachDataSnapshot.child("no_of_shares").getValue();
                                currentBalance[0] = currentBalance[0] + (no_of_shares * land_current_price);

                            }
                        }
                    }
                }
                current_user.setCurrentBalance(currentBalance[0]);
                database.getReference().child("user").child(auth.getCurrentUser().getUid()).child("currentBalance").setValue(currentBalance[0]);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public long updateCurrentPrice(Land land, boolean bought){
        long currentPrice = land.getCurrentPrice();
        if(bought){
            //increment amount by 1
            currentPrice += 1;
        }else{
            //decrement amount by 1
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
                        if (dataSnapshot.child("no_of_shares").getValue() != null)
                            individual_owner.setNo_of_shares_bought(((Long) dataSnapshot.child("no_of_shares").getValue()).intValue());
                        else
                            individual_owner.setNo_of_shares_bought(0);
                        if(dataSnapshot.child("total_invested").getValue() != null)
                            individual_owner.settotal_invested((long) dataSnapshot.child("total_invested").getValue());
                        else
                            individual_owner.settotal_invested(0);

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

    public User convert_firebaseUser_to_user(){
        User current_user = new User();
        DatabaseReference databaseReference = database.getReference().child("user");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                        String email, name;
                        long currentBalance = 0;
                        long invested = 0;

                        name = dataSnapshot.child("name").getValue().toString();
                        email = dataSnapshot.child("email").getValue().toString();
                        if(dataSnapshot.child("currentBalance").getValue() != null)
                            currentBalance = (long) dataSnapshot.child("currentBalance").getValue();
                        if(dataSnapshot.child("currentBalance").exists())
                            invested = (long) dataSnapshot.child("invested").getValue();

                        current_user.setName(name);
                        current_user.setEmail(email);
                        current_user.setCurrentBalance(currentBalance);
                        current_user.setInvested(invested);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return current_user;
    }


}