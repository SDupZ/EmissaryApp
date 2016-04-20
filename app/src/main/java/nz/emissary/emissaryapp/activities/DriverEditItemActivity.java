package nz.emissary.emissaryapp.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import nz.emissary.emissaryapp.Feedback;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class DriverEditItemActivity extends AppCompatActivity{

    private String itemId;
    private Firebase mRef;
    private Firebase currentFirebaseDelivery;
    private Delivery currentDelivery;

    private Firebase currentFirebaseDriver;
    private User currentDriver;

    private Firebase currentFirebaseUser;
    private User currentUser;

    private String prevMsg;

    private int deliveryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_edit);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prevMsg = "";
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

            final TextView itemStatusView = ((TextView) findViewById(R.id.item_status_description));
            final CardView deliveryStatusCard = ((CardView) findViewById(R.id.delivery_status_card));

            final TextView driverMessageView = ((TextView) findViewById(R.id.item_driver_message));

            final Button driverUpdateStatusButton = (Button) findViewById(R.id.driver_update_status_button);

            final EditText messageFromDriverView = (EditText) findViewById(R.id.message_for_lister);
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            final TextView feedbackLinkView = ((TextView) findViewById(R.id.place_feedback_link));

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

            feedbackLinkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(DriverEditItemActivity.this, R.style.MyAlertDialogStyle2);

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
                                    driverFeedback.setUserId(currentDelivery.getOriginalLister());
                                    driverFeedback.setDeliveryId(itemId);
                                    driverFeedback.setRating(rating);
                                    driverFeedback.setFeedbackMessage(message);
                                    driverFeedback.setFeedbackPosterId(mRef.getAuth().getUid());
                                    driverFeedback.setFeedbackIsForDriver(true);

                                    Firebase firebaseUser = (new Firebase("https://emissary.firebaseio.com")).child("feedback");
                                    Firebase newPostRef = firebaseUser.push();
                                    newPostRef.setValue(driverFeedback, new Firebase.CompletionListener(){
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            Toast t = Toast.makeText(getApplicationContext(), "Feedback sucessfully placed!", Toast.LENGTH_SHORT);
                                            t.show();
                                            if (currentDelivery.getStatus() == Constants.STATUS_DELIVERED_D_FB){
                                                currentDelivery.setStatus(Constants.STATUS_COMPLETE);
                                            }else if (currentDelivery.getStatus() == Constants.STATUS_DELIVERED_NO_FB) {
                                                currentDelivery.setStatus(Constants.STATUS_DELIVERED_L_FB);
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

            //----------------Load the object from the local database---------------
            itemId = intent.getStringExtra("object_id");

            mRef = new Firebase("https://emissary.firebaseio.com");
            currentFirebaseDelivery = new Firebase("https://emissary.firebaseio.com/deliveries/" + itemId);

            messageFromDriverView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)}));
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_send));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            currentFirebaseDelivery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentDelivery = dataSnapshot.getValue(Delivery.class);
                    deliveryStatus = currentDelivery.getStatus();

                    if (!toolbar.getTitle().equals("Your listing") && prevMsg != currentDelivery.getMessageFromDriver()){
                        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{ContextCompat.getColor(getApplicationContext(), R.color.colorFabDone)}));
                        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_done));
                    }

                    ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(currentDelivery.getListingName());
                    toolbar.setTitle(currentDelivery.getListingName());

                    notesView.setText(currentDelivery.getNotes());
                    pickupLocationView.setText(currentDelivery.getPickupLocation());
                    dropOffLocationView.setText(currentDelivery.getDropoffLocation());

                    dropoffTimeView.setText(Constants.convertTime( Long.parseLong(currentDelivery.getDropoffTime())));
                    pickupTimeView.setText(Constants.convertTime( Long.parseLong(currentDelivery.getPickupTime())));

                    itemStatusView.setText(Constants.getStatusDescription(deliveryStatus, getApplicationContext(), true));

                    Drawable cardBackground = Constants.getStatusBackgroundDrawable(deliveryStatus, getApplicationContext(), true);
                    if (cardBackground != null)
                        deliveryStatusCard.setBackground(cardBackground);

                    driverUpdateStatusButton.setText(Constants.getUpdateButtonText(deliveryStatus, getApplicationContext()));
                    driverUpdateStatusButton.setEnabled(true);
                    driverMessageView.setText(currentDelivery.getMessageFromDriver());

                    if (deliveryStatus == Constants.STATUS_DELIVERED_NO_FB || deliveryStatus == Constants.STATUS_DELIVERED_D_FB){
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

            //----------------Accept a delivery---------------
            driverUpdateStatusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(DriverEditItemActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(R.string.update_status_dialog_title);

                    String message =
                    Constants.getNextStatusMessageForLister(deliveryStatus, getApplicationContext());

                    builder.setMessage(message);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            driverUpdateStatusButton.setEnabled(false);
                            currentDelivery.setStatus(Constants.getNextStatus(deliveryStatus));
                            currentFirebaseDelivery.setValue(currentDelivery);
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    AppCompatDialog dialog = builder.create();
                    dialog.show();
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newMsg = messageFromDriverView.getText().toString();
                    prevMsg = newMsg;
                    currentDelivery.setMessageFromDriver(newMsg);
                    currentFirebaseDelivery.setValue(currentDelivery);
                    messageFromDriverView.setText("");

                }
            });

        }
    }


}
