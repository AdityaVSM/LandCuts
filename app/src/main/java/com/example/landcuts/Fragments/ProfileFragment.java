package com.example.landcuts.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.landcuts.Constants.Constants;
import com.example.landcuts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    TextView user_name, profit_bal_in_profile_fragment, invested_bal_in_profile_fragment, current_bal_in_profile_fragment;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        user_name = view.findViewById(R.id.user_name);
        profit_bal_in_profile_fragment = view.findViewById(R.id.profit_bal_in_profile_fragment);
        invested_bal_in_profile_fragment = view.findViewById(R.id.invested_bal_in_profile_fragment);
        current_bal_in_profile_fragment = view.findViewById(R.id.current_bal_in_profile_fragment);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        getData();
        return view;
    }

    public void getData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "";
                String email = "";
                long current_balance = 0,invested = 0;
                for(DataSnapshot data: snapshot.getChildren()){
                    if(data.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())) {
                        name = data.child("name").getValue().toString();
                        email = data.child("email").getValue().toString();
                        current_balance = (long)data.child("currentBalance").getValue();
                        invested = (long)data.child("invested").getValue();
                    }
                }
                user_name.setText(name);
                invested_bal_in_profile_fragment.setText((Constants.rupee_symbol +String.valueOf(invested)));
                current_bal_in_profile_fragment.setText((Constants.rupee_symbol+String.valueOf(current_balance)));
                profit_bal_in_profile_fragment.setText((String.valueOf(calculateProfit(current_balance, invested))+Constants.percent_symbol));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public double calculateProfit(long current_balance, long invested){
        double profit_percentage = 0;
        if(invested!=0){
            long profit = current_balance - invested;
            profit_percentage = ((double)Math.abs(profit)/(double) invested) * 100;

            if(profit>0) {
                profit_bal_in_profile_fragment.setTextColor(Color.GREEN);
                return profit_percentage;
            }
            else {
                profit_bal_in_profile_fragment.setTextColor(Color.RED);
                return -profit_percentage;
            }
        }
        return 0;
    }
}