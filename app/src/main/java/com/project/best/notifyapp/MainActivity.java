package com.project.best.notifyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

//    Variable untuk button
    private Button mNotifyButton;
    private Button mUpdateButton;
    private Button mNCancelButton;

//    Notifikasi
    private NotificationManager notificationManager;
//    Id dari notifikasi
    private static final int NOTIFICATION_ID = 0;
//    Id Chanel
    private static final String CHANNEL_ID = "ch1";
//    Lable untuk tombol learn more (Memanggil url)
    private static final String NOTIF_URL = "http://google.co.id";
//    Label untuk tombol update di notifikasi
    private static final String ACTION_UPDATE_NOTIFICATION =
            BuildConfig.APPLICATION_ID + ".ACTION_UPDATE_NOTIFICATION";
//    Label untuk tombol cancel di notifikasi
    private static final String ACTION_CANCEL_NOTIFICATION =
            BuildConfig.APPLICATION_ID + ".ACTION_CANCEL_NOTIFICATION";

    private NotificationReceiver notificationReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Interaksi dengan view
        setContentView(R.layout.activity_main);
        mNotifyButton = findViewById(R.id.notifyButton);
        mUpdateButton = findViewById(R.id.updateButton);
        mNCancelButton = findViewById(R.id.cancelButton);
//        Variable untuk memangemen notifikasi
        notificationManager = ( NotificationManager )getSystemService(NOTIFICATION_SERVICE);
//        Broadcast Receiver untuk menerima data/aksi dari notifikasi
        createNotificationChannel();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION);
        intentFilter.addAction(ACTION_CANCEL_NOTIFICATION);
        registerReceiver(notificationReceiver, intentFilter);
//        membuat fungsi klik tombol :
        mNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });
        mNCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });
    }


//    Fungsi untuk membuat channer notifikasi -> yang nantinya user dapat mengatur
//    (channel notifikasi mana saya yang akan aktif)

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Nama Channel
            CharSequence name = "Channel 1";
//            deskripsi dari channel
            String desc = "Description ... ";
//            memiliki prioritas ke dua. (Termasuk prioritas tinggi)
            int importance = NotificationManager.IMPORTANCE_HIGH;
//            membuat object chanel (untuk membuat chanel lebih dari satu
//            dapat di inisiasi object lagi, jangan lupa membuat label id notifikasi dan chanel baru)
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(desc);
//            Menayangkan / membuat chanel
            notificationManager.createNotificationChannel(channel);
        }
    }

    //    Mengirimkan notifikasi dengan data tertentu dari chanel yang dibuat
    private void sendNotification() {
//        Jika tombol action dipencet (yang dinotifikasi)
//        Ketika notifikasi di pencet akan menuju main activity
        Intent notifInten = new Intent( this, MainActivity.class);
        PendingIntent notifPendingIntent = PendingIntent.getActivity( this, NOTIFICATION_ID,
                notifInten, PendingIntent.FLAG_UPDATE_CURRENT ); // FLAG_UPDATE_CURRENT => yang bisa di klik berkali kali

//         Ketika tombol learn More yang dinotifikasi di pencet akan menuju url
        Intent moreIntent = new Intent( Intent.ACTION_VIEW, Uri.parse(NOTIF_URL));
        PendingIntent morePendingIntent = PendingIntent.getActivity( this, NOTIFICATION_ID,
                moreIntent, PendingIntent.FLAG_ONE_SHOT); // FLAG_ONE_SHOT => hanya bisa diklik satu kali

//         Ketika tombol update yang dinotifikasi di pencet akan memunculkan notifikasi update
        Intent updateIntent = new Intent( ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getActivity( this, NOTIFICATION_ID,
                updateIntent, PendingIntent.FLAG_ONE_SHOT); // FLAG_ONE_SHOT => hanya bisa diklik satu kali

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Notify Title")
                .setContentText("Notif Content")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .addAction(R.mipmap.ic_launcher, "Learn more", morePendingIntent)
                .addAction(R.mipmap.ic_launcher, "Update", updatePendingIntent)
                .setContentIntent(notifPendingIntent);

        Notification notification = notifyBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    //    Mengirimkan notifikasi dengan data tertentu dari chanel yang dibuat
    private void updateNotification() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    //         Ketika tombol learn More yang dinotifikasi di pencet akan menuju url
        Intent cancelIntent = new Intent( ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent = PendingIntent.getActivity( this, NOTIFICATION_ID,
                cancelIntent, PendingIntent.FLAG_ONE_SHOT); // FLAG_ONE_SHOT => hanya bisa diklik satu kali

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Update Title")
                .setContentText("Update Content")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .addAction(R.mipmap.ic_launcher, "Cancel", cancelPendingIntent)
                .setStyle( new NotificationCompat.BigPictureStyle().bigPicture(image)
                        .setBigContentTitle("Notification Update !!"));

        Notification notification = notifyBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    //    Mengirimkan notifikasi dengan data tertentu dari chanel yang dibuat
    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

//    Untuk meboradcast sehingga tombol action (update dan cancel) dapat diakses
    private class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_UPDATE_NOTIFICATION:
                    updateNotification();
                    break;
                case ACTION_CANCEL_NOTIFICATION:
                    cancelNotification();
                    break;
            }
        }
    }

    private static class BuildConfig {
        public static final String APPLICATION_ID = "";
    }

}
