package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.security.AccessController.getContext;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef, ChatRef;

    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> chat_list = new ArrayList<>();

    private Toolbar mToolbar;


    private String currentUserId, currentUserName, userName;
    private String currentDate, currentTime;
    private DatabaseReference Users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userName = getIntent().getExtras().get("userName").toString();

        //Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onCreate: strating service");
        

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyyMMdd");
        currentDate = currentDateFormat.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hhmms");
        currentTime = currentTimeFormat.format(callForTime.getTime());

        Log.d(TAG, "onCreate: " + currentTime);
        Log.d(TAG, "onCreate: passed calendar");
        Intent serviceIntent= new Intent(ChatActivity.this,NotificationService.class);
        serviceIntent.putExtra("token", userName);
        serviceIntent.putExtra("time", currentTime);
        serviceIntent.putExtra("date", currentDate);
        startService(serviceIntent);
        
        mToolbar = (Toolbar) findViewById(R.id.register_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chats");

        RootRef = FirebaseDatabase.getInstance().getReference();
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Chatlist");


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        currentUserName();

        list_view = (ListView) findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chat_list);
        list_view.setAdapter(arrayAdapter);

        //Toast.makeText(ChatActivity.this, "" + currentUserName, Toast.LENGTH_SHORT).show();

        ChatRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                chat_list.clear();
                chat_list.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String currentChatName = parent.getItemAtPosition(position).toString();

                Intent chatIntent = new Intent(ChatActivity.this, MessageActivity.class);
                chatIntent.putExtra("chatName", currentChatName);
                chatIntent.putExtra("userName", userName);
                startActivity(chatIntent);


            }
        });

    }

    private void currentUserName() {

        Users = FirebaseDatabase.getInstance().getReference().child("Users");

        Users.child(currentUserId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);



        if(item.getItemId() == R.id.log_out_option)
        {
            mAuth.signOut();
            Intent registerIntent = new Intent(ChatActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }
        if(item.getItemId() == R.id.new_chat_option)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.AlertDialogue);
            builder.setTitle("Enter Username");

            final EditText username = new EditText(ChatActivity.this);
            username.setHint("Username");
            builder.setView(username);

          //  Toast.makeText(ChatActivity.this, "" + currentUserName, Toast.LENGTH_SHORT).show();

            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String userName = username.getText().toString();
                //    Toast.makeText(ChatActivity.this, "" + currentUserName, Toast.LENGTH_SHORT).show();
                    RootRef.child("Chatlist").child(currentUserId).child(userName).setValue("")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                        Toast.makeText(ChatActivity.this, "Chat created successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });


            builder.show();

        }
        return true;

    }
}
