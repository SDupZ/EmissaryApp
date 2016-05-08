package nz.emissary.emissaryapp;

/**
 * Created by Simon on 3/03/2016.
 */
public class Delivery {
    private String uid;
    private String originalLister;
    private String listingName;
    private String notes;

    private String pickupLocation;
    private String dropoffLocation;

    private String pickupTime;
    private String dropoffTime;

    private String messageFromDriver;

    private double distance;

    //private String expiryDate;

    private String driver;
    private int status;

    private double createdAt;

    public Delivery(){
        status = Constants.STATUS_LISTED;
    }

    public String getID() {
        return uid;
    }

    public void setID(String id) {
        this.uid = id;
    }

    public String getOriginalLister() {
        return originalLister;
    }

    public void setOriginalLister(String originalLister) {
        this.originalLister = originalLister;
    }

    public String getListingName() {
        return listingName;
    }

    public void setListingName(String listingName) {
        this.listingName = listingName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation;}

    public String getMessageFromDriver() {
        return messageFromDriver;
    }

    public void setMessageFromDriver(String messageFromDriver) {
        this.messageFromDriver = messageFromDriver;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDropoffTime() {
        return dropoffTime;
    }

    public void setDropoffTime(String dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(double createdAt) {
        this.createdAt = createdAt;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
