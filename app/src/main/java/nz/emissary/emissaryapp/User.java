package nz.emissary.emissaryapp;

import java.util.ArrayList;

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

    private ArrayList<String> currentListings;
    private ArrayList<String> previousListings;
    private int listerRating;

    private ArrayList<String> currentDeliveries;
    private ArrayList<String> previousDeliveries;
    private ArrayList<String> availableVehicles;
    private int driverRating;

    private long createdAt;

    public User(){}

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

    public ArrayList<String> getCurrentListings() {
        return currentListings;
    }

    public void setCurrentListings(ArrayList<String> currentListings) {
        this.currentListings = currentListings;
    }

    public ArrayList<String> getPreviousListings() {
        return previousListings;
    }

    public void setPreviousListings(ArrayList<String> previousListings) {
        this.previousListings = previousListings;
    }

    public int getListerRating() {
        return listerRating;
    }

    public void setListerRating(int listerRating) {
        this.listerRating = listerRating;
    }

    public ArrayList<String> getCurrentDeliveries() {
        return currentDeliveries;
    }

    public void setCurrentDeliveries(ArrayList<String> currentDeliveries) {
        this.currentDeliveries = currentDeliveries;
    }

    public ArrayList<String> getPreviousDeliveries() {
        return previousDeliveries;
    }

    public void setPreviousDeliveries(ArrayList<String> previousDeliveries) {
        this.previousDeliveries = previousDeliveries;
    }

    public ArrayList<String> getAvailableVehicles() {
        return availableVehicles;
    }

    public void setAvailableVehicles(ArrayList<String> availableVehicles) {
        this.availableVehicles = availableVehicles;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
