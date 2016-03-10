package nz.emissary.emissaryapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 3/03/2016.
 */
public class User {
    private String uID;
    private String name;
    private String username;
    private String phone;
    private String email;
    private Boolean isDriver;
    private Boolean emailVerified;

    private String provider;
    private String lastLoginDate;

    private List<String> currentListings;
    private List<String> previousListings;
    private int listerRating;

    private List<String> currentDeliveries;
    private List<String> previousDeliveries;
    private List<String> availableVehicles;
    private int driverRating;

    public User(){
        currentDeliveries = new ArrayList<String>();
        previousDeliveries  = new ArrayList<String>();

        currentListings = new ArrayList<String>();
        previousListings =  new ArrayList<String>();

        availableVehicles = new ArrayList<String>();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsDriver() {
        return isDriver;
    }

    public void setIsDriver(Boolean isDriver) {
        this.isDriver = isDriver;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public int getListerRating() {
        return listerRating;
    }

    public void setListerRating(int listerRating) {
        this.listerRating = listerRating;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    //**********************************************************************************************
    //Current Listings

    public List<String> getCurrentListings() {
        return currentListings;
    }

    public void setCurrentListings(List<String> currentListings) {
        this.currentListings = currentListings;
    }

    public List<String> getPreviousListings() {
        return previousListings;
    }

    public void setPreviousListings(List<String> previousListings) {
        this.previousListings = previousListings;
    }

    public void addNewListing(String deliveryId){
        this.currentListings.add(deliveryId);
    }

    public void finishListing(String deliveryId){
        this.currentDeliveries.remove(deliveryId);
        this.previousDeliveries.add(deliveryId);
    }

    //**********************************************************************************************
    //Deliveries
    public List<String> getCurrentDeliveries() {
        return currentDeliveries;
    }

    public void setCurrentDeliveries(List<String> currentDeliveries) {
        this.currentDeliveries = currentDeliveries;
    }

    public List<String> getPreviousDeliveries() {
        return previousDeliveries;
    }

    public void setPreviousDeliveries(List<String> previousDeliveries) {
        this.previousDeliveries = previousDeliveries;
    }

    public void acceptDelivery(String deliveryId){
        this.currentDeliveries.add(deliveryId);
    }

    public void finishDelivery(String deliveryId){
        this.currentDeliveries.remove(deliveryId);
        this.previousDeliveries.add(deliveryId);
    }

    //**********************************************************************************************
    //Available Vehicles
    public List<String> getAvailableVehicles() {
        return availableVehicles;
    }

    public void setAvailableVehicles(List<String> availableVehicles) {
        this.availableVehicles = availableVehicles;
    }
}
