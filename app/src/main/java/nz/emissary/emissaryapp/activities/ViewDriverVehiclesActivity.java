package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Feedback;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.Vehicle;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewDriverVehiclesActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;

    private Button addVehicleButton;

    private int vehicleType = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_driver_vehicles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Firebase mRef = new Firebase(Constants.FIREBASE_USERS_VEHICLES);
        final Query queryRef = mRef.child(mRef.getAuth().getUid());

        final FirebaseRecyclerAdapter<Vehicle, ViewHolder> adapter =
                new FirebaseRecyclerAdapter<Vehicle, ViewHolder>(Vehicle.class, R.layout.listview_vehicles, ViewHolder.class, queryRef){

                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Vehicle vehicle, final int i) {
                        int vehicleType = vehicle.getVehicleType();
                        String plateNumber = vehicle.getVehicleLicenseNumber();

                        viewHolder.licenseNumberView.setText(plateNumber);
                        viewHolder.setVehicleType(vehicleType);
                    }


                };
        mRecyclerView.setAdapter(adapter);

        addVehicleButton = (Button) findViewById(R.id.add_vehicle);
        addVehicleButton.setOnClickListener(new View.OnClickListener() {
            ImageView carView;
            ImageView vanView;
            ImageView motorcycleView;

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ViewDriverVehiclesActivity.this, R.style.MyAlertDialogStyle2);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.pager_driver_account_add_vehicle, null);
                builder.setView(dialogView);

                final EditText licenseView;
                licenseView = (EditText) dialogView.findViewById(R.id.vehicle_license);
                carView = (ImageView) dialogView.findViewById(R.id.vehicle_car);
                vanView = (ImageView) dialogView.findViewById(R.id.vehicle_van);
                motorcycleView = (ImageView) dialogView.findViewById(R.id.vehicle_motorcycle);

                View.OnClickListener t = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setColorOfImage(v.getId());
                    }
                };

                carView.setOnClickListener(t);
                vanView.setOnClickListener(t);
                motorcycleView.setOnClickListener(t);

                builder.setPositiveButton("Add Vehicle", null);
                builder.setNegativeButton("Cancel", null);

                final AlertDialog tempDialog = builder.create();

                tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = tempDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                boolean error = false;
                                
                                String licensePlate = licenseView.getText().toString().trim();
                                if (vehicleType == -1){
                                    error = true;
                                    Toast.makeText(ViewDriverVehiclesActivity.this, "Choose a vehicle type", Toast.LENGTH_SHORT).show();
                                }else if (licensePlate == ""){
                                    error = true;
                                    licenseView.setError("This value is required");
                                }

                                if (!error){
                                    final Firebase mRef = new Firebase(Constants.FIREBASE_USERS_VEHICLES);
                                    final Firebase firebaseVehicles = mRef.child(mRef.getAuth().getUid());

                                    Vehicle vehicle = new Vehicle();
                                    vehicle.setVehicleType(vehicleType);
                                    vehicle.setVehicleLicenseNumber(licensePlate);

                                    Firebase newPostRef = firebaseVehicles.push();
                                    newPostRef.setValue(vehicle, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            tempDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                AppCompatDialog dialog  = tempDialog;
                dialog.show();


            }
            public void setColorOfImage(int id) {
                if (id == R.id.vehicle_car) {
                    vehicleType = Constants.VEHICLE_CAR;
                    carView.setColorFilter(null);
                    vanView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
                    motorcycleView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
                } else if (id == R.id.vehicle_motorcycle) {
                    vehicleType = Constants.VEHICLE_MOTORBIKE;
                    carView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
                    vanView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
                    motorcycleView.setColorFilter(null);
                } else if (id == R.id.vehicle_van) {
                    vehicleType = Constants.VEHICLE_VAN;
                    carView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
                    vanView.setColorFilter(null);
                    motorcycleView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
                }
            }

        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ImageView vehicleImageView;
        public TextView licenseNumberView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            licenseNumberView = (TextView) v.findViewById(R.id.license_number);
            vehicleImageView = (ImageView) v.findViewById(R.id.vehicle_image);
        }

        public void setVehicleType(int vehicleType){
            if (vehicleType == Constants.VEHICLE_CAR) {
                vehicleImageView.setImageResource(R.drawable.vector_car);
            } else if (vehicleType == Constants.VEHICLE_MOTORBIKE) {
                vehicleImageView.setImageResource(R.drawable.vector_motorcycle);
            } else if (vehicleType == Constants.VEHICLE_VAN) {
                vehicleImageView.setImageResource(R.drawable.vector_van);
            }
        }
    }

}