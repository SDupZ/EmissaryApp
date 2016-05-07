package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class EditItemActivity extends AppCompatActivity implements View.OnClickListener{

    private String itemId;
    private Firebase mRef;
    private Firebase currentFirebaseDelivery;
    private Delivery currentDelivery;

    private Firebase currentFirebaseDriver;
    private User currentDriver;

    private Firebase currentFirebaseUser;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("object_id")) {

            final TextView nameView = ((TextView)findViewById(R.id.item_name));
            final TextView pickupLocationView = ((TextView)findViewById(R.id.item_pickup_location));
            final TextView dropOffLocationView = ((TextView)findViewById(R.id.item_drop_off_location));
            final TextView notesView = ((TextView)findViewById(R.id.item_notes));

            final Button acceptDeliveryButton = (Button) findViewById(R.id.accept_delivery);


            //----------------Load the object from the local database---------------
            itemId = intent.getStringExtra("object_id");

            //----------------Accept a delivery---------------
            acceptDeliveryButton.setOnClickListener(this);

            mRef = new Firebase(Constants.FIREBASE_BASE);
            currentFirebaseDelivery = new Firebase(Constants.FIREBASE_DELIVERIES_ACTIVE).child(itemId);

            currentFirebaseDelivery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentDelivery = dataSnapshot.getValue(Delivery.class);
                    notesView.setText(currentDelivery.getNotes());
                    nameView.setText(currentDelivery.getListingName());
                    pickupLocationView.setText(currentDelivery.getPickupLocation());
                    dropOffLocationView.setText(currentDelivery.getDropoffLocation());

                    if (!currentDelivery.getOriginalLister().equals(mRef.getAuth().getUid())){
                        acceptDeliveryButton.setVisibility(View.VISIBLE);
                    }

                    //A driver has accepted this job
                    if (currentDelivery.getDriver() !=  null ){
                        currentFirebaseDriver = mRef.child("users").child(currentDelivery.getDriver());
                        currentFirebaseDriver.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentDriver = dataSnapshot.getValue(User.class);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            currentFirebaseUser = mRef.child("users").child(mRef.getAuth().getUid());
            currentFirebaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(EditItemActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Remove this listing");
        builder.setMessage(getResources().getString(R.string.cancel_dialog));
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentDelivery.setStatus(Constants.STATUS_CANCELLED);

                currentFirebaseDelivery.setValue(currentDelivery);
                currentUser.finishListing(itemId);
                currentFirebaseUser.setValue(currentUser);

                if (currentDelivery.getDriver() != null) {
                    currentDriver.finishDelivery(itemId);
                    currentFirebaseDriver.setValue(currentDriver);
                }
            }
        });
        builder.setNegativeButton("Go back to safety", null);

        AppCompatDialog dialog = builder.create();
        dialog.show();
    }

}
