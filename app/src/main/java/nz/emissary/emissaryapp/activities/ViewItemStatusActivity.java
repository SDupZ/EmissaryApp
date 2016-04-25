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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.Feedback;
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

            final TextView pickupLocationView = ((TextView)findViewById(R.id.item_pickup_location));
            final TextView dropOffLocationView = ((TextView)findViewById(R.id.item_drop_off_location));
            final TextView notesView = ((TextView)findViewById(R.id.item_notes));
            final TextView dropoffTimeView = ((TextView)findViewById(R.id.item_dropoff_time));
            final TextView pickupTimeView = ((TextView)findViewById(R.id.item_pickup_time));

            final TextView itemStatusView = ((TextView) findViewById(R.id.item_status_description));
            final CardView deliveryStatusCard = ((CardView) findViewById(R.id.delivery_status_card));

            final TextView messageTitleView = ((TextView) findViewById(R.id.item_driver_message_title));
            final TextView messageView = ((TextView) findViewById(R.id.item_driver_message));

            final TextView feedbackLinkView = ((TextView) findViewById(R.id.place_feedback_link));

            //----------------Load the object from the local database---------------
            itemId = intent.getStringExtra("object_id");

            mRef = new Firebase("https://emissary.firebaseio.com");
            currentFirebaseDelivery = new Firebase("https://emissary.firebaseio.com/deliveries/" + itemId);

            feedbackLinkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(ViewItemStatusActivity.this, R.style.MyAlertDialogStyle2);

                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_place_feedback_for_driver, null);
                    builder.setView(dialogView);

                    final RatingBar ratingView = (RatingBar) dialogView.findViewById(R.id.rating);
                    final EditText feedbackTextView = (EditText) dialogView.findViewById(R.id.feedback_text);

                    builder.setTitle(R.string.dialog_place_feedback_for_driver_title);
                    builder.setPositiveButton("Submit", null);
                    builder.setNegativeButton("Cancel", null);

                    final AlertDialog tempDialog = builder.create();

                    tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button b = tempDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    float rating = ratingView.getRating();
                                    String message = feedbackTextView.getText().toString();

                                    Feedback driverFeedback = new Feedback();
                                    driverFeedback.setUserId(currentDelivery.getDriver());
                                    driverFeedback.setDeliveryId(itemId);
                                    driverFeedback.setRating(rating);
                                    driverFeedback.setFeedbackMessage(message);
                                    driverFeedback.setFeedbackPosterId(mRef.getAuth().getUid());
                                    driverFeedback.setFeedbackIsForDriver(true);

                                    Date d = new Date();
                                    driverFeedback.setFeedbackPostTime(d.getTime());

                                    Firebase firebaseUser = (new Firebase("https://emissary.firebaseio.com")).child("feedback");
                                    Firebase newPostRef = firebaseUser.push();
                                    newPostRef.setValue(driverFeedback, new Firebase.CompletionListener(){
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            Toast t = Toast.makeText(getApplicationContext(), "Feedback sucessfully placed!", Toast.LENGTH_SHORT);
                                            t.show();

                                            if (currentDelivery.getStatus() == Constants.STATUS_DELIVERED_L_FB){
                                                currentDelivery.setStatus(Constants.STATUS_COMPLETE);
                                            }else if (currentDelivery.getStatus() == Constants.STATUS_DELIVERED_NO_FB) {
                                                currentDelivery.setStatus(Constants.STATUS_DELIVERED_D_FB);
                                            }

                                            currentFirebaseDelivery.setValue(currentDelivery);

                                        }
                                    });
                                    tempDialog.dismiss();
                                }
                            });
                        }
                    });

                    AppCompatDialog dialog  = tempDialog;
                    dialog.show();
                }
            });

            currentFirebaseDelivery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentDelivery = dataSnapshot.getValue(Delivery.class);

                    toolbar.setTitle(currentDelivery.getListingName());
                    notesView.setText(currentDelivery.getNotes());
                    pickupLocationView.setText(currentDelivery.getPickupLocation());
                    dropOffLocationView.setText(currentDelivery.getDropoffLocation());

                    dropoffTimeView.setText(Constants.convertTime(currentDelivery.getDropoffTime()));
                    pickupTimeView.setText(Constants.convertTime(currentDelivery.getPickupTime()));

                    int currentStatus = currentDelivery.getStatus();

                    itemStatusView.setText(Constants.getStatusDescription(currentStatus, getApplicationContext(), false));

                    Drawable cardBackground = Constants.getStatusBackgroundDrawable(currentStatus, getApplicationContext(), false);
                    if (cardBackground != null)
                        deliveryStatusCard.setBackground(cardBackground);

                    String messageFromDriver = currentDelivery.getMessageFromDriver();
                    if (messageFromDriver != null && messageFromDriver != ""){
                        messageView.setText(messageFromDriver);
                        messageTitleView.setVisibility(View.VISIBLE);
                        messageView.setVisibility(View.VISIBLE);
                    }
                    if (currentStatus == Constants.STATUS_DELIVERED_NO_FB || currentStatus == Constants.STATUS_DELIVERED_L_FB){
                        feedbackLinkView.setVisibility(View.VISIBLE);
                    }else{
                        feedbackLinkView.setVisibility(View.GONE);
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
