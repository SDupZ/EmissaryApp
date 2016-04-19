package nz.emissary.emissaryapp;

/**
 * Created by Simon on 19/04/2016.
 */
public class UrgentMessage {

    private String uID;

    private String userId;
    private String message;
    private String deliveryId;

    public UrgentMessage(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }
}
