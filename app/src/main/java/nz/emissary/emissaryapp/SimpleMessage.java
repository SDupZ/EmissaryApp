package nz.emissary.emissaryapp;

/**
 * Created by Simon on 25/04/2016.
 */
public class SimpleMessage {
    private String senderId;
    private String recipientId;

    private String message;

    private long timeStamp;

    public SimpleMessage(){}

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
