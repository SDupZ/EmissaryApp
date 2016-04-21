package nz.emissary.emissaryapp.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewItemActivity extends AppCompatActivity implements View.OnClickListener{

    private String itemId;
    private Firebase mRef;
    private Firebase currentFirebaseDelivery;
    private Delivery currentDelivery;

    private Firebase currentFirebaseUser;
    private User currentUser;

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_item);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("object_id")) {

            final TextView nameView = ((TextView)findViewById(R.id.item_name));
            final TextView pickupLocationView = ((TextView)findViewById(R.id.item_pickup_location));
            final TextView dropOffLocationView = ((TextView)findViewById(R.id.item_drop_off_location));
            final TextView notesView = ((TextView)findViewById(R.id.item_notes));
            final TextView dropoffTimeView = ((TextView)findViewById(R.id.item_dropoff_time));
            final TextView pickupTimeView = ((TextView)findViewById(R.id.item_pickup_time));

            final Button acceptDeliveryButton = (Button) findViewById(R.id.accept_delivery);

            final ImageView copyPickupToClipboardView = ((ImageView) findViewById(R.id.copy_pickup_to_clipboard));
            final ImageView copyDropoffToClipboardView = ((ImageView) findViewById(R.id.copy_dropoff_to_clipboard));

            copyDropoffToClipboardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Emissary Location", dropOffLocationView.getText().toString());
                    clipboard.setPrimaryClip(clip);

                    Toast t = Toast.makeText(getApplicationContext(), "Dropoff location copied to clipboard!", Toast.LENGTH_SHORT);
                    t.show();
                }
            });

            copyPickupToClipboardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Emissary Location", pickupLocationView.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast t = Toast.makeText(getApplicationContext(), "Pickup location copied to clipboard!", Toast.LENGTH_SHORT);
                    t.show();
                }
            });

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
                    //nameView.setText(currentDelivery.getListingName());
                    ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(currentDelivery.getListingName());
                    toolbar.setTitle(currentDelivery.getListingName());
                    notesView.setText(currentDelivery.getNotes());
                    pickupLocationView.setText(currentDelivery.getPickupLocation());
                    dropOffLocationView.setText(currentDelivery.getDropoffLocation());

                    dropoffTimeView.setText(Constants.convertTime(currentDelivery.getDropoffTime()));
                    pickupTimeView.setText(Constants.convertTime(currentDelivery.getPickupTime()));

                    if (!currentDelivery.getOriginalLister().equals(mRef.getAuth().getUid())){
                        acceptDeliveryButton.setVisibility(View.VISIBLE);
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
                new AlertDialog.Builder(ViewItemActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Driver Confirmation");
        builder.setMessage(getResources().getString(R.string.confirm_dialog));
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentDelivery.setDriver(mRef.getAuth().getUid());
                currentDelivery.setStatus(Constants.STATUS_ACCEPTED);

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
