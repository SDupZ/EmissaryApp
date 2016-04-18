package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
public class ViewItemStatusActivity extends AppCompatActivity{

    private String itemId;
    private Firebase mRef;
    private Firebase currentFirebaseDelivery;
    private Delivery currentDelivery;

    private Firebase currentFirebaseUser;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_item_status);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("object_id")) {

            final TextView pickupLocationView = ((TextView)findViewById(R.id.item_pickup_location));
            final TextView dropOffLocationView = ((TextView)findViewById(R.id.item_drop_off_location));
            final TextView notesView = ((TextView)findViewById(R.id.item_notes));
            final TextView dropoffTimeView = ((TextView)findViewById(R.id.item_dropoff_time));
            final TextView pickupTimeView = ((TextView)findViewById(R.id.item_pickup_time));

            final TextView itemStatusView = ((TextView) findViewById(R.id.item_status_description));
            final CardView deliveryStatusCard = ((CardView) findViewById(R.id.delivery_status_card));

            final TextView messageTitleView = ((TextView) findViewById(R.id.item_driver_message_title));
            final TextView messageView = ((TextView) findViewById(R.id.item_driver_message));

            //----------------Load the object from the local database---------------
            itemId = intent.getStringExtra("object_id");

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

                    dropoffTimeView.setText(Constants.convertTime( Long.parseLong(currentDelivery.getDropoffTime())));
                    pickupTimeView.setText(Constants.convertTime( Long.parseLong(currentDelivery.getPickupTime())));

                    itemStatusView.setText(Constants.getStatusDescription(currentDelivery.getStatus(), getApplicationContext(), false));

                    Drawable cardBackground = Constants.getStatusBackgroundDrawable(currentDelivery.getStatus(), getApplicationContext());
                    if (cardBackground != null)
                        deliveryStatusCard.setBackground(cardBackground);

                    String messageFromDriver = currentDelivery.getMessageFromDriver();
                    if (messageFromDriver != null && messageFromDriver != ""){
                        messageView.setText(messageFromDriver);
                        messageTitleView.setVisibility(View.VISIBLE);
                        messageView.setVisibility(View.VISIBLE);
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
}
