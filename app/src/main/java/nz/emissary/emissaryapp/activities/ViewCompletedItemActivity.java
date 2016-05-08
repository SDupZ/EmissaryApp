package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.Feedback;
import nz.emissary.emissaryapp.MessagesAdapter;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.SimpleMessage;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewCompletedItemActivity extends AppCompatActivity{

    private String itemId;
    private Firebase mRef;
    private Firebase currentFirebaseDelivery;
    private Delivery currentDelivery;

    private Firebase currentFirebaseUser;
    private User currentUser;

    ArrayList<SimpleMessage> messagesList;
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

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

            messagesList = new ArrayList<SimpleMessage>();

            final TextView pickupLocationView = ((TextView)findViewById(R.id.item_pickup_location));
            final TextView dropOffLocationView = ((TextView)findViewById(R.id.item_drop_off_location));
            final TextView notesView = ((TextView)findViewById(R.id.item_notes));
            final TextView dropoffTimeView = ((TextView)findViewById(R.id.item_dropoff_time));
            final TextView pickupTimeView = ((TextView)findViewById(R.id.item_pickup_time));

            final TextView itemStatusView = ((TextView) findViewById(R.id.item_status_description));
            final CardView deliveryStatusCard = ((CardView) findViewById(R.id.delivery_status_card));

            final TextView messageTitleView = ((TextView) findViewById(R.id.item_driver_message_title));

            final ListView messageListView = (ListView) findViewById(R.id.message_list_view);

            messageListView.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            final MessagesAdapter arrayAdapter = new MessagesAdapter(this ,messagesList);

            messageListView.setAdapter(arrayAdapter);

            final TextView feedbackLinkView = ((TextView) findViewById(R.id.place_feedback_link));

            //----------------Load the object from the local database---------------
            itemId = intent.getStringExtra("object_id");

            mRef = new Firebase(Constants.FIREBASE_BASE);
            currentFirebaseDelivery = new Firebase(Constants.FIREBASE_DELIVERIES_UNLISTED).child(itemId);

            Firebase firebaseMessages = new Firebase(Constants.FIREBASE_MESSAGES).child(itemId);
            firebaseMessages.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                    messageTitleView.setVisibility(View.VISIBLE);
                    messageListView.setVisibility(View.VISIBLE);
                    SimpleMessage newMessage = snapshot.getValue(SimpleMessage.class);
                    messagesList.add(newMessage);
                    arrayAdapter.notifyDataSetChanged();
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });


            currentFirebaseDelivery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentDelivery = dataSnapshot.getValue(Delivery.class);

                    if (currentDelivery != null) {
                        toolbar.setTitle(currentDelivery.getListingName());
                        notesView.setText(currentDelivery.getNotes());
                        pickupLocationView.setText(currentDelivery.getPickupLocation());
                        dropOffLocationView.setText(currentDelivery.getDropoffLocation());

                        dropoffTimeView.setText(Constants.getFullDateTimeString(currentDelivery.getDropoffTime()));
                        pickupTimeView.setText(Constants.getFullDateTimeString(currentDelivery.getPickupTime()));

                        int currentStatus = currentDelivery.getStatus();

                        itemStatusView.setText(Constants.getStatusDescription(currentStatus, getApplicationContext(), false));

                        Drawable cardBackground = Constants.getStatusBackgroundDrawable(currentStatus, getApplicationContext(), false);
                        if (cardBackground != null)
                            deliveryStatusCard.setBackground(cardBackground);

                        if (currentStatus == Constants.STATUS_DELIVERED_NO_FB || currentStatus == Constants.STATUS_DELIVERED_L_FB) {
                            feedbackLinkView.setVisibility(View.VISIBLE);
                        } else {
                            feedbackLinkView.setVisibility(View.GONE);
                        }
                    }else{
                        finish();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            currentFirebaseUser = mRef.child(Constants.FIREBASE_USERS_BASE_CHILD).child(mRef.getAuth().getUid());
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
