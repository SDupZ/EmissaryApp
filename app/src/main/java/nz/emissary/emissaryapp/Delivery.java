package nz.emissary.emissaryapp;

/**
 * Created by Simon on 3/03/2016.
 */
public class Delivery {
    private String uid;
    //private User originalLister;
    private String listingName;
    private String notes;

    private String pickupLocation;
    private String dropoffLocation;
    //private String expiryDate;

    //private User driver;
    //private Boolean hasDriver;

    //private long createdAt;

    public Delivery(){
    }

    public String getID() {
        return uid;
    }


    public void setID(String id) {
        this.uid = id;
    }

    /*public User getOriginalLister() {
        return originalLister;
    }*/

    /*
    public void setOriginalLister(User originalLister) {
        this.originalLister = originalLister;
    }*/

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

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    /*
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Boolean getHasDriver() {
        return hasDriver;
    }

    public void setHasDriver(Boolean hasDriver) {
        this.hasDriver = hasDriver;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }*/
}
