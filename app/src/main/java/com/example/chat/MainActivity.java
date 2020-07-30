package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

      //  String current = currentUser.getEmail().toString();

     //   Toast.makeText(this, "email" + current, Toast.LENGTH_SHORT).show();
        //if(currentUser == null) {
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        //}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


}
