package com.example.landcuts.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.landcuts.Adapters.LandViewAdapter;
import com.example.landcuts.Constants.Constants;
import com.example.landcuts.Models.Land;
import com.example.landcuts.R;
import com.example.landcuts.EachLandActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ListView diff_land_list_view;
    LandViewAdapter landViewAdapter;
    TextView current_worth_view;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        diff_land_list_view= view.findViewById(R.id.diff_land_list_view);
        current_worth_view = view.findViewById(R.id.current_worth_view);

        final ArrayList<Land> arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        landViewAdapter = new LandViewAdapter(getActivity().getApplicationContext(), arrayList);
        diff_land_list_view.setAdapter(landViewAdapter);

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
        set_land_view(landViewAdapter,database.getReference().child("land"));
        if(auth.getCurrentUser()!=null)
            set_current_worth(auth, database.getReference().child("user"));

        diff_land_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    public void set_land_view(LandViewAdapter landViewAdapter,DatabaseReference databaseReference){
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
                    land.setCurrentPrice(currentPrice);
                    if(data_snapshot.child("no_of_available_cuts").getValue()!=null)
                        land.setNo_of_available_cuts(((Long)data_snapshot.child("no_of_available_cuts").getValue()).intValue());
                    else
                        land.setNo_of_available_cuts(100);
                    if(data_snapshot.child("currentPrice").getValue()!=null)
                        land.setCurrentPrice((long)data_snapshot.child("currentPrice").getValue());
                    else
                        land.setCurrentPrice(initialPrice);
                    land.setId(((Long)data_snapshot.child("id").getValue()).intValue());
                    landViewAdapter.add(land);
                    landViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void set_current_worth(FirebaseAuth auth, DatabaseReference databaseReference){
        final long[] current_worth = {0};
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                        current_worth[0] = (long) dataSnapshot.child("currentBalance").getValue();
                        current_worth_view.setText((Constants.rupee_symbol+String.valueOf(current_worth[0]).toString()));
                        System.out.println(current_worth[0]);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}