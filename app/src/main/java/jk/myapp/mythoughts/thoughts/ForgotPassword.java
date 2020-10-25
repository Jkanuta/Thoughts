package jk.myapp.mythoughts.thoughts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import jk.myapp.mythoughts.R;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().hide();
    }
}
