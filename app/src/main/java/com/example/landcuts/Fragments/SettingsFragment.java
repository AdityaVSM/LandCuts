package com.example.landcuts.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.landcuts.LoginActivity;
import com.example.landcuts.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    TextInputEditText user_name_in_settings, user_email_in_settings;
    Button update_user_details;
    ImageButton logout_button;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("user").child(auth.getCurrentUser().getUid());

        user_name_in_settings = view.findViewById(R.id.user_name_in_settings);
        user_email_in_settings = view.findViewById(R.id.user_email_in_settings);
        logout_button = view.findViewById(R.id.logout_button);
        update_user_details = view.findViewById(R.id.update_user_details);

        getUserData();

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            }
        });

        update_user_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_name = user_name_in_settings.getText().toString();
                String new_email = user_email_in_settings.getText().toString();
                databaseReference.child("name").setValue(new_name);
                databaseReference.child("email").setValue(new_email);
            }
        });

        return view;
    }

    private void getUserData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            String name,email;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = (String) snapshot.child("name").getValue();
                email = (String) snapshot.child("email").getValue();
                System.out.println("settings"+name);
                user_name_in_settings.setText(name);
                user_email_in_settings.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
}