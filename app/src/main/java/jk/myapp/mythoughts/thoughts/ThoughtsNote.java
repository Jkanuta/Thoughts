package jk.myapp.mythoughts.thoughts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jk.myapp.mythoughts.R;

public class ThoughtsNote extends AppCompatActivity {

    Button saveButton;
    EditText thoughtsTitle, thoughtsText;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    String currentItemHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts_note);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPref = getSharedPreferences("USERINFORMATION", MODE_PRIVATE);
        final String signInNames = sharedPref.getString("nameofwork", "");


        saveButton = findViewById(R.id.saveButton);
        thoughtsText = findViewById(R.id.thoughtsText);
        thoughtsTitle = findViewById(R.id.thoughtsTitle);

        databaseReference.child(signInNames).child("Title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    currentItemHere  = getIntent().getExtras().get("item").toString();
                    if (dataSnapshot.hasChild(currentItemHere)){
                        databaseReference.child(signInNames).child("Title").child(currentItemHere).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                thoughtsTitle.setText(currentItemHere);
                                thoughtsText.setText(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = getSharedPreferences("USERINFORMATION", MODE_PRIVATE);
                String signInNames = sharedPref.getString("nameofwork", "");


                String databaseThoughtsTitle = thoughtsTitle.getText().toString();
                String databaseThoughtsText = thoughtsText.getText().toString();


                databaseReference.child(signInNames).child("Title");

                databaseReference.child(signInNames).child("Title").child(databaseThoughtsTitle);

                databaseReference.child(signInNames).child("Title").child(databaseThoughtsTitle).setValue(databaseThoughtsText).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ThoughtsNote.this, "Saved", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ThoughtsNote.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

        });
    }
}
