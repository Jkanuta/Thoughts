package jk.myapp.mythoughts.thoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jk.myapp.mythoughts.R;

public class MainActivity extends AppCompatActivity {

    ImageView thoughtsImageView;
    TextView peaceTextView, newUser, forgotPassword;
    TextInputLayout containerUsername, containerePassword;
    TextInputEditText username, password;
    Button logIn;
    ProgressDialog progressDialog;

//    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);


        thoughtsImageView = findViewById(R.id.xmlThoughtsImageView);
        peaceTextView = findViewById(R.id.xmlPeaceTextView);
        containerUsername = findViewById(R.id.usernameTextViewLayout);
        containerePassword = findViewById(R.id.passwordTextViewLayout);
        username = findViewById(R.id.usernameTextViewText);
        password = findViewById(R.id.passwordTextViewText);
        logIn = findViewById(R.id.xmlLoginButton);
        newUser = findViewById(R.id.xmlNewUser);
        forgotPassword = findViewById(R.id.xmlForgotPassword);

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewUser.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signInWithUser();
                signIn();

            }
        });

        peaceTextView.animate().alpha(-1).setStartDelay(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                thoughtsImageView.animate().translationYBy(-500).setStartDelay(2000);
                peaceTextView.animate().translationYBy(-400).setStartDelay(2000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        newUser.animate().alpha(1).translationYBy(-100);
                        forgotPassword.animate().alpha(1).translationYBy(-100);
                        containerUsername.animate().alpha(1).translationYBy(-300);
                        containerePassword.animate().alpha(1).translationYBy(-300);
                        logIn.animate().alpha(1).translationYBy(-300);

                    }
                });
            }
        });
    }

    private void signIn() {
        //the aim is to make the app initiate login with the mAuth object
        //then after login, check the database

        //right now the app is checking the database for the Username string but the database needs authentication to allow access. ACCESS DENIED

        mAuth.signInWithEmailAndPassword(username.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


            }
        });

        databaseReference.child(username.getText().toString()).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.i("onDataChange", "Value here");
                if (dataSnapshot.exists()){
                    progressDialog.show();
                    progressDialog.setMessage("Logging in");
                    String emailAddress = dataSnapshot.child("Email").getValue().toString();
                    mAuth.signInWithEmailAndPassword(emailAddress, password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
//                                SharedPreferences sharedPreferences = getSharedPreferences("USERINFORMATION", MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("nameofwork", username.getText().toString());
//                                editor.apply();
                                progressDialog.dismiss();
                                startActivity(new Intent(MainActivity.this, ThoughtsHome.class));
                            }
                            else{
                                String error = task.getException().toString();
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "One ore more input field is wrong.", Toast.LENGTH_SHORT).show();
                                Log.i("Error login in", error);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void signInWithUser() {

        mAuth.signInWithEmailAndPassword(username.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()){
                    SharedPreferences sharedPreferences = getSharedPreferences("USERSIGNIN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("SIGNINNAME", username.getText().toString());
                    editor.apply();
                    startActivity(new Intent(MainActivity.this, ThoughtsHome.class));
                }else{
                    String errorMessage = task.getException().toString();
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
