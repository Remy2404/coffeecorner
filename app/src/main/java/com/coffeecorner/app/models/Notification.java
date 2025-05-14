package com.coffeecorner.app.models;

public class Notification {
    public static final int TYPE_ORDER = 1;
    public static final int TYPE_PROMOTION = 2;
    public static final int TYPE_REWARD = 3;
    public static final int TYPE_NEWS = 4;
    
    private String title;
    private String content;
    private String timeAgo;
    private int type;
    private String referenceId; // For orders, etc.
    private boolean isRead;

    public Notification(String title, String content, String timeAgo, int type, String referenceId, boolean isRead) {
        this.title = title;
        this.content = content;
        this.timeAgo = timeAgo;
        this.type = type;
        this.referenceId = referenceId;
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
    
    public int getIconResource() {
        switch (type) {
            case TYPE_ORDER:
                return R.drawable.ic_order_notification;
            case TYPE_PROMOTION:
                return R.drawable.ic_promotion;
            case TYPE_REWARD:
                return R.drawable.ic_reward;
            case TYPE_NEWS:
                return R.drawable.ic_news;
            default:
                return R.drawable.ic_notification;
        }
    }
    
    public int getBackgroundTint() {
        switch (type) {
            case TYPE_ORDER:
                return R.color.notification_order;
            case TYPE_PROMOTION:
                return R.color.notification_promotion;
            case TYPE_REWARD:
                return R.color.notification_reward;
            case TYPE_NEWS:
                return R.color.notification_news;
            default:
                return R.color.notification_default;
        }
    }
}
