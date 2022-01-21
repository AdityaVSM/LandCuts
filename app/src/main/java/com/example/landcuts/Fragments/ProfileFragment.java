package com.example.landcuts.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.landcuts.Adapters.LandViewAdapter;
import com.example.landcuts.Constants.Constants;
import com.example.landcuts.EachLandActivity;
import com.example.landcuts.Models.Land;
import com.example.landcuts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    TextView user_name, profit_bal_in_profile_fragment, invested_bal_in_profile_fragment, current_bal_in_profile_fragment;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LandViewAdapter landViewAdapter;
    ListView diff_land_list_view_bought_by_user;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        user_name = view.findViewById(R.id.user_name);
        profit_bal_in_profile_fragment = view.findViewById(R.id.profit_bal_in_profile_fragment);
        invested_bal_in_profile_fragment = view.findViewById(R.id.invested_bal_in_profile_fragment);
        current_bal_in_profile_fragment = view.findViewById(R.id.current_bal_in_profile_fragment);
        diff_land_list_view_bought_by_user = view.findViewById(R.id.diff_land_list_view_bought_by_user);

        ArrayList arrayList = new ArrayList<>();
        landViewAdapter = new LandViewAdapter(getActivity().getApplicationContext(), arrayList);
        diff_land_list_view_bought_by_user.setAdapter(landViewAdapter);

        getBasicUserData();
        getLandBoughtByUser();

        diff_land_list_view_bought_by_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Land land = (Land) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), EachLandActivity.class);
                intent.putExtra("land", land);
                startActivity(intent);
//                System.out.println(land.getName());
            }
        });

        return view;
    }

    public void getBasicUserData(){
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
                String profit = "0";
                if((calculateProfit(current_balance, invested)!=null))
                    profit = (calculateProfit(current_balance, invested));
                profit_bal_in_profile_fragment.setText((profit+Constants.percent_symbol));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getLandBoughtByUser(){
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("land");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                landViewAdapter.clear();
                for(DataSnapshot landSnapshot : snapshot.getChildren()){
                    if(landSnapshot.child("users_who_bought_current_land").exists()){
                        System.out.println("land is bought");
                        for (DataSnapshot eachLandInfo : landSnapshot.child("users_who_bought_current_land").getChildren()){
                            if(eachLandInfo.child("bought_by").getValue().toString().equals(auth.getCurrentUser().getUid())){
                                System.out.println("land is bought by current user");
                                String name = landSnapshot.child("name").getValue().toString();
                                String location = landSnapshot.child("location").getValue().toString();
                                long initialPrice, currentPrice;
                                if(landSnapshot.child("initialPrice").getValue()!=null)
                                    initialPrice = (long)landSnapshot.child("initialPrice").getValue();
                                else
                                    initialPrice = 0;
                                if(landSnapshot.child("currentPrice").getValue()!=null)
                                    currentPrice = (long)landSnapshot.child("currentPrice").getValue();
                                else
                                    currentPrice = 0;
                                Land land = new Land(name,location,initialPrice);
                                land.setCurrentPrice(currentPrice);
                                if(landSnapshot.child("no_of_available_cuts").getValue()!=null)
                                    land.setNo_of_available_cuts(((Long)landSnapshot.child("no_of_available_cuts").getValue()).intValue());
                                else
                                    land.setNo_of_available_cuts(100);
                                land.setId(((Long)landSnapshot.child("id").getValue()).intValue());
                                landViewAdapter.add(land);
                                landViewAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String calculateProfit(long current_balance, long invested){
        double profit_percentage = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        if(invested!=0){
            long profit = current_balance - invested;
            profit_percentage = ((double)Math.abs(profit)/(double) invested) * 100;
            String profit_str = df.format(profit_percentage);
            if(profit>0) {
                profit_bal_in_profile_fragment.setTextColor(Color.GREEN);
                return profit_str;
            }
            else {
                profit_bal_in_profile_fragment.setTextColor(Color.RED);
                return "-"+profit_str;
            }
        }
        return null;
    }
}