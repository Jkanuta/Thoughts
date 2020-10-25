package jk.myapp.mythoughts.thoughts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jk.myapp.mythoughts.R;

public class ThoughtsHome extends AppCompatActivity {

    ImageView addImageView;
    TextView myTextView;

    ListView myListView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> list_of_thoughts = new ArrayList<>();

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts_home);

        addImageView = findViewById(R.id.xmlImageView);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        addImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                startActivity(new Intent(ThoughtsHome.this, ThoughtsNote.class));

                return false;
            }
        });

        InitializeFields();

        RetrieveAndDisplayThoughts();

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ThoughtsHome.this, R.style.AlertDialog);
                builder.setTitle("Are you sure you want to delete? ");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String currentItem = parent.getItemAtPosition(position).toString();
                        SharedPreferences sharedPref = getSharedPreferences("USERINFORMATION", MODE_PRIVATE);
                        final String signInNames = sharedPref.getString("nameofwork", "");
                        list_of_thoughts.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        databaseReference.child(signInNames).child("Title").child(currentItem).removeValue();
                        Toast.makeText(ThoughtsHome.this, "Deleted successfully!", Toast.LENGTH_SHORT).show();

                    }

                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return true;
            }
        });


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentItem = parent.getItemAtPosition(position).toString();
                Intent itemIntent = new Intent(ThoughtsHome.this, ThoughtsNote.class);
                itemIntent.putExtra("item", currentItem);
                startActivity(itemIntent);

            }
        });

    }
    private void RetrieveAndDisplayThoughts() {

        SharedPreferences sharedPref = getSharedPreferences("USERINFORMATION", MODE_PRIVATE);
        String signInNames = sharedPref.getString("nameofwork", "");

        databaseReference.child(signInNames).child("Title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    myTextView.setAlpha(0);

                    Set<String> set = new HashSet<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        set.add(((DataSnapshot)iterator.next()).getKey());
                    }
                    list_of_thoughts.clear();
                    list_of_thoughts.addAll(set);
                    arrayAdapter.notifyDataSetChanged();

                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void InitializeFields() {
        myListView = findViewById(R.id.myListView);
        myTextView = findViewById(R.id.noThoughtsTextView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_thoughts );
        myListView.setAdapter(arrayAdapter);

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
