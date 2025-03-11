package com.example.powiadomienia02;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "progress_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        createNotificationChannel();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> showProgressNotification());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Progress Channel", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private void showProgressNotification() {
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Pobieranie pliku")
                .setContentText("Pobieranie w toku...")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)  // Utrzymuje powiadomienie aktywne
                .setProgress(100, 0, false);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Symulacja pobierania pliku w osobnym wątku
        new Thread(() -> {
            try {
                for (int progress = 0; progress <= 100; progress += 10) {
                    Thread.sleep(500); // Symulacja pobierania
                    builder.setProgress(100, progress, false);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                builder.setContentText("Pobieranie zakończone")
                        .setProgress(0, 0, false)
                        .setOngoing(false); // Usuń tryb ciągły
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
