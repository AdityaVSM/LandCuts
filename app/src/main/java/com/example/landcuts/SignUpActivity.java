package com.example.landcuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.landcuts.Models.User;
import com.example.landcuts.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText reg_name_field,reg_email_field,reg_password_field,confirm_reg_password_field;
    Button register_button;
    TextView txt_login;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        reg_name_field = findViewById(R.id.reg_name_field);
        reg_email_field = findViewById(R.id.reg_email_field);
        reg_password_field = findViewById(R.id.reg_password_field);
        confirm_reg_password_field = findViewById(R.id.confirm_reg_password_field);
        register_button = findViewById(R.id.register_button);
        txt_login = findViewById(R.id.txt_login);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = reg_name_field.getText().toString();
                String email = reg_email_field.getText().toString();
                String password = reg_password_field.getText().toString();
                String confirm_password = confirm_reg_password_field.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_password.isEmpty())
                    Toast.makeText(SignUpActivity.this, "One or more fields are empty", Toast.LENGTH_SHORT).show();
                else if(!email.matches(Constants.email_pattern))
                    reg_email_field.setError("Enter valid email");
                else if(password.length()<6 || confirm_password.length()<6){
                    reg_password_field.setError("Length should be minimum 6");
                    confirm_reg_password_field.setError("Length should be minimum 6");
                }else if(!password.equals(confirm_password))
                    confirm_reg_password_field.setError("Password didn't match");
                else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                //Store data in database
                                DatabaseReference databaseReference = database.getReference().child("user").child(auth.getUid());
                                User current_user = new User(name,auth.getUid(),email);
                                databaseReference.setValue(current_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                                        }else
                                            Toast.makeText(SignUpActivity.this, "Unable to create user", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(SignUpActivity.this, "Unable to create user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}