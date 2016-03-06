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
    //private String expiryDate;

    private String driver;
    private Boolean hasDriver;

    private double createdAt;

    public Delivery(){
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

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    /*
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }*/

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Boolean getHasDriver() {
        return hasDriver;
    }

    public void setHasDriver(Boolean hasDriver) {
        this.hasDriver = hasDriver;
    }

    public double getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(double createdAt) {
        this.createdAt = createdAt;
    }
}
