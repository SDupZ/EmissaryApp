package nz.emissary.emissaryapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 31/05/2016.
 */
public class Vehicle {

    private String uid;
    private int vehicleType;
    private String vehicleLicenseNumber;

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleLicenseNumber() {
        return vehicleLicenseNumber;
    }

    public void setVehicleLicenseNumber(String vehicleLicenseNumber) {
        this.vehicleLicenseNumber = vehicleLicenseNumber;
    }
}
