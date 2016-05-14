package nz.emissary.emissaryapp;

import com.firebase.client.DataSnapshot;

import java.util.Comparator;

/**
 * Created by Simon on 3/03/2016.
 */
public class Delivery {
    private String uid;
    private String originalLister;
    private String listingName;
    private String notes;

    private String pickupLocation;
    private String pickupLocationShort;

    private String dropoffLocation;
    private String dropoffLocationShort;

    private String pickupTime;
    private String dropoffTime;

    private String messageFromDriver;

    private double distance;
    private double pickupLat;
    private double pickupLong;
    private double dropoffLat;
    private double dropoffLong;

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

    public String getPickupLocationShort() {
        return pickupLocationShort;
    }

    public void setPickupLocationShort(String pickupLocationShort) {
        this.pickupLocationShort = pickupLocationShort;
    }

    public String getDropoffLocationShort() {
        return dropoffLocationShort;
    }

    public void setDropoffLocationShort(String dropoffLocationShort) {
        this.dropoffLocationShort = dropoffLocationShort;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLong() {
        return pickupLong;
    }

    public void setPickupLong(double pickupLong) {
        this.pickupLong = pickupLong;
    }

    public double getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(double dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public double getDropoffLong() {
        return dropoffLong;
    }

    public void setDropoffLong(double dropoffLong) {
        this.dropoffLong = dropoffLong;
    }

    public static class AlphabeticalComparator implements Comparator<DataSnapshot> {
        @Override
        public int compare(DataSnapshot lhsSnapshot, DataSnapshot rhsSnapshot) {
            Delivery lhs = lhsSnapshot.getValue(Delivery.class);
            Delivery rhs = rhsSnapshot.getValue(Delivery.class);
            return lhs.getListingName().toLowerCase().compareTo(rhs.getListingName().toLowerCase());
        }
    }

    public static class TotalDistanceComparator implements Comparator<DataSnapshot> {
        @Override
        public int compare(DataSnapshot lhsSnapshot, DataSnapshot rhsSnapshot) {
            Delivery lhs = lhsSnapshot.getValue(Delivery.class);
            Delivery rhs = rhsSnapshot.getValue(Delivery.class);
            double lhsDistance = lhs.getDistance();
            double rhsDistance = rhs.getDistance();

            if (lhsDistance == rhsDistance){
                return 0;
            }else{
                return lhsDistance < rhsDistance ? -1:1;
            }
        }
    }

    public static class LatestComparator implements Comparator<DataSnapshot> {
        @Override
        public int compare(DataSnapshot lhsSnapshot, DataSnapshot rhsSnapshot) {
            Delivery lhs = lhsSnapshot.getValue(Delivery.class);
            Delivery rhs = rhsSnapshot.getValue(Delivery.class);

            double lhsCreatedAt = lhs.getCreatedAt();
            double rhsCreatedAt= rhs.getCreatedAt();

            if (lhsCreatedAt == rhsCreatedAt){
                return 0;
            }else{
                return lhsCreatedAt < rhsCreatedAt ? -1:1;
            }
        }
    }

    public static class PickupTimeComparator implements Comparator<DataSnapshot> {
        @Override
        public int compare(DataSnapshot lhsSnapshot, DataSnapshot rhsSnapshot) {
            Delivery lhs = lhsSnapshot.getValue(Delivery.class);
            Delivery rhs = rhsSnapshot.getValue(Delivery.class);

            String lhsPickupTime = lhs.getPickupTime();
            String rhsPickupTime= rhs.getPickupTime();

            Long lhsTime = 0l;
            Long rhsTime = 0l;

            String[] resultArray = lhsPickupTime.split(Constants.TIME_TOKEN);

            if (resultArray.length == 1 && resultArray[0].equals(Constants.TIME_ASAP)){
                lhsTime = 0l;
            }else if(resultArray.length == 2 && resultArray[0].equals(Constants.TIME_SPECIFIC)){
                try {
                    Long time = Long.parseLong(resultArray[1]);
                    lhsTime = time;
                }catch (NumberFormatException e){
                }
            }else if(resultArray.length == 3 && resultArray[0].equals(Constants.TIME_RANGE)){
                try {
                    Long timeBegin = Long.parseLong(resultArray[1]);
                    lhsTime = timeBegin;
                }catch (NumberFormatException e){
                }
            }

            resultArray = rhsPickupTime.split(Constants.TIME_TOKEN);

            if (resultArray.length == 1 && resultArray[0].equals(Constants.TIME_ASAP)){
                rhsTime = 0l;
            }else if(resultArray.length == 2 && resultArray[0].equals(Constants.TIME_SPECIFIC)){
                try {
                    Long time = Long.parseLong(resultArray[1]);
                    rhsTime = time;
                }catch (NumberFormatException e){
                }
            }else if(resultArray.length == 3 && resultArray[0].equals(Constants.TIME_RANGE)){
                try {
                    Long timeBegin = Long.parseLong(resultArray[1]);
                    rhsTime = timeBegin;
                }catch (NumberFormatException e){
                }
            }

            if (lhsTime == rhsTime){
                return 0;
            }else{
                return lhsTime < rhsTime ? -1:1;
            }
        }
    }
}
