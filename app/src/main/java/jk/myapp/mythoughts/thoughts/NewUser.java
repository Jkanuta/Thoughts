package jk.myapp.mythoughts.thoughts;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jk.myapp.mythoughts.R;

public class NewUser extends AppCompatActivity {

    TextInputEditText firstName, userName, email, password;
    Button createUser;

    ProgressDialog progressDialog;

    DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        getSupportActionBar().hide();


        firstName = findViewById(R.id.firstNameText);
        userName = findViewById(R.id.userNameText);
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        createUser = findViewById(R.id.xmlCreateAccountButton);



        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String dataBaseName = firstName.getText().toString();
                progressDialog.show();
                progressDialog.setMessage("Please wait, creating your account!");
                mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            SharedPreferences sharedPreferences = getSharedPreferences("USERINFO", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", firstName.getText().toString());
                            editor.putString("userNameForDatabase", userName.getText().toString().trim());
                            editor.apply();

                            databaseReference.child(userName.getText().toString());
                            databaseReference.child(userName.getText().toString()).child("Email").setValue(email.getText().toString());

                            progressDialog.dismiss();

                            firstName.setText("");
                            userName.setText("");
                            password.setText("");
                            email.setText("");

                            Toast.makeText(NewUser.this, "You can now sign in with your Username", Toast.LENGTH_SHORT).show();
                        }else{
                            String errorMessage = task.getException().toString();
                            Toast.makeText(NewUser.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}
