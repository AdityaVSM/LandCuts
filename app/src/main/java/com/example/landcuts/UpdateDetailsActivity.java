package com.example.landcuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateDetailsActivity extends AppCompatActivity {
    TextInputEditText user_name_in_settings, user_email_in_settings;
    Button update_user_details;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("user").child(auth.getCurrentUser().getUid());

        user_name_in_settings = findViewById(R.id.user_name_in_settings);
        user_email_in_settings = findViewById(R.id.user_email_in_settings);
        update_user_details = findViewById(R.id.update_user_details);

        getUserData();


        update_user_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_name = user_name_in_settings.getText().toString();
                String new_email = user_email_in_settings.getText().toString();
                new MaterialAlertDialogBuilder(UpdateDetailsActivity.this)
                        .setTitle("Confirm changes")
                        .setMessage("Are you sure want to updateprofile data")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child("name").setValue(new_name);
                                databaseReference.child("email").setValue(new_email);
                                Toast.makeText(UpdateDetailsActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

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