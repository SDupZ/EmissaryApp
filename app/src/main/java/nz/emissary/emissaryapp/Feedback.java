package nz.emissary.emissaryapp;

/**
 * Created by Simon on 20/04/2016.
 */
public class Feedback {
    String uId;

    String userId;
    String deliveryId;

    String feedbackPosterId;
    boolean feedbackIsForDriver;

    double rating;
    String feedbackMessage;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getFeedbackPosterId() {
        return feedbackPosterId;
    }

    public void setFeedbackPosterId(String feedbackPosterId) {
        this.feedbackPosterId = feedbackPosterId;
    }

    public boolean isFeedbackIsForDriver() {
        return feedbackIsForDriver;
    }

    public void setFeedbackIsForDriver(boolean feedbackIsForDriver) {
        this.feedbackIsForDriver = feedbackIsForDriver;
    }
}
