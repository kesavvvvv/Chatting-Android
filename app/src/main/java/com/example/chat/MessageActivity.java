package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MessageActivity extends AppCompatActivity {

    private EditText messageText;
    private Button sendButton, recordButton1;

    private ListView message_list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> chat_list = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference Users, Chats, ChatsKey;

    private String chatName, currentUserId, currentUserName, currentDate, currentTime, userName;

    private MediaRecorder recorder;

    private Toolbar mToolbar;

    private String fileName;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mStorage = FirebaseStorage.getInstance().getReference();

        fileName = getExternalCacheDir().getAbsolutePath() + "/recorded_audio.mp3";

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        chatName = getIntent().getExtras().get("chatName").toString();

        userName = getIntent().getExtras().get("userName").toString();

    /*    createNotificationChannel();



        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "lamubitA")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Testing mothafuckers")
                .setContentText("New Message Bitchesss")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

*/
        mToolbar = (Toolbar) findViewById(R.id.message_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(chatName);

        Users = FirebaseDatabase.getInstance().getReference().child("Users");

        Chats = FirebaseDatabase.getInstance().getReference().child("Chats");

        messageText = (EditText) findViewById(R.id.messageEditText);
        sendButton = (Button) findViewById(R.id.sendButton);

        message_list_view = (ListView) findViewById(R.id.messageListView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chat_list);
        message_list_view.setAdapter(arrayAdapter);

        Chats.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists())
                {

                    Set<String> set = new HashSet<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();

                    while (iterator.hasNext())
                    {
                        String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatReceiver = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatSender = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

                        //Toast.makeText(MessageActivity.this, chatReceiver, Toast.LENGTH_SHORT).show();
                        if((TextUtils.equals(chatSender, chatName) && TextUtils.equals(chatReceiver, userName)) || (TextUtils.equals(chatSender, userName) && TextUtils.equals(chatReceiver, chatName)))
                            set.add(chatSender + ": "+ chatMessage);
                    }

                    chat_list.addAll(set);
                    Toast.makeText(MessageActivity.this, "message sent", Toast.LENGTH_SHORT).show();
            //        notificationManager.notify(100, builder.build());
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists())
                {

                    Set<String> set = new HashSet<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();

                    while (iterator.hasNext())
                    {
                        String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatReceiver = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatSender = (String) ((DataSnapshot)iterator.next()).getValue();
                        String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

                        set.add(chatSender + ": " + chatMessage);
                    }

                    chat_list.addAll(set);
                    Toast.makeText(MessageActivity.this, "message received", Toast.LENGTH_SHORT).show();
          //          notificationManager.notify(100, builder.build());
                    arrayAdapter.notifyDataSetChanged();
                }

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


     //   Toast.makeText(this, currentUserId, Toast.LENGTH_SHORT).show();

   /*     Users.child(currentUserId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    getSupportActionBar().setTitle(currentUserId);
                    currentUserName = dataSnapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

*/

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(MessageActivity.this, "" + currentUserName, Toast.LENGTH_SHORT).show();

                String message = messageText.getText().toString();
                String messageKey = Chats.push().getKey();

                if(TextUtils.isEmpty(message))
                {
                    Toast.makeText(MessageActivity.this, "Please write message first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Calendar callForDate = Calendar.getInstance();
               //     SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyyMMdd");
                    currentDate = currentDateFormat.format(callForDate.getTime());

                    Calendar callForTime = Calendar.getInstance();
                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hhmms");

                    currentTime = currentTimeFormat.format(callForTime.getTime());

                    HashMap<String, Object> chatMessageKey = new HashMap<>();

                    Chats.updateChildren(chatMessageKey);

                    ChatsKey = Chats.child(messageKey);

                    //Toast.makeText(MessageActivity.this, currentUserId, Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> messageInfo = new HashMap<>();
                        messageInfo.put("sender", userName);
                        messageInfo.put("receiver", chatName);
                        messageInfo.put("message", message);
                        messageInfo.put("time", currentTime);
                        messageInfo.put("date", currentDate);

                     ChatsKey.updateChildren(messageInfo);

                }

                messageText.setText("");



            }
        });
    }

    private void createNotificationChannel() {

        //     if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.0) {
        CharSequence name = "channel";
        String description = "Channel for notification";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("lamubitA", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        //   }
    }
}

