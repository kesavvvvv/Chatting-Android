package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton, loginButton;
    private EditText emailEditText, username, password;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef, Users;

    private Toolbar mToolbar;

    private String currentUserId, userName, emailId, pass1, pass2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolbar = (Toolbar) findViewById(R.id.register_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Welcome!");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        Users = FirebaseDatabase.getInstance().getReference().child("Users");

        //Toast.makeText(this, currentUserId, Toast.LENGTH_SHORT).show();

        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);



        username = (EditText) findViewById(R.id.userEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);

        registerButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  final String user = username.getText().toString();

                  userName = user;

                  Toast.makeText(RegisterActivity.this, userName, Toast.LENGTH_SHORT).show();
                  String email = emailEditText.getText().toString();

                  emailId = email;

                  String pass = password.getText().toString();

                  pass2 = pass;

                  if (TextUtils.isEmpty(email))
                      Toast.makeText(RegisterActivity.this, "Enter an email id", Toast.LENGTH_SHORT).show();
                  else if (TextUtils.isEmpty((user)))
                      Toast.makeText(RegisterActivity.this, "Enter a username", Toast.LENGTH_SHORT).show();
                  else if (TextUtils.isEmpty(pass))
                      Toast.makeText(RegisterActivity.this, "Enter a password", Toast.LENGTH_SHORT).show();
                  else {


                      Toast.makeText(RegisterActivity.this, "come here!", Toast.LENGTH_SHORT).show();
                      mAuth.createUserWithEmailAndPassword(email, pass)
                              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {

                                      if (task.isSuccessful()) {

                                          Toast.makeText(RegisterActivity.this, "working!", Toast.LENGTH_SHORT).show();

                                          String currentUserID = currentUser.getUid();
                                          RootRef.child("Users").child(currentUserID).setValue("");

                                          HashMap<String, String> profileMap = new HashMap<>();
                                            profileMap.put("name", user);
                                            profileMap.put("email", emailId);
                                            profileMap.put("psdd", pass2);
                                          Toast.makeText(RegisterActivity.this, "working2!", Toast.LENGTH_SHORT).show();
                                          RootRef.child("Users").child(currentUserID).setValue(profileMap)
                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<Void> task) {

                                                          if (task.isSuccessful())
                                                              Toast.makeText(RegisterActivity.this, "username added", Toast.LENGTH_SHORT).show();
                                                          else {
                                                              String message = task.getException().toString();

                                                              Toast.makeText(RegisterActivity.this, "Account not Created " + message, Toast.LENGTH_SHORT).show();
                                                          }

                                                      }
                                                  });

                                          Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                      } else {
                                          String message = task.getException().toString();

                                          Toast.makeText(RegisterActivity.this, "Account not Created " + message, Toast.LENGTH_SHORT).show();
                                      }




                                  }
                              });
                  }
              }

          });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = username.getText().toString();
                String email = emailEditText.getText().toString();
                emailId = email;
                String pass = password.getText().toString();
                pass1 = pass;

                if(TextUtils.isEmpty(email))
                    Toast.makeText(RegisterActivity.this, "Enter an email id", Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(pass))
                    Toast.makeText(RegisterActivity.this, "Enter a password", Toast.LENGTH_SHORT).show();
                else
                {
                    Users = FirebaseDatabase.getInstance().getReference().child("Users");

                    Users.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            if(dataSnapshot.exists())
                            {

                                Set<String> set = new HashSet<>();
                                Iterator iterator = dataSnapshot.getChildren().iterator();

                                while (iterator.hasNext())
                                {
                                    String email1 = (String) ((DataSnapshot)iterator.next()).getValue();
                                    String user1 = (String) ((DataSnapshot)iterator.next()).getValue();
                                    String pass3 = (String) ((DataSnapshot)iterator.next()).getValue();

                                    if(TextUtils.equals(user1, user))
                                    if((TextUtils.equals(emailId, email1) && TextUtils.equals(user1, user)))
                                    {

                                        mAuth.signInWithEmailAndPassword(emailId, pass1)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                                        if(task.isSuccessful()) {

                                                            Intent chatActivityIntent = new Intent(RegisterActivity.this, ChatActivity.class);
                                                            Toast.makeText(RegisterActivity.this, user, Toast.LENGTH_SHORT).show();
                                                            chatActivityIntent.putExtra("userName", user);
                                                            startActivity(chatActivityIntent);

                                                            Toast.makeText(RegisterActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();



                                                        }
                                                        else {
                                                            String message = task.getException().toString();

                                                            Toast.makeText(RegisterActivity.this, "Login Unsuccessful " + message, Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });


                                    }
                                    else
                                        Toast.makeText(RegisterActivity.this, "Username does not match!", Toast.LENGTH_SHORT).show();



                                }


                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }
        });
    }
}
