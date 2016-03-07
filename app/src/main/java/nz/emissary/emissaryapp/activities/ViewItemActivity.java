package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewItemActivity extends BaseActivity implements View.OnClickListener{

    private String itemId;
    private Firebase mRef;
    private Firebase currentFirebaseDelivery;
    private Delivery currentDelivery;

    private Firebase currentFirebaseUser;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


            mRef = new Firebase("https://emissary.firebaseio.com");
            currentFirebaseDelivery = new Firebase("https://emissary.firebaseio.com/deliveries/" + itemId);

            currentFirebaseDelivery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentDelivery = dataSnapshot.getValue(Delivery.class);

                    notesView.setText(currentDelivery.getNotes());
                    nameView.setText(currentDelivery.getListingName());
                    pickupLocationView.setText(currentDelivery.getPickupLocation());
                    dropOffLocationView.setText(currentDelivery.getDropoffLocation());
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
    protected int getLayoutResource() {
        return R.layout.activity_view_item;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ViewItemActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Driver Confirmation");
        builder.setMessage(getResources().getString(R.string.confirm_dialog));
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentDelivery.setDriver(mRef.getAuth().getUid());
                currentDelivery.setHasDriver(true);

                currentFirebaseDelivery.setValue(currentDelivery);

                currentUser.acceptDelivery(itemId);
                currentFirebaseUser.setValue(currentUser);
            }
        });
        builder.setNegativeButton("Cancel", null);

        AppCompatDialog dialog = builder.create();
        dialog.show();
    }

}
