package com.example.chat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";

    private DatabaseReference Chats;

    private String userName, time, date;

    private int timeInt, dateInt;

    public NotificationService() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "service started");

                createNotificationChannel();

                userName = intent.getStringExtra("token");
                time = intent.getStringExtra("time");
                date = intent.getStringExtra("date");

                Log.d(TAG, "run: does it pass here?" + time + date );

                Chats = FirebaseDatabase.getInstance().getReference().child("Chats");

                Chats.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if(dataSnapshot.exists()) {

                            Iterator iterator = dataSnapshot.getChildren().iterator();

                            while (iterator.hasNext()) {

                                String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
                                String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
                                String chatReceiver = (String) ((DataSnapshot) iterator.next()).getValue();
                                String chatSender = (String) ((DataSnapshot) iterator.next()).getValue();
                                String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();



                       //         if(TextUtils.equals(userName, chatReceiver) && (Integer.parseInt(time) <= Integer.parseInt(chatTime) && Integer.parseInt(date) <= Integer.parseInt(chatDate))) {

                                if(TextUtils.equals(userName, chatReceiver) && time.compareTo(chatTime) < 0 && date.compareTo(chatDate) <= 0) {
                                    Log.d(TAG, "onChildAdded: " + time + chatTime + chatDate + date);
                                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "lamubitA")
                                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                            .setContentTitle("New Message from: " + chatSender)
                                            .setContentText(chatMessage)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                    notificationManager.notify(100, builder.build());

                                }



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
        }).start();


        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        restartServiceIntent.putExtra("token", userName);
        restartServiceIntent.putExtra("time", time);
        restartServiceIntent.putExtra("date", date);
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
