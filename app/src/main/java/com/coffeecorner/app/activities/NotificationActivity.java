package com.coffeecorner.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.adapters.NotificationAdapter;
import com.coffeecorner.app.models.Notification;
import com.coffeecorner.app.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize toolbar
        setupToolbar();

        // Initialize notifications list
        setupNotificationsRecyclerView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        ImageButton btnClearAll = findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(v -> clearAllNotifications());
    }

    private void setupNotificationsRecyclerView() {
        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        // Create sample notifications data
        List<Notification> notifications = getSampleNotifications();

        // Initialize adapter
        notificationAdapter = new NotificationAdapter(this, notifications);
        notificationAdapter.setOnNotificationActionListener(new NotificationAdapter.OnNotificationActionListener() {
            @Override
            public void onNotificationClicked(Notification notification, int position) {
                handleNotificationClick(notification);
            }

            @Override
            public void onDeleteClicked(Notification notification, int position) {
                deleteNotification(notification, position);
            }
        });

        rvNotifications.setAdapter(notificationAdapter);
    }

    private List<Notification> getSampleNotifications() {
        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification(
                "Order Confirmed",
                "Your order #CC-19052 has been confirmed and is being prepared.",
                "10 minutes ago",
                Notification.TYPE_ORDER,
                "CC-19052",
                false));

        notifications.add(new Notification(
                "Special Offer",
                "Buy one get one free on all Signature Blends this weekend!",
                "2 hours ago",
                Notification.TYPE_PROMOTION,
                null,
                false));

        notifications.add(new Notification(
                "Points Added",
                "You earned 75 points from your recent purchase.",
                "Yesterday",
                Notification.TYPE_REWARD,
                null,
                true));

        notifications.add(new Notification(
                "New Seasonal Menu",
                "Our Summer menu is now available. Try our new Mango Tango Smoothie!",
                "2 days ago",
                Notification.TYPE_NEWS,
                null,
                true));

        notifications.add(new Notification(
                "Free Birthday Drink",
                "Your birthday is coming up! Don't forget to claim your free signature drink.",
                "1 week ago",
                Notification.TYPE_REWARD,
                null,
                true));

        return notifications;
    }

    private void handleNotificationClick(Notification notification) {
        // In a real app, we would open the relevant screen based on notification type
        // For now, just mark as read and show a toast
        notification.setRead(true);
        notificationAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Opened: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void deleteNotification(Notification notification, int position) {
        // Delete this notification
        notificationAdapter.removeItem(position);

        Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
    }

    private void clearAllNotifications() {
        // Clear all notifications
        notificationAdapter.clearAll();

        Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
    }
}
