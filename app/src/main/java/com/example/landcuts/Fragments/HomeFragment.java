package com.example.landcuts.Fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.landcuts.Adapters.LandViewAdapter;
import com.example.landcuts.Constants.Constants;
import com.example.landcuts.Models.Land;
import com.example.landcuts.Models.User;
import com.example.landcuts.R;
import com.example.landcuts.EachLandActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ArrayList<Land> arrayList;
    VideoView videoView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        diff_land_list_view= view.findViewById(R.id.diff_land_list_view);
        current_worth_view = view.findViewById(R.id.current_worth_view);
        videoView = view.findViewById(R.id.videoView);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        Uri uri = Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.test);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        arrayList = new ArrayList<>();
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
                landViewAdapter.clear();
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
        DatabaseReference user_database_reference = database.getReference().child("user");
        database.getReference().child("land").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentBalance = 0;
                for (DataSnapshot landSnapshot : snapshot.getChildren()){
                    if(landSnapshot.child("users_who_bought_current_land").exists()){
                        for (DataSnapshot eachLandInfo : landSnapshot.child("users_who_bought_current_land").getChildren()){
                            if(eachLandInfo.child("bought_by").getValue().toString().equals(auth.getCurrentUser().getUid())){
                                long currentPrice, no_of_cuts_bought;
                                if(landSnapshot.child("currentPrice").getValue()!=null)
                                    currentPrice = (long)landSnapshot.child("currentPrice").getValue();
                                else
                                    currentPrice = 0;
                                if(eachLandInfo.child("no_of_shares").getValue()!=null)
                                    no_of_cuts_bought = (long)eachLandInfo.child("no_of_shares").getValue();
                                else
                                    no_of_cuts_bought = 0;
                                currentBalance += no_of_cuts_bought*currentPrice;
                            }
                        }
                    }
                }
                current_worth_view.setText((Constants.rupee_symbol+String.valueOf(currentBalance)));
                user_database_reference.child(auth.getCurrentUser().getUid()).child("currentBalance").setValue(currentBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}