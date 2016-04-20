package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.UrgentMessage;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class ContactUsActivity extends AppCompatActivity {

    Firebase mRef;

    List<String> userListingsAndDeliveries;
    List<String> corresponsingListingIds;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userListingsAndDeliveries = new ArrayList<String>();
        corresponsingListingIds = new ArrayList<String>();

        userListingsAndDeliveries.add("---No Listing---");
        corresponsingListingIds.add("");

        final Spinner spinner = (Spinner) findViewById(R.id.listings_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, userListingsAndDeliveries);
        spinner.setAdapter(adapter);

        mRef = new Firebase("https://emissary.firebaseio.com");
        userId = mRef.getAuth().getUid();

        final Firebase mRef = new Firebase("https://emissary.firebaseio.com/deliveries");
        Query queryRef = mRef.orderByChild("originalLister").equalTo(userId);


        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Delivery d = dataSnapshot.getValue(Delivery.class);
                userListingsAndDeliveries.add(d.getListingName());
                corresponsingListingIds.add(d.getID());
                adapter.notifyDataSetChanged();
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

        Query queryRef2 = mRef.orderByChild("driver").equalTo(userId);

        queryRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Delivery d = dataSnapshot.getValue(Delivery.class);
                userListingsAndDeliveries.add("[Driver] " + d.getListingName());
                corresponsingListingIds.add(d.getID());
                adapter.notifyDataSetChanged();
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

        final Button sendMessageButton = (Button) findViewById(R.id.send_request);
        final EditText problemDescription = (EditText) findViewById(R.id.problem_text);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageDescription = problemDescription.getText().toString();
                if (messageDescription == null || messageDescription.equals("")){
                    problemDescription.setError("Describe the issue");
                    problemDescription.requestFocus();
                }else if(messageDescription.toString().length() < Constants.MINIMUM_MESSAGE_REQUEST_LENGTH) {
                    problemDescription.setError("Please be detailed with your description");
                    problemDescription.requestFocus();
                }else{
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(ContactUsActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Submit urgent request");
                    builder.setMessage(getResources().getString(R.string.urgent_message_dialog_mesage));
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        UrgentMessage urgentMessage = new UrgentMessage();
                        urgentMessage.setUserId(userId);
                        urgentMessage.setMessage(messageDescription);
                        urgentMessage.setDeliveryId(corresponsingListingIds.get(spinner.getSelectedItemPosition()));

                        Firebase firebaseUser = (new Firebase("https://emissary.firebaseio.com")).child("urgent_messages");
                        Firebase newPostRef = firebaseUser.push();
                        newPostRef.setValue(urgentMessage, new Firebase.CompletionListener(){
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                Toast t = Toast.makeText(getApplicationContext(), "Request sucessfully sent!", Toast.LENGTH_SHORT);
                                t.show();
                                problemDescription.setText("");
                                spinner.setSelection(0);
                            }
                        });
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    AppCompatDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }
}