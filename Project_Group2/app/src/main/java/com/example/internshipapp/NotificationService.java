package com.example.internshipapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

public class NotificationService {

    private static final String CHANNEL_ID = "default_channel";

    public static void checkForUpdates(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            String role = userDoc.getString("role");

            db.collection("applications")
                    .whereEqualTo(role.equals("recruiter") ? "recruiterId" : "userId", userId)
                    .get()
                    .addOnSuccessListener(query -> {
                        for (DocumentSnapshot doc : query.getDocuments()) {
                            Boolean notified = doc.getBoolean("notified");
                            if (Boolean.TRUE.equals(notified)) continue;

                            String status = doc.getString("status");

                            if ("recruiter".equals(role)) {
                                showNotification(context, "New Application", "You have a new application.", 1);
                            } else if ("Accepted".equals(status) || "Declined".equals(status)) {
                                showNotification(context, "Interview " + status,
                                        "Your interview was " + status.toLowerCase() + ".", 2);
                            }

                            // Đánh dấu đã thông báo
                            db.collection("applications").document(doc.getId())
                                    .update("notified", true);
                        }
                    });
        });
    }

    public static void showNotification(Context context, String title, String message, int id) {
        if (!PermissionHelper.checkNotificationPermission(context)) {
            Log.w("Notification", "Permission not granted. Notification skipped.");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }

    static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "General Notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}



