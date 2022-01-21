package com.example.landcuts.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.landcuts.LoginActivity;
import com.example.landcuts.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    FirebaseAuth auth;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        ImageButton logout_button = view.findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            }
        });

        return view;
    }
}