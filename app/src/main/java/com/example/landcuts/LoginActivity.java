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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email_address_field, password_field;
    Button login_button;
    TextView txt_signup;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_address_field = findViewById(R.id.email_address_field);
        password_field = findViewById(R.id.password_field);
        login_button = findViewById(R.id.login_button);
        txt_signup = findViewById(R.id.txt_signup);

        auth = FirebaseAuth.getInstance();


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_address_field.getText().toString();
                String password =password_field.getText().toString();

                if(email.isEmpty() && password.isEmpty()){
                    email_address_field.setError("This field can't be empty");
                    password_field.setError("This field can't be empty");
                }
                else if(email.isEmpty())
                    email_address_field.setError("This field can't be empty");
                else if(password.isEmpty())
                    password_field.setError("This field can't be empty");
                else if(!email.matches(Constants.email_pattern))
                    email_address_field.setError("Enter valid email");
                else if(password.length()<6)
                    password_field.setError("Length should be minimum 6");
                else{
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            }else{
                                Toast.makeText(LoginActivity.this, "Error while logging in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });



        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
    }
}