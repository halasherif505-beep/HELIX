package Patient;
public class Notification implements Printable {
    private Notifiable recipient;
    private String message;
    private String notificationType;
    private String timestamp;

    public Notification(Notifiable recipient, String message, String notificationType, String timestamp) {
        this.recipient = recipient;
        this.message = (message != null && !message.isEmpty()) ? message : "No Message";
        this.notificationType = (notificationType != null && !notificationType.isEmpty()) ? notificationType : "General";
        this.timestamp = (timestamp != null && !timestamp.isEmpty()) ? timestamp : java.time.LocalDateTime.now().toString();
    }

    public void sendNotification() {
        if (recipient != null) recipient.receiveNotification(this);
    }

    public void scheduleNotification(String newTimestamp) {
        if (newTimestamp != null && !newTimestamp.isEmpty()) this.timestamp = newTimestamp;
    }

    @Override
    public void printInfo() {
        System.out.println("Notification → Message: " + message +
                ", Type: " + notificationType +
                ", Timestamp: " + timestamp);
    }
}
