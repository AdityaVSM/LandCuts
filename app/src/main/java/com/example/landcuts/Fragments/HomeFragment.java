package com.example.landcuts.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.landcuts.Adapters.LandViewAdapter;
import com.example.landcuts.Constants.Constants;
import com.example.landcuts.Models.Land;
import com.example.landcuts.Models.User;
import com.example.landcuts.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    FirebaseDatabase database;
    ListView diff_land_list_view;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        diff_land_list_view= view.findViewById(R.id.diff_land_list_view);
        final ArrayList<Land> arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("land");

//        DatabaseReference databaseReference = database.getReference().child("land").child("1");
//        databaseReference.setValue(new Land("Land1","Arizona",10000));
//
//        databaseReference = database.getReference().child("land").child("2");
//        databaseReference.setValue(new Land("Land2","UK",30000));
//
//        databaseReference = database.getReference().child("land").child("3");
//        databaseReference.setValue(new Land("Land3","Bengaluru",3000));
//
//        databaseReference = database.getReference().child("land").child("4");
//        databaseReference.setValue(new Land("Land4","Delhi",14230));
//
//        databaseReference = database.getReference().child("land").child("5");
//        databaseReference.setValue(new Land("Land5","Davanagere",901010));
//
//        databaseReference = database.getReference().child("land").child("6");
//        databaseReference.setValue(new Land("Land6","California",42000));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data_snapshot : snapshot.getChildren()){
                    String name = data_snapshot.child("name").getValue().toString();
                    String location = data_snapshot.child("location").getValue().toString();
                    long initialPrice, currentPrice;
                    if(data_snapshot.child("initialPrice").getValue()!=null)
                        initialPrice = (long)data_snapshot.child("initialPrice").getValue();
                    else
                        initialPrice = 0;
                    if(data_snapshot.child("currentPrice").getValue()!=null)
                        currentPrice = (long)data_snapshot.child("currentPrice").getValue();
                    else
                        currentPrice = 0;
                    Land land = new Land(name,location,initialPrice);

//                    land.setNo_of_available_cuts((int)data_snapshot.child("no_of_available_cuts").getValue());
//                    land.setCurrentPrice((long)data_snapshot.child("currentPrice").getValue());
                    arrayList.add(land);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        LandViewAdapter landViewAdapter = new LandViewAdapter(getActivity().getApplicationContext(), arrayList);


        diff_land_list_view.setAdapter(landViewAdapter);

        return view;
    }


}